package com.parallelblock.reporter.api;

import java.lang.invoke.MethodHandle;

public final class MethodHandleHelper {
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
