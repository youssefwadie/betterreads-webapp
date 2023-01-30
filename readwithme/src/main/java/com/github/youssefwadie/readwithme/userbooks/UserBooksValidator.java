package com.github.youssefwadie.readwithme.userbooks;

import com.github.youssefwadie.readwithme.exceptions.InvalidValidationApiUsageException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Component
public class UserBooksValidator implements Validator {
    private final static Class<UserBooks> SUPPORTED_CLASS = UserBooks.class;

    @Override
    public boolean supports(Class<?> clazz) {
        return SUPPORTED_CLASS.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        if (!supports(target.getClass())) {
            throw new InvalidValidationApiUsageException(SUPPORTED_CLASS, target.getClass());
        }

        ValidationUtils.rejectIfEmpty(errors, "key", "key cannot be null");
        ValidationUtils.rejectIfEmpty(errors, "startedDate", "startDate cannot be null");
        ValidationUtils.rejectIfEmpty(errors, "completedDate", "completedDate cannot be null");
        ValidationUtils.rejectIfEmpty(errors, "readingStatus", "readingStatus cannot be null");
        ValidationUtils.rejectIfEmpty(errors, "rating", "rating cannot be null");
        UserBooks userBooks = (UserBooks) target;
        LocalDate startedDate = userBooks.getStartedDate();
        LocalDate completedDate = userBooks.getCompletedDate();
        Integer rating = userBooks.getRating();
        validateStartedAndCompletedDates(errors, startedDate, completedDate);
        validateRating(errors, rating);
    }

    private static void validateRating(Errors errors, Integer rating) {
        if (rating == null) {
            errors.rejectValue("rating", "rating must be set");
            return;
        }

        if (!inRange(rating)) {
            errors.rejectValue("rating", "rating must be between 1 and 5");
        }

    }

    private static void validateStartedAndCompletedDates(Errors errors, LocalDate startedDate, LocalDate completedDate) {
        if (startedDate == null || completedDate == null) {
            errors.rejectValue("startedDate", "cannot be null");
            errors.rejectValue("completedDate", "cannot be null");
            return;
        }

        if (startedDate.isEqual(completedDate) || startedDate.isAfter(completedDate)) {
            errors.rejectValue("startedDate", "started date must be before the completed date");
            errors.rejectValue("completedDate", "completed date must be after the started date");
        }
    }

    private static boolean inRange(int rating) {
        return rating >= 1 && rating <= 5;
    }

}
