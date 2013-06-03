package com.intellij.openapi.diagnostic;

/**
 * @author VISTALL
 * @since 11:48/03.06.13
 */
public class Logger {
    public static Logger getInstance(Class<?> clazz) {
        return new Logger();
    }

    public void log() {
    }
}
