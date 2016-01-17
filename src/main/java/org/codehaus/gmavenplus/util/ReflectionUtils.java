/*
 * Copyright (C) 2015 the original author or authors.
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
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * Inspired heavily by Spring's <a href="https://github.com/SpringSource/spring-framework/blob/master/spring-core/src/main/java/org/springframework/util/ReflectionUtils.java">ReflectionUtils</a>.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Rod Johnson
 * @author Costin Leau
 * @author Sam Brannen
 * @author Chris Beams
 * @author Keegan Witt
 * @since 1.0-beta-1
 */
public class ReflectionUtils {

    /**
     * Private constructor that should never be called since this is a static
     * utility class.
     */
    private ReflectionUtils() { }

    protected static List<Method> findConcreteMethodsOnInterfaces(Class<?> clazz) {
        List<Method> result = null;
        for (Class<?> ifc : clazz.getInterfaces()) {
            for (Method ifcMethod : ifc.getMethods()) {
                if (!Modifier.isAbstract(ifcMethod.getModifiers())) {
                    if (result == null) {
                        result = new LinkedList<Method>();
                    }
                    result.add(ifcMethod);
                }
            }
        }
        return result;
    }

    /**
     * Attempt to find a {@link Constructor} on the supplied class with the
     * supplied parameter types. Searches all superclasses up to
     * <code>Object</code>.
     *
     * @param clazz The class to introspect
     * @param paramTypes The parameter types of the method (may be <code>null</code> to indicate any signature)
     * @return The Constructor object
     */
    public static Constructor findConstructor(final Class<?> clazz, final Class<?>... paramTypes) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class must not be null.");
        }
        Class<?> searchType = clazz;
        while (searchType != null) {
            Constructor[] constructors = searchType.isInterface() ? clazz.getConstructors() : clazz.getDeclaredConstructors();
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
     * Attempt to find a {@link Field field} on the supplied {@link Class} with
     * the supplied <code>name</code> and/or {@link Class type}. Searches all
     * superclasses up to {@link Object}.
     *
     * @param clazz The class to introspect
     * @param name The name of the field (may be <code>null</code> if type is specified)
     * @param type The type of the field (may be <code>null</code> if name is specified)
     * @return The corresponding Field object
     */
    public static Field findField(final Class<?> clazz, final String name, final Class<?> type) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class must not be null");
        }
        if (name == null && type == null) {
            throw new IllegalArgumentException("Either name or type of the field must be specified.");
        }
        Class<?> searchType = clazz;
        while (Object.class != searchType && searchType != null) {
            Field[] fields = searchType.getDeclaredFields();
            for (Field field : fields) {
                if ((name == null || name.equals(field.getName())) && (type == null || type.equals(field.getType()))) {
                    return field;
                }
            }
            searchType = searchType.getSuperclass();
        }
        throw new IllegalArgumentException("Unable to find " + (type != null ? type.getName() : "") + " " + (name != null ? name : "") + ".");
    }

    /**
     * Attempt to find a {@link Method} on the supplied class with the supplied
     * name and parameter types. Searches all superclasses up to
     * <code>Object</code>.
     *
     * @param clazz      The class to introspect
     * @param name       The name of the method
     * @param paramTypes The parameter types of the method
     *                   (may be <code>null</code> to indicate any signature)
     * @return The Method object
     */
    public static Method findMethod(final Class<?> clazz, final String name, final Class<?>... paramTypes) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class must not be null.");
        }
        if (name == null) {
            throw new IllegalArgumentException("Method name must not be null.");
        }
        Class<?> searchType = clazz;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : getDeclaredMethods(searchType));
            if (methods != null) {
                for (Method method : methods) {
                    if (name.equals(method.getName()) && (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
                        return method;
                    }
                }
            }
            searchType = searchType.getSuperclass();
        }
        throw new IllegalArgumentException("Unable to find method " + clazz.getName() + "." + name + "(" + Arrays.toString(paramTypes).replaceAll("^\\[", "").replaceAll("\\]$", "").replaceAll("class ", "") + ").");
    }

    /**
     * This variant retrieves {@link Class#getDeclaredMethods()} from a local cache
     * in order to avoid the JVM's SecurityManager check and defensive array copying.
     * In addition, it also includes Java 8 default methods from locally implemented
     * interfaces, since those are effectively to be treated just like declared methods.
     *
     * @param clazz the class to introspect
     * @return the cached array of methods
     * @see Class#getDeclaredMethods()
     */
    protected static Method[] getDeclaredMethods(Class<?> clazz) {
        Method[] result;
        Method[] declaredMethods = clazz.getDeclaredMethods();
        List<Method> defaultMethods = findConcreteMethodsOnInterfaces(clazz);
        if (defaultMethods != null) {
            result = new Method[declaredMethods.length + defaultMethods.size()];
            System.arraycopy(declaredMethods, 0, result, 0, declaredMethods.length);
            int index = declaredMethods.length;
            for (Method defaultMethod : defaultMethods) {
                result[index] = defaultMethod;
                index++;
            }
        } else {
            result = declaredMethods;
        }
        return result;
    }

    /**
     * Find and return the specified value from the specified enum class.
     *
     * @param clazz The enum class to introspect
     * @param valueName The name of the enum value to get
     * @return The enum value
     */
    public static Object getEnumValue(final Class<?> clazz, final String valueName) {
        if (clazz.isEnum()) {
            for (Object o : clazz.getEnumConstants()) {
                if (o.toString().equals(valueName)) {
                    return o;
                }
            }
            throw new IllegalArgumentException("Unable to get an enum constant with that name.");
        } else {
            throw new IllegalArgumentException(clazz + " must be an enum.");
        }
    }

    /**
     * Get the field represented by the supplied {@link Field field object} on
     * the specified {@link Object target object}. In accordance with
     * {@link Field#get(Object)} semantics, the returned value is automatically
     * wrapped if the underlying field has a primitive type.
     *
     * @param field The field to get
     * @param target The target object from which to get the field
     * @return The field's current value
     * @throws IllegalAccessException when unable to access the specified field because access modifiers prevent it
     */
    public static Object getField(final Field field, final Object target) throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(target);
    }

    /**
     * Get the field represented by the supplied {@link Field field object} on
     * the specified {@link Object target object}. In accordance with
     * {@link Field#get(Object)} semantics, the returned value is automatically
     * wrapped if the underlying field has a primitive type.
     *
     * @param field The field to get
     * @return The field's current value
     * @throws IllegalAccessException when unable to access the specified field because access modifiers prevent it
     */
    public static Object getStaticField(final Field field) throws IllegalAccessException {
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("Field must be static.");
        }
        return getField(field, null);
    }

    /**
     * Invoke the specified {@link Constructor}  with the supplied arguments.
     *
     * @param constructor The method to invoke
     * @param args The invocation arguments (may be <code>null</code>)
     * @return The invocation result, if any
     * @throws IllegalAccessException when unable to access the specified constructor because access modifiers prevent it
     * @throws java.lang.reflect.InvocationTargetException when a reflection invocation fails
     * @throws InstantiationException when an instantiation fails
     */
    public static Object invokeConstructor(final Constructor constructor, final Object... args) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if (constructor == null) {
            throw new IllegalArgumentException("Constructor must not be null.");
        }
        constructor.setAccessible(true);
        return constructor.newInstance(args);
    }

    /**
     * Invoke the specified {@link Method} against the supplied target object
     * with the supplied arguments. The target object can be <code>null</code>
     * when invoking a static {@link Method}.
     *
     * @param method The method to invoke
     * @param target The target object to invoke the method on
     * @param args The invocation arguments (may be <code>null</code>)
     * @return The invocation result, if any
     * @throws IllegalAccessException when unable to access the specified method because access modifiers prevent it
     * @throws java.lang.reflect.InvocationTargetException when a reflection invocation fails
     */
    public static Object invokeMethod(final Method method, final Object target, final Object... args) throws InvocationTargetException, IllegalAccessException {
        if (method == null) {
            throw new IllegalArgumentException("Method must not be null.");
        }
        if (target == null) {
            throw new IllegalArgumentException("Object must not be null.");
        }
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    /**
     * Invoke the specified static {@link Method} with the supplied arguments.
     *
     * @param method The method to invoke
     * @param args The invocation arguments (may be <code>null</code>)
     * @return The invocation result, if any
     * @throws IllegalAccessException when unable to access the specified method because access modifiers prevent it
     * @throws java.lang.reflect.InvocationTargetException when a reflection invocation fails
     */
    public static Object invokeStaticMethod(final Method method, final Object... args) throws InvocationTargetException, IllegalAccessException {
        if (method == null) {
            throw new IllegalArgumentException("Method must not be null.");
        }
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException("Method must be static.");
        }
        method.setAccessible(true);
        return method.invoke(null, args);
    }

}
