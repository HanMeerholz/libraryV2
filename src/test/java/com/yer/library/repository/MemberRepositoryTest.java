//package com.yer.library.repository;
//
//import com.yer.library.model.Member;
//import org.assertj.core.api.Condition;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.time.LocalDate;
//import java.time.Month;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.data.domain.PageRequest.ofSize;
//
//@DataJpaTest
//class MemberRepositoryTest {
//    @Autowired
//    private MemberRepository underTest;
//
//    @AfterEach
//    void tearDown() {
//        underTest.deleteAll();
//    }
//
//    @Test
//    void findByEmailThatExists() {
//        // given
//        String email = "k.dickens@gmail.com";
//        Member member = new Member(
//                "Kaden Dickens",
//                "835 Vincenza Loaf",
//                email,
//                LocalDate.of(1953, Month.APRIL, 25)
//        );
//
//        underTest.save(member);
//
//        // when
//        Optional<Member> actual = underTest.findByEmail(email);
//
//        // then
//        assertThat(actual.isPresent()).isTrue();
//        assertThat(actual.get()).isNotNull();
//        assertThat(actual.get().getEmailAddress()).isEqualTo(email);
//    }
//
//    @Test
//    void findByEmailThatDoesNotExist() {
//        // given
//        String isbn = "978-2-3915-3957-4";
//
//        // when
//        Optional<Member> actual = underTest.findByEmail(isbn);
//
//        // then
//        assertThat(actual.isPresent()).isFalse();
//    }
//
//    @Test
//    void listAvailableEmptyDatabase() {
//        // when
//        List<Member> actual = underTest.listAvailable(ofSize(10));
//
//        // then
//        assertThat(actual).isEmpty();
//    }
//
//    @Test
//    void listAvailableOnlyDeletedMembers() {
//        Member member1 = new Member(
//                "Kaden Dickens",
//                "835 Vincenza Loaf",
//                "k.dickens@gmail.com",
//                LocalDate.of(1953, Month.APRIL, 25)
//        );
//        member1.setDeleted(true);
//        Member member2 = new Member(
//                "Iain Carter",
//                "950 Poplar St.",
//                "iaincarter@hotmail.com",
//                LocalDate.of(1998, Month.JUNE, 8)
//        );
//        member2.setDeleted(true);
//
//        List<Member> members = Collections.unmodifiableList(Arrays.asList(member1, member2));
//        underTest.saveAll(members);
//
//        // when
//        List<Member> actual = underTest.listAvailable(ofSize(10));
//
//        // then
//        assertThat(actual).hasSize(0);
//    }
//
//    @Test
//    void listAvailableOneAvailableMember() {
//        // given
//        Member member = new Member(
//                "Kaden Dickens",
//                "835 Vincenza Loaf",
//                "k.dickens@gmail.com",
//                LocalDate.of(1953, Month.APRIL, 25)
//        );
//        underTest.save(member);
//
//        // when
//        List<Member> actual = underTest.listAvailable(ofSize(10));
//
//        // then
//        assertThat(actual).hasSize(1).contains(member);
//    }
//
//    @Test
//    void listAvailableMultipleAvailableMembers() {
//        // given
//        Member member1 = new Member(
//                "Kaden Dickens",
//                "835 Vincenza Loaf",
//                "k.dickens@gmail.com",
//                LocalDate.of(1953, Month.APRIL, 25)
//        );
//        Member member2 = new Member(
//                "Iain Carter",
//                "950 Poplar St.",
//                "iaincarter@hotmail.com",
//                LocalDate.of(1998, Month.JUNE, 8)
//        );
//
//        List<Member> members = Collections.unmodifiableList(Arrays.asList(member1, member2));
//        underTest.saveAll(members);
//
//        // when
//        List<Member> actual = underTest.listAvailable(ofSize(10));
//
//        // then
//        assertThat(actual).hasSize(2).contains(member1).contains(member2);
//    }
//
//    @Test
//    void listAvailableMultipleAvailableAndDeletedMembers() {
//        // given
//        Member member1 = new Member(
//                "Kaden Dickens",
//                "835 Vincenza Loaf",
//                "k.dickens@gmail.com",
//                LocalDate.of(1953, Month.APRIL, 25)
//        );
//        member1.setDeleted(true);
//        Member member2 = new Member(
//                "Iain Carter",
//                "950 Poplar St.",
//                "iaincarter@hotmail.com",
//                LocalDate.of(1998, Month.JUNE, 8)
//        );
//        Member member3 = new Member(
//                "Kylo Finch",
//                null,
//                "kylo.finch@yahoo.com",
//                LocalDate.of(1943, Month.MAY, 7)
//        );
//        member3.setDeleted(true);
//        Member member4 = new Member(
//                "Henrietta Goodwin",
//                "651 Santa Clara Street",
//                "henrigoodwin@gmail.com",
//                LocalDate.of(1961, Month.MARCH, 13)
//        );
//
//        List<Member> members = Collections.unmodifiableList(Arrays.asList(member1, member2, member3, member4));
//        underTest.saveAll(members);
//
//        // when
//        List<Member> actual = underTest.listAvailable(ofSize(10));
//
//        // then
//        assertThat(actual)
//                .hasSize(2)
//                .doesNotContain(member1)
//                .contains(member2)
//                .doesNotContain(member3)
//                .contains(member4);
//    }
//
//    @Test
//    void listAvailablePageSizeSmallerThanTotalAmountOfMembers() {
//        // given
//        Member member1 = new Member(
//                "Kaden Dickens",
//                "835 Vincenza Loaf",
//                "k.dickens@gmail.com",
//                LocalDate.of(1953, Month.APRIL, 25)
//        );
//        Member member2 = new Member(
//                "Iain Carter",
//                "950 Poplar St.",
//                "iaincarter@hotmail.com",
//                LocalDate.of(1998, Month.JUNE, 8)
//        );
//        Member member3 = new Member(
//                "Kylo Finch",
//                null,
//                "kylo.finch@yahoo.com",
//                LocalDate.of(1943, Month.MAY, 7)
//        );
//        Member member4 = new Member(
//                "Henrietta Goodwin",
//                "651 Santa Clara Street",
//                "henrigoodwin@gmail.com",
//                LocalDate.of(1961, Month.MARCH, 13)
//        );
//
//        List<Member> members = Collections.unmodifiableList(Arrays.asList(member1, member2, member3, member4));
//        underTest.saveAll(members);
//
//        // when
//        List<Member> actual = underTest.listAvailable(ofSize(3));
//
//        // then
//        // at least 3 members of the list "members" appear in actual
//        assertThat(actual).hasSize(3).areAtLeast(3, new Condition<>(members::contains, "containsNBooksOf"));
//    }
//}