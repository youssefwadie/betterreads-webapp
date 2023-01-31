package com.github.youssefwadie.readwithme.exceptions;

public class InvalidValidationApiUsageException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "supported class %s, actual class %s.";

    public InvalidValidationApiUsageException(String message) {
        super(message);
    }

    public InvalidValidationApiUsageException(Class<?> supportedClass, Class<?> actualClass) {
        this(MESSAGE_TEMPLATE.formatted(supportedClass.getName(), actualClass.getName()));
    }
}
