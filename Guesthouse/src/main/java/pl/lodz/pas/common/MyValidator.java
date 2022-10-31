package pl.lodz.pas.common;

import jakarta.validation.*;

import java.util.Set;

public class MyValidator {

    private static final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    public static <T> void validate(T object) {
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (violations.size() > 0) {
            throw new ValidationException();
        }
    }

}
