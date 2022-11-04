package com.yer.library.model;

import com.yer.library.model.enums.MembershipTypeName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MembershipTypeTest {
    private static Validator validator;

    @BeforeEach
    public void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void validMembershipType() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );

        Set<ConstraintViolation<MembershipType>> violations = validator.validate(membershipType);
        assertThat(violations).isEmpty();
    }

    @Test
    public void typeNameNull() {
        MembershipType membershipType = new MembershipType(
                null, 500
        );

        Set<ConstraintViolation<MembershipType>> violations = validator.validate(membershipType);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void negativeCost() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, -500
        );

        Set<ConstraintViolation<MembershipType>> violations = validator.validate(membershipType);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void costTooHigh() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 50000
        );

        Set<ConstraintViolation<MembershipType>> violations = validator.validate(membershipType);
        assertThat(violations).hasSize(1);
    }
}