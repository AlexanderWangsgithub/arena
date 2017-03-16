package com.github.alexanderwangsgithub.arena.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.testng.annotations.Test;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertEqualsNoOrder;


/**
 * @author Alexander Wang
 * @email alexanderwangwork@outlook.com
 * @date 09/03/2017
 */
public class SnipTest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ObjA{
        int fieldA;
        String fieldB;
    }
    @Test
    public void PropertyDescriptorTest() throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        ObjA objA = new ObjA(11, "before");
        PropertyDescriptor descriptor = new PropertyDescriptor("fieldB", objA.getClass());
        Method getter = descriptor.getReadMethod();//获得某个field的get方法
        Method setter = descriptor.getWriteMethod();//获得某个field的set方法
        assertEquals("before", getter.invoke(objA));
        setter.invoke(objA, "after");
        assertEquals("after", objA.getFieldB());
        assertEquals(descriptor.getPropertyType(),objA.getFieldB().getClass());//获得某个field的class
    }

    @Test
    public void regTest(){

    }
}
