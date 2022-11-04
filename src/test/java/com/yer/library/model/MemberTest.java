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

class MemberTest {
    private static Validator validator;

    @BeforeEach
    public void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void validMember() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.JANUARY, 1),
                LocalDate.of(2020, Month.JANUARY, 1)
        );
        Member member = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).isEmpty();
    }

    @Test
    public void nameNull() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.JANUARY, 1),
                LocalDate.of(2020, Month.JANUARY, 1)
        );
        Member member = new Member(
                null,
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void nameEmpty() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.JANUARY, 1),
                LocalDate.of(2020, Month.JANUARY, 1)
        );
        Member member = new Member(
                "",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void addressNull() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.JANUARY, 1),
                LocalDate.of(2020, Month.JANUARY, 1)
        );
        Member member = new Member(
                "Kaden Dickens",
                null,
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).isEmpty();
    }

    @Test
    public void emailNull() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.JANUARY, 1),
                LocalDate.of(2020, Month.JANUARY, 1)
        );
        Member member = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                null,
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void emailEmpty() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.JANUARY, 1),
                LocalDate.of(2020, Month.JANUARY, 1)
        );
        Member member = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "",
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void emailInvalid() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.JANUARY, 1),
                LocalDate.of(2020, Month.JANUARY, 1)
        );
        Member member = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "kadendickens",
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void emailInvalid2() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.JANUARY, 1),
                LocalDate.of(2020, Month.JANUARY, 1)
        );
        Member member = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "kadendickens@mail",
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void emailInvalid3() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.JANUARY, 1),
                LocalDate.of(2020, Month.JANUARY, 1)
        );
        Member member = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "kadendickens.com",
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void emailValid() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.JANUARY, 1),
                LocalDate.of(2020, Month.JANUARY, 1)
        );
        Member member = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "a@b.gg",
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).isEmpty();
    }

    @Test
    public void birthdayNull() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.JANUARY, 1),
                LocalDate.of(2020, Month.JANUARY, 1)
        );
        Member member = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                null,
                membership
        );

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).isEmpty();
    }

    @Test
    public void futureBirthday() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.JANUARY, 1),
                LocalDate.of(2020, Month.JANUARY, 1)
        );
        Member member = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.now().plusYears(1),
                membership
        );

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void nullMembership() {
        Member member = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                null
        );

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).isEmpty();
    }
}