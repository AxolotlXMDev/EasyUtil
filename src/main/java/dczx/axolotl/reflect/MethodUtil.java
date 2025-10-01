package dczx.axolotl.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/10/1 21:17
 */
public class MethodUtil {
    /**
     * 调用指定对象的指定方法，支持可变参数。
     *
     * @param obj    要调用方法的对象
     * @param method 要调用的方法
     * @param args   方法的参数，可变参数形式
     * @return 方法调用的返回值
     * @throws Exception 如果方法调用失败或参数不匹配
     */
    public static Object invokeWithVarargs(Object obj, Method method, Object... args) throws Exception {
        if (!method.isVarArgs()) {
            return method.invoke(obj, args);
        }

        Class<?>[] paramTypes = method.getParameterTypes();
        int fixedParamCount = paramTypes.length - 1;

        if (args.length < fixedParamCount) {
            throw new IllegalArgumentException("Not enough arguments for method: " + method.getName());
        }

        // 构造最终参数数组
        Object[] finalArgs = new Object[paramTypes.length];

        // 复制固定参数
        System.arraycopy(args, 0, finalArgs, 0, fixedParamCount);

        // 包装可变参数为数组
        Class<?> varArgComponentType = paramTypes[fixedParamCount].getComponentType();
        Object varArgArray = Array.newInstance(varArgComponentType, args.length - fixedParamCount);

        for (int i = fixedParamCount; i < args.length; i++) {
            Array.set(varArgArray, i - fixedParamCount, args[i]);
        }

        finalArgs[fixedParamCount] = varArgArray;
        return method.invoke(obj, finalArgs);
    }

    /**
     * 查找指定类中名称匹配且参数兼容的方法。
     *
     * @param clazz      要查找方法的类
     * @param methodName 方法名称
     * @param args       方法的参数，用于匹配方法签名
     * @return 匹配的方法对象
     * @throws IllegalArgumentException 如果未找到匹配的方法
     */
    public static Method findMethod(Class<?> clazz, String methodName, Object... args) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                Class<?>[] paramTypes = method.getParameterTypes();
                if (isCompatible(method, args)) {
                    return method;
                }
            }
        }
        throw new IllegalArgumentException("No such method: " + methodName);
    }

    /**
     * 检查方法的参数类型是否与给定参数兼容。
     *
     * @param method 要检查的目标方法
     * @param args   给定的参数
     * @return 如果参数兼容则返回 true，否则返回 false
     */
    private static boolean isCompatible(Method method, Object[] args) {
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length == 0) return args.length == 0;

        // 如果最后一个参数是 varargs
        boolean isVarArgs = method.isVarArgs();
        int fixedParamCount = isVarArgs ? paramTypes.length - 1 : paramTypes.length;

        if (args.length < fixedParamCount) return false;
        if (!isVarArgs && args.length != paramTypes.length) return false;

        // 检查固定参数类型
        for (int i = 0; i < fixedParamCount; i++) {
            if (!isAssignable(paramTypes[i], args[i])) {
                return false;
            }
        }

        if (isVarArgs) {
            Class<?> varArgType = paramTypes[paramTypes.length - 1].getComponentType(); // 注意：是 componentType！
            for (int i = fixedParamCount; i < args.length; i++) {
                if (!isAssignable(varArgType, args[i])) {
                    return false;
                }
            }
        }

        return true;
    }

    private static final java.util.Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER =
            java.util.Map.of(
                    boolean.class, Boolean.class,
                    byte.class, Byte.class,
                    char.class, Character.class,
                    double.class, Double.class,
                    float.class, Float.class,
                    int.class, Integer.class,
                    long.class, Long.class,
                    short.class, Short.class,
                    void.class, Void.class
            );


    /**
     * 检查给定的参数是否可以分配给目标类型。
     *
     * @param targetType 目标类型
     * @param arg        给定的参数
     * @return 如果参数可以分配给目标类型则返回 true，否则返回 false
     */
    private static boolean isAssignable(Class<?> targetType, Object arg) {
        if (arg == null) {
            return !targetType.isPrimitive();
        }
        Class<?> argType = arg.getClass();

        // 如果目标类型是基本类型，检查 arg 是否是对应的包装类
        if (targetType.isPrimitive()) {
            Class<?> expectedWrapper = PRIMITIVE_TO_WRAPPER.get(targetType);
            return expectedWrapper != null && expectedWrapper.equals(argType);
        }

        // 否则按常规引用类型处理
        return targetType.isAssignableFrom(argType);
    }
}
