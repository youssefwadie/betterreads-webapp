package com.github.youssefwadie.readwithme.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.Errors;

@Getter
@RequiredArgsConstructor
public class ValidationException extends IllegalArgumentException {
    private final Errors errors;

}
