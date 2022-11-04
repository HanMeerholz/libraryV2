package com.yer.library.repository;

import com.yer.library.model.Member;
import com.yer.library.model.Membership;
import com.yer.library.model.MembershipType;
import com.yer.library.model.enums.MembershipTypeName;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.PageRequest.ofSize;

@DataJpaTest
class MemberRepositoryTest {
    @Autowired
    private MemberRepository underTest;
    @Autowired
    private MembershipRepository membershipRepository;
    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByEmailThatExists() {
        // given
        MembershipType childMembershipType = new MembershipType(MembershipTypeName.CHILD, 0);

        Membership membership = new Membership(
                childMembershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));

        String email = "k.dickens@gmail.com";
        Member member = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                email,
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );

        membershipTypeRepository.save(childMembershipType);
        membershipRepository.save(membership);

        underTest.save(member);

        // when
        Optional<Member> actual = underTest.findByEmail(email);

        // then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isNotNull();
        assertThat(actual.get().getEmailAddress()).isEqualTo(email);
    }

    @Test
    void findByEmailThatDoesNotExist() {
        // given
        String email = "k.dickens@gmail.com";

        // when
        Optional<Member> actual = underTest.findByEmail(email);

        // then
        assertThat(actual.isPresent()).isFalse();
    }

    @Test
    void findByEmailForDeletedMember() {
        // given
        MembershipType childMembershipType = new MembershipType(MembershipTypeName.CHILD, 0);

        Membership membership = new Membership(
                childMembershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));

        String email = "k.dickens@gmail.com";
        Member member = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                email,
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );
        member.setDeleted(true);

        membershipTypeRepository.save(childMembershipType);
        membershipRepository.save(membership);
        underTest.save(member);

        // when
        Optional<Member> actual = underTest.findByEmail(email);

        // then
        assertThat(actual.isPresent()).isFalse();
    }

    @Test
    void listAvailableEmptyDatabase() {
        // when
        List<Member> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void listAvailableOnlyDeletedMembers() {
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.FAMILY, 1500
        );

        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );

        Member member1 = new Member(
                "Iain Carter",
                "950 Poplar St.",
                "iaincarter@hotmail.com",
                LocalDate.of(1998, Month.JUNE, 8),
                membership
        );
        member1.setDeleted(true);
        Member member2 = new Member(
                "Harry Carter",
                "950 Poplar St.",
                "harrycarter@hotmail.com",
                LocalDate.of(1996, Month.JULY, 4),
                membership
        );
        member2.setDeleted(true);

        membershipTypeRepository.save(membershipType);
        membershipRepository.save(membership);
        List<Member> members = Collections.unmodifiableList(Arrays.asList(member1, member2));
        underTest.saveAll(members);

        // when
        List<Member> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual).hasSize(0);
    }

    @Test
    void listAvailableOneAvailableMember() {
        // given
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.FAMILY, 1500
        );

        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );

        Member member = new Member(
                "Iain Carter",
                "950 Poplar St.",
                "iaincarter@hotmail.com",
                LocalDate.of(1998, Month.JUNE, 8),
                membership
        );

        membershipTypeRepository.save(membershipType);
        membershipRepository.save(membership);
        underTest.save(member);

        // when
        List<Member> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual).hasSize(1).containsOnly(member);
    }

    @Test
    void listAvailableMultipleAvailableMembers() {
        // given
        MembershipType membershipTypeFamily = new MembershipType(
                MembershipTypeName.FAMILY, 1500
        );
        MembershipType membershipTypeAdult = new MembershipType(
                MembershipTypeName.ADULT, 500
        );

        Membership membership1 = new Membership(
                membershipTypeFamily,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        Membership membership2 = new Membership(
                membershipTypeAdult,
                LocalDate.of(2020, Month.JUNE, 2),
                LocalDate.of(2020, Month.JULY, 2)
        );

        Member member1 = new Member(
                "Iain Carter",
                "950 Poplar St.",
                "iaincarter@hotmail.com",
                LocalDate.of(1998, Month.JUNE, 8),
                membership1
        );
        Member member2 = new Member(
                "Harry Carter",
                "950 Poplar St.",
                "harrycarter@hotmail.com",
                LocalDate.of(1996, Month.JULY, 4),
                membership1
        );
        Member member3 = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                membership2
        );

        List<MembershipType> membershipTypes = Collections.unmodifiableList(Arrays.asList(membershipTypeFamily, membershipTypeAdult));
        membershipTypeRepository.saveAll(membershipTypes);
        List<Membership> memberships = Collections.unmodifiableList(Arrays.asList(membership1, membership2));
        membershipRepository.saveAll(memberships);
        List<Member> members = Collections.unmodifiableList(Arrays.asList(member1, member2, member3));
        underTest.saveAll(members);

        // when
        List<Member> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual).hasSize(3).containsOnly(member1, member2, member3);
    }

    @Test
    void listAvailableMultipleAvailableAndDeletedMembers() {
        // given
        MembershipType membershipTypeFamily = new MembershipType(
                MembershipTypeName.FAMILY, 1500
        );
        MembershipType membershipTypeAdult = new MembershipType(
                MembershipTypeName.ADULT, 500
        );

        Membership membership1 = new Membership(
                membershipTypeFamily,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        Membership membership2 = new Membership(
                membershipTypeAdult,
                LocalDate.of(2020, Month.JUNE, 13),
                LocalDate.of(2020, Month.SEPTEMBER, 13)
        );
        Membership membership3 = new Membership(
                membershipTypeAdult,
                LocalDate.of(2021, Month.JANUARY, 1),
                LocalDate.of(2023, Month.JANUARY, 1)
        );

        Member member1 = new Member(
                "Iain Carter",
                "950 Poplar St.",
                "iaincarter@hotmail.com",
                LocalDate.of(1998, Month.JUNE, 8),
                membership1
        );
        member1.setDeleted(true);
        Member member2 = new Member(
                "Harry Carter",
                "950 Poplar St.",
                "harrycarter@hotmail.com",
                LocalDate.of(1996, Month.JULY, 4),
                membership1
        );
        Member member3 = new Member(
                "Henrietta Goodwin",
                "651 Santa Clara Street",
                "henrigoodwin@gmail.com",
                LocalDate.of(1961, Month.MARCH, 13),
                membership1
        );
        member3.setDeleted(true);

        Member member4 = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                membership2
        );
        Member member5 = new Member(
                "Kylo Finch",
                null,
                "kylo.finch@yahoo.com",
                LocalDate.of(1943, Month.MAY, 7),
                membership3
        );
        member5.setDeleted(true);

        List<MembershipType> membershipTypes = Collections.unmodifiableList(Arrays.asList(membershipTypeFamily, membershipTypeAdult));
        membershipTypeRepository.saveAll(membershipTypes);
        List<Membership> memberships = Collections.unmodifiableList(Arrays.asList(membership1, membership2, membership3));
        membershipRepository.saveAll(memberships);
        List<Member> members = Collections.unmodifiableList(Arrays.asList(member1, member2, member3, member4, member5));
        underTest.saveAll(members);

        // when
        List<Member> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual)
                .hasSize(2)
                .containsOnly(member2, member4);
    }

    @Test
    void listAvailablePageSizeSmallerThanTotalAmountOfMembers() {
        // given
        MembershipType membershipTypeFamily = new MembershipType(
                MembershipTypeName.FAMILY, 1500
        );
        MembershipType membershipTypeAdult = new MembershipType(
                MembershipTypeName.ADULT, 500
        );

        Membership membership1 = new Membership(
                membershipTypeFamily,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        Membership membership2 = new Membership(
                membershipTypeAdult,
                LocalDate.of(2020, Month.JUNE, 13),
                LocalDate.of(2020, Month.SEPTEMBER, 13)
        );

        Member member1 = new Member(
                "Iain Carter",
                "950 Poplar St.",
                "iaincarter@hotmail.com",
                LocalDate.of(1998, Month.JUNE, 8),
                membership1
        );
        Member member2 = new Member(
                "Harry Carter",
                "950 Poplar St.",
                "harrycarter@hotmail.com",
                LocalDate.of(1996, Month.JULY, 4),
                membership1
        );
        Member member3 = new Member(
                "Henrietta Goodwin",
                "651 Santa Clara Street",
                "henrigoodwin@gmail.com",
                LocalDate.of(1961, Month.MARCH, 13),
                membership1
        );
        Member member4 = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                membership2
        );

        List<MembershipType> membershipTypes = Collections.unmodifiableList(Arrays.asList(membershipTypeFamily, membershipTypeAdult));
        membershipTypeRepository.saveAll(membershipTypes);
        List<Membership> memberships = Collections.unmodifiableList(Arrays.asList(membership1, membership2));
        membershipRepository.saveAll(memberships);
        List<Member> members = Collections.unmodifiableList(Arrays.asList(member1, member2, member3, member4));
        underTest.saveAll(members);

        // when
        List<Member> actual = underTest.listAvailable(ofSize(3));

        // then
        // at least 3 members of the list "members" appear in actual
        assertThat(actual).hasSize(3).areAtLeast(3, new Condition<>(members::contains, "containsNBooksOf"));
    }

    @Test
    void listByMembershipNoMembership() {
        // when
        List<Member> actual = underTest.listByMembership(1L, ofSize(10));

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void listByMembershipNoMembers() {
        // given
        MembershipType membershipTypeFamily = new MembershipType(
                MembershipTypeName.FAMILY, 1500
        );
        Long membershipId = 1L;
        Membership membership = new Membership(
                membershipTypeFamily,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        membership.setId(membershipId);

        membershipTypeRepository.save(membershipTypeFamily);
        membershipRepository.save(membership);

        // when
        List<Member> actual = underTest.listByMembership(membershipId, ofSize(10));

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void listByMembershipMultipleAvailableMembers() {
        // given
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.FAMILY, 1500
        );

        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );

        Member member1 = new Member(
                "Iain Carter",
                "950 Poplar St.",
                "iaincarter@hotmail.com",
                LocalDate.of(1998, Month.JUNE, 8),
                membership
        );
        Member member2 = new Member(
                "Harry Carter",
                "950 Poplar St.",
                "harrycarter@hotmail.com",
                LocalDate.of(1996, Month.JULY, 4),
                membership
        );

        membershipTypeRepository.save(membershipType);
        Long membershipId = membershipRepository.save(membership).getId();
        List<Member> members = Collections.unmodifiableList(Arrays.asList(member1, member2));
        underTest.saveAll(members);

        // when
        List<Member> actual = underTest.listByMembership(membershipId, ofSize(10));

        // then
        assertThat(actual)
                .hasSize(2)
                .containsOnly(member1, member2);
    }

    @Test
    void listByMembershipMultipleAvailableAndDeletedMembers() {
        // given
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.FAMILY, 1500
        );

        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );

        Member member1 = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );
        member1.setDeleted(true);
        Member member2 = new Member(
                "Iain Carter",
                "950 Poplar St.",
                "iaincarter@hotmail.com",
                LocalDate.of(1998, Month.JUNE, 8),
                membership
        );
        Member member3 = new Member(
                "Harry Carter",
                "950 Poplar St.",
                "harrycarter@hotmail.com",
                LocalDate.of(1996, Month.JULY, 4),
                membership
        );
        member3.setDeleted(true);
        Member member4 = new Member(
                "Kylo Finch",
                null,
                "kylo.finch@yahoo.com",
                LocalDate.of(1943, Month.MAY, 7),
                membership
        );
        Member member5 = new Member(
                "Henrietta Goodwin",
                "651 Santa Clara Street",
                "henrigoodwin@gmail.com",
                LocalDate.of(1961, Month.MARCH, 13),
                membership
        );
        member5.setDeleted(true);

        membershipTypeRepository.save(membershipType);
        Long membershipId = membershipRepository.save(membership).getId();
        List<Member> members = Collections.unmodifiableList(Arrays.asList(member1, member2, member3, member4, member5));
        underTest.saveAll(members);

        // when
        List<Member> actual = underTest.listByMembership(membershipId, ofSize(10));

        // then
        assertThat(actual)
                .hasSize(2)
                .containsOnly(member2, member4);
    }
}