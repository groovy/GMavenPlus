package gmavenplus.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;


/**
 * Inspired from https://src.springframework.org/svn/spring-framework/trunk/org.springframework.core/src/main/java/org/springframework/util/ReflectionUtils.java
 *
 * @author wittk
 * @version $Rev$ $Date$
 */
public class ReflectionUtils {

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
            throw new IllegalArgumentException("Class must not be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Method name must not be null");
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
        throw new IllegalArgumentException("Unable to find method " + clazz.getName() + "." + name + "(" + Arrays.toString(paramTypes).replaceAll("^\\[", "").replaceAll("\\]$", "").replaceAll("class ", "") + ")");
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
            throw new IllegalArgumentException("Class must not be null");
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
        throw new IllegalArgumentException("Unable to find constructor " + clazz.getName() + "(" + Arrays.toString(paramTypes).replaceAll("^\\[", "").replaceAll("\\]$", "").replaceAll("class ", "") + ")");
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
            throw new IllegalArgumentException("Method must not be null");
        }
        return method.invoke(target, args);
    }

    /**
     * Invoke the specified {@link Method} against the supplied target object with the
     * supplied arguments. The target object can be <code>null</code> when invoking a
     * static {@link Method}.
     *
     * @param method the method to invoke
     * @param args the invocation arguments (may be <code>null</code>)
     * @return the invocation result, if any
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public static Object invokeStaticMethod(Method method, Object... args) throws InvocationTargetException, IllegalAccessException {
        if (method == null) {
            throw new IllegalArgumentException("Method must not be null");
        }
        return method.invoke(args);
    }

}
