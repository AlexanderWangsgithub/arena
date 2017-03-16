package com.github.alexanderwangsgithub.arena.core.common.bean;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alexanderwangsgithub.arena.core.common.reflect.AInvoke;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Primitives;
import com.google.common.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public final class ABean {
    private ABean() {}

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Set<Class<?>> UNSUPPORTED_TYPES = ImmutableSet.of(
            byte.class, char.class, void.class,
            Byte.class, Character.class, Void.class);

    private static final Set<Class<?>> BASE_TYPES = ImmutableSet.of(
            boolean.class, short.class, int.class, long.class, float.class, double.class,
            Boolean.class, Short.class, Integer.class, Long.class, Float.class, Double.class);

    private static final Set<Class<?>> EXTRA_TYPES = ImmutableSet.of(
            String.class, Class.class,
            LocalDate.class, LocalTime.class, LocalDateTime.class);

    private static final Set<Class<?>> CONTAINER_TYPES = ImmutableSet.of(
            List.class, Set.class, Map.class);

    private static boolean isUnsupportedType(Class<?> type) {
        return UNSUPPORTED_TYPES.contains(type);
    }

    private static boolean isBaseType(Class<?> type) {
        return BASE_TYPES.contains(type);
    }

    private static boolean isExtraType(Class<?> type) {
        return EXTRA_TYPES.contains(type);
    }

    private static boolean isContainerType(Class<?> type) {
        for (Class<?> containerClass : CONTAINER_TYPES) {
            //check whether containerClass is type's interface or superclass
            if (containerClass.isAssignableFrom(type)) {
                checkArgument(containerClass == type, "Arena error: Please use [%s] instead of [%s].", containerClass, type);
                return true;
            }
        }
        return false;
    }

    private static boolean isEnumType(Class<?> type) {
        return type.isEnum();
    }


    private static Function directAssign = Function.identity();
    private static Map<Class<?>, Map<Class<?>, Function>> forceCastFuncMap = Maps.newHashMap();
    static {
        forceCastFuncMap.put(Short.class, Maps.newHashMap());
        forceCastFuncMap.get(Short.class).put(Integer.class, source -> (int)(Short)source);
        forceCastFuncMap.get(Short.class).put(Long.class, source -> (long)(Short)source);

        forceCastFuncMap.put(Integer.class, Maps.newHashMap());
        forceCastFuncMap.get(Integer.class).put(Long.class, source -> (long)(Integer)source);

        //if source>Int.Max : ArithmeticException("integer overflow")
        forceCastFuncMap.put(Long.class, Maps.newHashMap());
        forceCastFuncMap.get(Long.class).put(Integer.class, source -> Math.toIntExact((Long)source));

        forceCastFuncMap.put(Float.class, Maps.newHashMap());
        forceCastFuncMap.get(Float.class).put(Double.class, source -> Double.parseDouble(source.toString()));
    }

    /**先全部装箱，然后判断直接赋值还是强制类型转换*/
    private static Function getBaseTypeCaster(Class<?> sourceType, Class<?> targetType) {
        sourceType = Primitives.wrap(sourceType);
        targetType = Primitives.wrap(targetType);

        if (sourceType == targetType) {
            return directAssign;
        }
        if (forceCastFuncMap.containsKey(sourceType)) {
            return forceCastFuncMap.get(sourceType).get(targetType);
        }
        return null;
    }


    @Data
    @AllArgsConstructor
    private static class ReadWrite {
        private boolean canBeDeepened;
        private JavaType sourceType;
        private Method sourceReadMethod;
        private Method sourceWriteMethod;
        private JavaType targetType;
        private Method targetReadMethod;
        private Method targetWriteMethod;
    }

    private static final Map<Class<?>, Map<Class<?>, Set<ReadWrite>>> readWritesRegistry = Maps.newHashMap();

    private static boolean hasRegistered(Class<?> sourceType, Class<?> targetType) {
        return readWritesRegistry.containsKey(sourceType) &&
                readWritesRegistry.get(sourceType).containsKey(targetType);
    }

    private static synchronized void registerReadWrites(Class<?> sourceType, Class<?> targetType) {
        if (hasRegistered(sourceType, targetType)) {
            return;
        }

        if (!readWritesRegistry.containsKey(sourceType)) {
            readWritesRegistry.put(sourceType, Maps.newHashMap());
        }

        Map<String, PropertyDescriptor> sourceDescriptorMap;
        Map<String, PropertyDescriptor> targetDescriptorMap;
        try {
            sourceDescriptorMap = Lists.newArrayList(
                    Introspector.getBeanInfo(sourceType).getPropertyDescriptors())
                    .stream()
                    .collect(Collectors.toMap(
                            PropertyDescriptor::getName,
                            Function.identity()));
            targetDescriptorMap = Lists.newArrayList(
                    Introspector.getBeanInfo(targetType).getPropertyDescriptors())
                    .stream()
                    .collect(Collectors.toMap(
                            PropertyDescriptor::getName,
                            Function.identity()));
        } catch (IntrospectionException ex) {
            throw new IllegalStateException("Failed to load getter/setter.", ex);
        }

        Set<ReadWrite> readWrites = Sets.newHashSet();
        sourceDescriptorMap.forEach((propertyName, sourceDescriptor) -> {
            if (!targetDescriptorMap.containsKey(propertyName)) {
                return;
            }

            PropertyDescriptor targetDescriptor = targetDescriptorMap.get(propertyName);
            if (sourceDescriptor.getReadMethod() == null || sourceDescriptor.getWriteMethod() == null ||
                    targetDescriptor.getReadMethod() == null || targetDescriptor.getWriteMethod() == null) {
                return;
            }

            boolean canBeDeepened = true;
            Class<?> readRawType = sourceDescriptor.getPropertyType();
            Class<?> writeRawType = targetDescriptor.getPropertyType();

            if (isBaseType(readRawType) || isBaseType(writeRawType)) {
                if (getBaseTypeCaster(readRawType, writeRawType) == null) {
                    return;
                }
                canBeDeepened = false;
            }

            if (isExtraType(readRawType) || isExtraType(writeRawType)) {
                if (readRawType != writeRawType) {
                    return;
                }
                canBeDeepened = false;
            }

            if (isContainerType(readRawType) || isContainerType(writeRawType)) {
                if (readRawType != writeRawType) {
                    return;
                }
                canBeDeepened = false;
            }

            if (isEnumType(readRawType) || isEnumType(writeRawType)) {
                if (isEnumType(readRawType) ^ isEnumType(writeRawType)) {
                    return;
                }
                canBeDeepened = !isEnumType(readRawType);
            }

            JavaType readType = mapper.constructType(readRawType);
            JavaType writeType = mapper.constructType(writeRawType);
            if (isContainerType(readRawType)) {
                Field readField, writeField;
                try {
                    readField = sourceType.getDeclaredField(sourceDescriptor.getName());
                    writeField = targetType.getDeclaredField(targetDescriptor.getName());
                } catch (NoSuchFieldException ex) {
                    return;
                }
                readType = mapper.constructType(readField.getGenericType());
                writeType = mapper.constructType(writeField.getGenericType());
            }

            readWrites.add(new ReadWrite(
                    canBeDeepened,
                    readType,
                    sourceDescriptor.getReadMethod(),
                    sourceDescriptor.getWriteMethod(),
                    writeType,
                    targetDescriptor.getReadMethod(),
                    targetDescriptor.getWriteMethod()));
        });
        readWritesRegistry.get(sourceType).put(targetType, readWrites);
    }

    private static Set<ReadWrite> getReadWrites(Class<?> sourceType, Class<?> targetType) {
        if (!hasRegistered(sourceType, targetType)) {
            registerReadWrites(sourceType, targetType);
        }
        return readWritesRegistry.get(sourceType).get(targetType);
    }

    private static void doReadWrite(Object target, Object source, boolean overrideByNull, boolean deepen) {
        if (source == null) {
            return;
        }

        Set<ReadWrite> readWrites = getReadWrites(source.getClass(), target.getClass());
        readWrites.forEach(rw -> {
            try {
                Object sourceValue = rw.getSourceReadMethod().invoke(source);
                if (overrideByNull || sourceValue != null) {
                    Object targetValue = convert(sourceValue, rw.getTargetType());
                    if (deepen && rw.isCanBeDeepened() && rw.getTargetReadMethod().invoke(target) != null) {
                        Object originTargetValue = rw.getTargetReadMethod().invoke(target);
                        doReadWrite(originTargetValue, targetValue, overrideByNull, true);
                        targetValue = originTargetValue;
                    }
                    rw.getTargetWriteMethod().invoke(target, targetValue);
                }
            } catch (InvocationTargetException | IllegalAccessException ex) {
                throw new IllegalStateException(String.format("Failed to copy bean properties of [%s]", target.getClass().getName()), ex);
            }
        });
    }

    private static Map<Class<? extends Enum>, Map<Class<? extends Enum>, Map<Enum, Enum>>> enumMapRegistry = Maps.newHashMap();

    private static boolean hasEnumMap(Class<? extends Enum> sourceEnumType, Class<? extends Enum> targetEnumType) {
        return enumMapRegistry.containsKey(sourceEnumType) &&
                enumMapRegistry.get(sourceEnumType).containsKey(targetEnumType);
    }

    private static synchronized void initializeEnumMap(Class<? extends Enum> sourceEnumType, Class<? extends Enum> targetEnumType) {
        if (hasEnumMap(sourceEnumType, targetEnumType)) {
            return;
        }

        if (!enumMapRegistry.containsKey(sourceEnumType)) {
            enumMapRegistry.put(sourceEnumType, Maps.newHashMap());
        }

        Set<String> sourceEnumNames = Lists.newArrayList(sourceEnumType.getEnumConstants()).stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
        Set<String> targetEnumNames = Lists.newArrayList(targetEnumType.getEnumConstants()).stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
        Set<String> sharedEnumNames = Sets.intersection(sourceEnumNames, targetEnumNames);

        Map<Enum, Enum> enumMap = sharedEnumNames.stream()
                .collect(Collectors.toMap(
                        name -> (Enum)Enum.valueOf(sourceEnumType, name),
                        name -> (Enum)Enum.valueOf(targetEnumType, name)));
        sourceEnumNames.forEach(name -> {
            if (!sharedEnumNames.contains(name)) {
                enumMap.put(Enum.valueOf(sourceEnumType, name), null);
            }
        });

        enumMapRegistry.get(sourceEnumType).put(targetEnumType, enumMap);
    }

    private static Map<Enum, Enum> getEnumMap(Class<? extends Enum> sourceEnumType, Class<? extends Enum> targetEnumType) {
        if (!hasEnumMap(sourceEnumType, targetEnumType)) {
            initializeEnumMap(sourceEnumType, targetEnumType);
        }
        return enumMapRegistry.get(sourceEnumType).get(targetEnumType);
    }

    /**
     * convert实现将复杂数据结构递归，最终转化为基本数据结构的copy
     * remarks: 这里可以加一个函数作为参数，使得convert方法更具扩展性
     * @param source
     * @param javaType
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T> T convert(Object source, JavaType javaType) {
        if (source == null) {
            return null;
        }

        Class<?> sourceType = source.getClass();
        Class<?> targetType = javaType.getRawClass();
        checkArgument(!isUnsupportedType(sourceType), "SourceType [%s] is unsupported.", sourceType);
        checkArgument(!isUnsupportedType(targetType), "TargetType [%s] is unsupported.", targetType);

        if (isBaseType(targetType)) {
            Function caster = getBaseTypeCaster(sourceType, targetType);
            checkArgument(caster != null, "TargetType [%s] is not compatible with SourceType [%s].", targetType, sourceType);
            return (T)caster.apply(source);
        }

        if (isExtraType(targetType)) {
            checkArgument(sourceType == targetType,
                    "TargetType [%s] is not same as SourceType [%s].", targetType, sourceType);
            return (T)source;
        }

        if (isContainerType(targetType)) {
            if (targetType == List.class) {
                checkArgument(List.class.isAssignableFrom(sourceType),
                        "SourceType [%s] should be subclass of List.", sourceType);
                return (T)((List)source).stream()
                        .map(elem -> convert(elem, javaType.getContentType()))
                        .collect(Collectors.toList());
            }
            if (targetType == Set.class) {
                checkArgument(Set.class.isAssignableFrom(sourceType),
                        "SourceType [%s] should be subclass of Set.", sourceType);
                return (T)((Set)source).stream()
                        .map(elem -> convert(elem, javaType.getContentType()))
                        .collect(Collectors.toSet());
            }
            if (targetType == Map.class) {
                checkArgument(Map.class.isAssignableFrom(sourceType),
                        "SourceType [%s] should be subclass of Map.", sourceType);
                return (T)((Map)source).entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> convert(((Map.Entry)entry).getKey(), javaType.getKeyType()),
                                entry -> convert(((Map.Entry)entry).getValue(), javaType.getContentType())));
            }
            throw new IllegalArgumentException("This exception should not be thrown.");
        }

        checkArgument(isEnumType(sourceType) == isEnumType(targetType),
                "SourceType [%s] and TargetType [%s] should both or both not be Enum.", sourceType, targetType);

        Object target;
        if (isEnumType(sourceType)) {
            Map<Enum, Enum> enumMap = getEnumMap((Class<? extends Enum>)sourceType, (Class<? extends Enum>)targetType);
            target = enumMap.get(source);
        } else {
            target = AInvoke.newInstance(javaType.getRawClass());
            doReadWrite(target, source, true, false);
        }
        return (T)target;
    }



    public static <T> T convert(Object source, TypeToken<T> typeToken) {
        return convert(source, typeToken);
    }


    public static <T> T convert(Object source, Class<T> type) {
        return convert(source, type);
    }

    @SuppressWarnings("unchecked")
    public static <T> T clone(T source) {
        checkArgument(source != null, "Object could not be null.");
        T target = AInvoke.newInstance((Class<T>)source.getClass());
        doReadWrite(target, source, true, false);
        return target;
    }

    private static <T> T doCopy(boolean deepen, T target, Object... sources) {
        for (Object source : sources) {
            doReadWrite(target, source, true, deepen);
        }
        return target;
    }

    public static <T> T copy(T target, Object... sources) {
        return doCopy(false, clone(target), sources);
    }

    public static <T> T deepCopy(T target, Object... sources) {
        return doCopy(true, clone(target), sources);
    }

    public static <T> void copyTo(T target, Object... sources) {
        doCopy(false, target, sources);
    }

    public static <T> void deepCopyTo(T target, Object... sources) {
        doCopy(true, target, sources);
    }

    private static <T> T doMerge(boolean deepen, T target, Object... sources) {
        for (Object source : sources) {
            doReadWrite(target, source, false, deepen);
        }
        return target;
    }

    public static <T> T merge(T target, Object... sources) {
        return doMerge(false, clone(target), sources);
    }

    public static <T> T deepMerge(T target, Object...sources) {
        return doMerge(true, clone(target), sources);
    }

    public static <T> void mergeTo(T target, Object... sources) {
        doMerge(false, target, sources);
    }

    public static <T> void deepMergeTo(T target, Object... sources) {
        doMerge(true, target, sources);
    }
}
