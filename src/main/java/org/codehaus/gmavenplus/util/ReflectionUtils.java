/*
 * Copyright (C) 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.gmavenplus.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;


/**
 * Inspired from https://src.springframework.org/svn/spring-framework/trunk/org.springframework.core/src/main/java/org/springframework/util/ReflectionUtils.java
 *
 * @author Keegan Witt
 */
public final class ReflectionUtils {

    private ReflectionUtils() {}

    /**
     * Attempt to find a {@link Method} on the supplied class with the supplied name
     * and parameter types. Searches all superclasses up to <code>Object</code>.
     *
     * @param clazz      the class to introspect
     * @param name       the name of the method
     * @param paramTypes the parameter types of the method
     *                   (may be <code>null</code> to indicate any signature)
     * @return the Method object
     */
    public static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class must not be null.");
        }
        if (name == null) {
            throw new IllegalArgumentException("Method name must not be null.");
        }
        Class<?> searchType = clazz;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
            for (Method method : methods) {
                if (name.equals(method.getName()) && (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        throw new IllegalArgumentException("Unable to find method " + clazz.getName() + "." + name + "(" + Arrays.toString(paramTypes).replaceAll("^\\[", "").replaceAll("\\]$", "").replaceAll("class ", "") + ").");
    }

    /**
     * Attempt to find a {@link Constructor} on the supplied class with the supplied
     * parameter types. Searches all superclasses up to <code>Object</code>.
     *
     * @param clazz      the class to introspect
     * @param paramTypes the parameter types of the method
     *                   (may be <code>null</code> to indicate any signature)
     * @return the Constructor object
     */
    public static Constructor findConstructor(Class<?> clazz, Class<?>... paramTypes) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class must not be null.");
        }
        Class<?> searchType = clazz;
        while (searchType != null) {
            Constructor[] constructors = (searchType.isInterface() ? searchType.getConstructors() : searchType.getDeclaredConstructors());
            for (Constructor constructor : constructors) {
                if (paramTypes == null || Arrays.equals(paramTypes, constructor.getParameterTypes())) {
                    return constructor;
                }
            }
            searchType = searchType.getSuperclass();
        }
        throw new IllegalArgumentException("Unable to find constructor " + clazz.getName() + "(" + Arrays.toString(paramTypes).replaceAll("^\\[", "").replaceAll("\\]$", "").replaceAll("class ", "") + ").");
    }

    /**
     * Invoke the specified {@link Method} against the supplied target object with the
     * supplied arguments. The target object can be <code>null</code> when invoking a
     * static {@link Method}.
     *
     * @param method the method to invoke
     * @param target the target object to invoke the method on
     * @param args the invocation arguments (may be <code>null</code>)
     * @return the invocation result, if any
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public static Object invokeMethod(Method method, Object target, Object... args) throws InvocationTargetException, IllegalAccessException {
        if (method == null) {
            throw new IllegalArgumentException("Method must not be null.");
        }
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    /**
     * Invoke the specified static {@link Method} with the supplied arguments.
     *
     * @param method the method to invoke
     * @param args the invocation arguments (may be <code>null</code>)
     * @return the invocation result, if any
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public static Object invokeStaticMethod(Method method, Object... args) throws InvocationTargetException, IllegalAccessException {
        if (method == null) {
            throw new IllegalArgumentException("Method must not be null.");
        }
        method.setAccessible(true);
        return method.invoke(args);
    }

    /**
     * Invoke the specified {@link Constructor}  with the supplied arguments.
     *
     * @param constructor the method to invoke
     * @param args the invocation arguments (may be <code>null</code>)
     * @return the invocation result, if any
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     * @throws InstantiationException
     */
    public static Object invokeConstructor(Constructor constructor, Object... args) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if (constructor == null) {
            throw new IllegalArgumentException("Constructor must not be null.");
        }
        constructor.setAccessible(true);
        return constructor.newInstance(args);
    }

    /**
     * Attempt to find a {@link Field field} on the supplied {@link Class} with the
     * supplied <code>name</code> and/or {@link Class type}. Searches all superclasses
     * up to {@link Object}.
     * @param clazz the class to introspect
     * @param name the name of the field (may be <code>null</code> if type is specified)
     * @param type the type of the field (may be <code>null</code> if name is specified)
     * @return the corresponding Field object
     */
    public static Field findField(Class<?> clazz, String name, Class<?> type) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class must not be null");
        }
        if (name == null && type == null) {
            throw new IllegalArgumentException("Either name or type of the field must be specified.");
        }
        Class<?> searchType = clazz;
        while (!Object.class.equals(searchType) && searchType != null) {
            Field[] fields = searchType.getDeclaredFields();
            for (Field field : fields) {
                if ((name == null || name.equals(field.getName())) && (type == null || type.equals(field.getType()))) {
                    return field;
                }
            }
            searchType = searchType.getSuperclass();
        }
        throw new IllegalArgumentException("Unable to find " + type.getName() + " " + name + ".");
    }

    /**
     * Get the field represented by the supplied {@link Field field object} on the
     * specified {@link Object target object}. In accordance with {@link Field#get(Object)}
     * semantics, the returned value is automatically wrapped if the underlying field
     * has a primitive type.
     * @param field the field to get
     * @param target the target object from which to get the field
     * @return the field's current value
     * @throws IllegalAccessException
     */
    public static Object getField(Field field, Object target) throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(target);
    }

    /**
     * Get the field represented by the supplied {@link Field field object} on the
     * specified {@link Object target object}. In accordance with {@link Field#get(Object)}
     * semantics, the returned value is automatically wrapped if the underlying field
     * has a primitive type.
     * @param field the field to get
     * @return the field's current value
     * @throws IllegalAccessException
     */
    public static Object getField(Field field) throws IllegalAccessException {
        return getField(field, null);
    }

    /**
     *
     * @param enumClass
     * @param constantName
     * @return
     */
    public static Object getEnumConstant(Class<?> enumClass, String constantName) {
        if (enumClass.isEnum()) {
            for ( Object o : enumClass.getEnumConstants()) {
              if (o.toString().equals(constantName)) {
                  return o;
              }
            }
            throw new IllegalArgumentException("Unable to get an enum constant with that name.");
        } else {
            throw new IllegalArgumentException(enumClass + " must be an enum.");
        }
    }

}
