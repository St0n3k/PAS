package pl.lodz.p.it.pas.common;

import java.util.Set;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

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
