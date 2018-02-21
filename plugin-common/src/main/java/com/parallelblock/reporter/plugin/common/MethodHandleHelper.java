package com.parallelblock.reporter.plugin.common;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public final class MethodHandleHelper {

    public static <T> T getNamedField(Object object, String fieldName, Class<T> fieldType) {
        try {
            return (T) MethodHandles.lookup().findGetter(object.getClass(), fieldName, fieldType).invoke(object);
        } catch (Throwable throwable) {
            if (throwable instanceof RuntimeException)
                throw (RuntimeException)throwable;
            else
                throw new RuntimeException("Unexpected throwable", throwable);
        }
    }

    public static <T> T help(MethodHandle handle, Object... invoke) {
        try {
            return (T) handle.invoke(invoke);
        } catch (Throwable throwable) {
            if (throwable instanceof RuntimeException)
                throw (RuntimeException)throwable;
            else
                throw new RuntimeException("Unexpected throwable", throwable);
        }
    }
}
