package com.github.alexanderwangsgithub.arena.core.common.reflect;

import com.google.common.base.Defaults;
import com.google.common.collect.Sets;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public final class AInvoke {
    private AInvoke() {}

    private static boolean isAssignable(List<Parameter> parameters, Class<?>[] parameterTypes) {
        if (parameters.size() != parameterTypes.length) {
            return false;
        }
        for (int index = 1; index < parameters.size(); index++) {
            if (!parameters.get(index).getType().getRawType().isAssignableFrom(parameterTypes[index])) {
                return false;
            }
        }
        return true;
    }

    public static <T> T newInstance(Class<T> clazz) {
        //指定的Class对象是否表示一个基本类型
        if (clazz.isPrimitive()) {
            return Defaults.defaultValue(clazz);
        }

        Invokable<T, T> constructor = AInvoke.getConstructor(clazz);
        if (constructor == null) {
            throw new IllegalArgumentException(String.format(
                    "Class [%s] should has a constructor with no parameter.", clazz.getName()));
        }
        return newInstance(constructor);
    }

    public static <T> T newInstance(Invokable invokable, Object... parameters) {
        if (!invokable.getClass().getName().equals("com.google.common.reflect.Invokable$ConstructorInvokable")) {
            throw new IllegalArgumentException("The invokable used to create instance must be constructed by a Constructor.");
        }

        try {
            invokable.setAccessible(true);
            return (T)invokable.invoke(null, parameters);
        } catch (InvocationTargetException | IllegalAccessException ex) {
            throw new IllegalStateException(String.format("Instantiation of [%s] failed.", invokable.getDeclaringClass()), ex);
        }
    }

    /**
     * @return Invokable是对java.lang.reflect.Method和java.lang.reflect.Constructor的流式包装
     * http://outofmemory.cn/java/guava/reflect/Invokable
     */
    public static Invokable getConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        try {
            return Invokable.from(clazz.getDeclaredConstructor(parameterTypes));
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    public static Set<Invokable> getConstructors(Class<?> clazz) {
        return Sets.newHashSet(clazz.getDeclaredConstructors()).stream()
                .map(Invokable::from).collect(Collectors.toSet());
    }


    public static Invokable getInvokableFromMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return Invokable.from(clazz.getMethod(methodName, parameterTypes));
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    public static Set<Invokable> getInvokablesFromMethods(Class<?> clazz, String methodName) {
        Set<Invokable> invokableSet = Sets.newHashSet();
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                invokableSet.add(Invokable.from(method));
            }
        }
        return invokableSet;
    }


    public static Invokable getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return Invokable.from(clazz.getDeclaredMethod(methodName, parameterTypes));
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    public static Set<Invokable> getDeclaredMethods(Class<?> clazz, String methodName) {
        Set<Invokable> invokableSet = Sets.newHashSet();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                invokableSet.add(Invokable.from(method));
            }
        }
        return invokableSet;
    }

    public static Set<Invokable> getDeclaredAssignableMethods(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Set<Invokable> invokableSet = Sets.newHashSet();
        for (Method method : clazz.getDeclaredMethods()) {
            Invokable invokable = Invokable.from(method);
            if (invokable.getName().equals(methodName) && isAssignable(invokable.getParameters(), parameterTypes)) {
                invokableSet.add(invokable);
            }
        }
        return invokableSet;
    }

    public static <T> T invokeMethod(Invokable invokable, Object instance, Object... parameters) {
        try {
            invokable.setAccessible(true);
            return (T)invokable.invoke(instance, parameters);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        } catch (InvocationTargetException ex) {
            throw new IllegalStateException(ex.getCause());
        }
    }
}
