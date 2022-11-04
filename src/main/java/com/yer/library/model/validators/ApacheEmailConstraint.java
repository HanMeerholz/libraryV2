package com.yer.library.model.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Email pattern check using Apache Commons EmailValidator
 * as the Hibernate EmailValidator is limited.
 * <p>
 * The string has to be a well-formed email address.
 *
 * @author Robert Oschwald
 */
@Documented
@Constraint(validatedBy = ApacheEmailValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
public @interface ApacheEmailConstraint {

    String message() default "{org.hibernate.validator.constraints.Email.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}