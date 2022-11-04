package com.yer.library.model;

import com.yer.library.model.enums.MembershipTypeName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MembershipTest {
    private static Validator validator;

    @BeforeEach
    public void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void validMembership() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.JANUARY, 1),
                LocalDate.of(2020, Month.JANUARY, 1)
        );

        Set<ConstraintViolation<Membership>> violations = validator.validate(membership);
        assertThat(violations).isEmpty();
    }

    @Test
    public void typeNull() {
        Membership membership = new Membership(
                null,
                LocalDate.of(2019, Month.JANUARY, 1),
                LocalDate.of(2020, Month.JANUARY, 1)
        );

        Set<ConstraintViolation<Membership>> violations = validator.validate(membership);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void startDateNull() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        Membership membership = new Membership(
                membershipType,
                null,
                LocalDate.of(2020, Month.JANUARY, 1)
        );

        Set<ConstraintViolation<Membership>> violations = validator.validate(membership);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void endDateNull() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.JANUARY, 1),
                null
        );

        Set<ConstraintViolation<Membership>> violations = validator.validate(membership);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void startDateInFuture() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        Membership membership = new Membership(
                membershipType,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(1).plusYears(1)
        );

        Set<ConstraintViolation<Membership>> violations = validator.validate(membership);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void membershipTimeTooLong() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.JANUARY, 1),
                LocalDate.of(2029, Month.JANUARY, 1)
        );

        Set<ConstraintViolation<Membership>> violations = validator.validate(membership);
        assertThat(violations).hasSize(1);
    }
}