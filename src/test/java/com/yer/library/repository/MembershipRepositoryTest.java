package com.yer.library.repository;

import com.yer.library.model.Membership;
import com.yer.library.model.MembershipType;
import com.yer.library.model.MembershipTypeName;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.PageRequest.ofSize;

@DataJpaTest
class MembershipRepositoryTest {
    @Autowired
    private MembershipRepository underTest;
    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
        membershipTypeRepository.deleteAll();
    }

    @Test
    void listAvailableEmptyDatabase() {
        // when
        List<Membership> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void listAvailableOnlyDeletedMemberships() {
        // given
        MembershipType childMembershipType = new MembershipType(MembershipTypeName.CHILD, 0);
        MembershipType adultMembershipType = new MembershipType(MembershipTypeName.ADULT, 500);
        MembershipType familyMembershipType = new MembershipType(MembershipTypeName.FAMILY, 1500);

        Membership membership1 = new Membership(
                familyMembershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        membership1.setDeleted(true);
        Membership membership2 = new Membership(
                childMembershipType,
                LocalDate.of(2019, Month.MAY, 20),
                LocalDate.of(2022, Month.MAY, 20));
        membership2.setDeleted(true);
        Membership membership3 = new Membership(
                adultMembershipType,
                LocalDate.of(2020, Month.JUNE, 2),
                LocalDate.of(2020, Month.JULY, 2));
        membership3.setDeleted(true);

        membershipTypeRepository.saveAll(Collections.unmodifiableList(Arrays.asList(childMembershipType, adultMembershipType, familyMembershipType)));

        List<Membership> memberships = Collections.unmodifiableList(Arrays.asList(membership1, membership2, membership3));
        underTest.saveAll(memberships);

        // when
        List<Membership> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual).hasSize(0);
    }

    @Test
    void listAvailableMultipleAvailableMemberships() {
        // given
        MembershipType childMembershipType = new MembershipType(MembershipTypeName.CHILD, 0);
        MembershipType adultMembershipType = new MembershipType(MembershipTypeName.ADULT, 500);

        Membership membership1 = new Membership(
                childMembershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        Membership membership2 = new Membership(
                childMembershipType,
                LocalDate.of(2019, Month.MAY, 20),
                LocalDate.of(2022, Month.MAY, 20));
        Membership membership3 = new Membership(
                adultMembershipType,
                LocalDate.of(2020, Month.JUNE, 2),
                LocalDate.of(2020, Month.JULY, 2));

        membershipTypeRepository.saveAll(Collections.unmodifiableList(Arrays.asList(childMembershipType, adultMembershipType)));

        List<Membership> memberships = Collections.unmodifiableList(Arrays.asList(membership1, membership2, membership3));
        underTest.saveAll(memberships);

        // when
        List<Membership> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual).hasSize(3).contains(membership1).contains(membership2).contains(membership3);
    }

    @Test
    void listAvailableMultipleAvailableAndDeletedMemberships() {
        // given
        MembershipType childMembershipType = new MembershipType(MembershipTypeName.CHILD, 0);
        MembershipType adultMembershipType = new MembershipType(MembershipTypeName.ADULT, 500);

        Membership membership1 = new Membership(
                childMembershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        membership1.setDeleted(true);
        Membership membership2 = new Membership(
                childMembershipType,
                LocalDate.of(2019, Month.MAY, 20),
                LocalDate.of(2022, Month.MAY, 20));
        Membership membership3 = new Membership(
                adultMembershipType,
                LocalDate.of(2020, Month.JUNE, 2),
                LocalDate.of(2020, Month.JULY, 2));
        membership3.setDeleted(true);
        Membership membership4 = new Membership(
                adultMembershipType,
                LocalDate.of(2020, Month.SEPTEMBER, 3),
                LocalDate.of(2022, Month.SEPTEMBER, 3));
        Membership membership5 = new Membership(
                childMembershipType,
                LocalDate.of(2020, Month.NOVEMBER, 3),
                LocalDate.of(2021, Month.FEBRUARY, 3));
        membership5.setDeleted(true);

        membershipTypeRepository.saveAll(Collections.unmodifiableList(Arrays.asList(childMembershipType, adultMembershipType)));

        List<Membership> memberships = Collections.unmodifiableList(Arrays.asList(membership1, membership2, membership3, membership4, membership5));
        underTest.saveAll(memberships);

        // when
        List<Membership> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual)
                .hasSize(2)
                .containsOnly(membership2, membership4);
    }

    @Test
    void listAvailablePageSizeSmallerThanTotalAmountOfMemberships() {
        // given
        MembershipType childMembershipType = new MembershipType(MembershipTypeName.CHILD, 0);
        MembershipType adultMembershipType = new MembershipType(MembershipTypeName.ADULT, 500);

        Membership membership1 = new Membership(
                childMembershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        membership1.setDeleted(true);
        Membership membership2 = new Membership(
                childMembershipType,
                LocalDate.of(2019, Month.MAY, 20),
                LocalDate.of(2022, Month.MAY, 20));
        Membership membership3 = new Membership(
                adultMembershipType,
                LocalDate.of(2020, Month.JUNE, 2),
                LocalDate.of(2020, Month.JULY, 2));
        membership1.setDeleted(true);
        Membership membership4 = new Membership(
                adultMembershipType,
                LocalDate.of(2020, Month.SEPTEMBER, 3),
                LocalDate.of(2022, Month.SEPTEMBER, 3));

        membershipTypeRepository.saveAll(Collections.unmodifiableList(Arrays.asList(childMembershipType, adultMembershipType)));

        List<Membership> memberships = Collections.unmodifiableList(Arrays.asList(membership1, membership2, membership3, membership4));
        underTest.saveAll(memberships);

        // when
        List<Membership> actual = underTest.listAvailable(ofSize(3));

        // then
        // at least 3 memberships of the list "memberships" appear in actual
        assertThat(actual)
                .hasSize(3)
                .areAtLeast(3, new Condition<>(memberships::contains, "containsNMembershipsOf"));
    }

    @Test
    void listByTypeNoType() {
        // when
        List<Membership> actual = underTest.listByMembershipType(4L, ofSize(10));

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void listByTypeNoMemberships() {
        // when
        List<Membership> actual = underTest.listByMembershipType(1L, ofSize(10));

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void listByTypeMultipleAvailableMemberships() {
        // given
        MembershipType childMembershipType = new MembershipType(MembershipTypeName.CHILD, 0);

        Membership membership1 = new Membership(
                childMembershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        Membership membership2 = new Membership(
                childMembershipType,
                LocalDate.of(2019, Month.MAY, 20),
                LocalDate.of(2022, Month.MAY, 20));


        Long membershipTypeId = membershipTypeRepository.save(childMembershipType).getId();

        List<Membership> memberships = Collections.unmodifiableList(Arrays.asList(membership1, membership2));
        underTest.saveAll(memberships);

        // when
        List<Membership> actual = underTest.listByMembershipType(membershipTypeId, ofSize(10));

        // then
        assertThat(actual)
                .hasSize(2)
                .containsOnly(membership1, membership2);
    }

    @Test
    void listByTypeMultipleAvailableAndDeletedMemberships() {
        // given
        MembershipType childMembershipType = new MembershipType(MembershipTypeName.CHILD, 0);

        Membership membership1 = new Membership(
                childMembershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        membership1.setDeleted(true);
        Membership membership2 = new Membership(
                childMembershipType,
                LocalDate.of(2019, Month.MAY, 20),
                LocalDate.of(2022, Month.MAY, 20));
        Membership membership3 = new Membership(
                childMembershipType,
                LocalDate.of(2020, Month.JUNE, 2),
                LocalDate.of(2020, Month.JULY, 2));
        membership3.setDeleted(true);
        Membership membership4 = new Membership(
                childMembershipType,
                LocalDate.of(2020, Month.SEPTEMBER, 3),
                LocalDate.of(2022, Month.SEPTEMBER, 3));
        Membership membership5 = new Membership(
                childMembershipType,
                LocalDate.of(2020, Month.NOVEMBER, 3),
                LocalDate.of(2021, Month.FEBRUARY, 3));
        membership5.setDeleted(true);


        Long membershipTypeId = membershipTypeRepository.save(childMembershipType).getId();

        List<Membership> memberships = Collections.unmodifiableList(Arrays.asList(membership1, membership2, membership3, membership4, membership5));
        underTest.saveAll(memberships);

        // when
        List<Membership> actual = underTest.listByMembershipType(membershipTypeId, ofSize(10));

        // then
        assertThat(actual)
                .hasSize(2)
                .containsOnly(membership2, membership4);
    }
}