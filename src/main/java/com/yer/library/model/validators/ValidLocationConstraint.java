package com.yer.library.model.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LocationValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLocationConstraint {
    String message() default "Invalid location";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}