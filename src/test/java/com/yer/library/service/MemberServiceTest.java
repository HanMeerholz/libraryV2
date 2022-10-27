//package com.yer.library.service;
//
//import com.yer.library.model.Member;
//import com.yer.library.repository.MemberRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Pageable;
//
//import java.time.LocalDate;
//import java.time.Month;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.argThat;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.verify;
//
//@ExtendWith(MockitoExtension.class)
//class MemberServiceTest {
//    @Mock
//    private MemberRepository memberRepository;
//
//    @InjectMocks
//    private MemberService underTest;
//
//    @Test
//    void getExistingNonDeletedMember() {
//        // given
//        Long memberId = 1L;
//
//        Member member = new Member(
//                "Kaden Dickens",
//                "835 Vincenza Loaf",
//                "k.dickens@gmail.com",
//                LocalDate.of(1953, Month.APRIL, 25)
//        );
//        member.setId(memberId);
//        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
//
//        // when
//        Member returnedMember = underTest.get(memberId);
//
//        // then
//        verify(memberRepository).findById(
//                argThat(id -> id.equals(memberId))
//        );
//        assertThat(returnedMember).isEqualTo(member);
//    }
//
//    @Test
//    void getExistingDeletedMember() {
//        // given
//        Long memberId = 1L;
//        Member member = new Member(
//                "Kaden Dickens",
//                "835 Vincenza Loaf",
//                "k.dickens@gmail.com",
//                LocalDate.of(1953, Month.APRIL, 25)
//        );
//        member.setId(memberId);
//        member.setDeleted(true);
//        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
//
//        // when
//        assertThatThrownBy(() -> underTest.get(memberId))
//                .isInstanceOf(IllegalStateException.class)
//                .hasMessageContaining("member with id " + memberId + " does not exist");
//        verify(memberRepository).findById(
//                argThat(id -> id.equals(memberId))
//        );
//        verify(memberRepository, never()).save(any());
//    }
//
//    @Test
//    void getNonExistingMember() {
//        // given
//        Long memberId = 1L;
//
//        given(memberRepository.findById(memberId)).willReturn(Optional.empty());
//
//        // when
//        // then
//        assertThatThrownBy(() -> underTest.get(memberId))
//                .isInstanceOf(IllegalStateException.class)
//                .hasMessageContaining("member with id " + memberId + " does not exist");
//        verify(memberRepository).findById(
//                argThat(id -> id.equals(memberId))
//        );
//        verify(memberRepository, never()).save(any());
//    }
//
//    @Test
//    void list() {
//        // given
//        int limit = 100;
//
//        // when
//        underTest.list(limit);
//
//        // then
//        verify(memberRepository).listAvailable(
//                argThat(pageable -> pageable.equals(Pageable.ofSize(limit)))
//        );
//    }
//
//    @Test
//    void addValidMember() {
//        // given
//        Long memberId = 1L;
//
//        Member member = new Member(
//                "Kaden Dickens",
//                "835 Vincenza Loaf",
//                "k.dickens@gmail.com",
//                LocalDate.of(1953, Month.APRIL, 25)
//        );
//        Member expectedReturnedMember = new Member(
//                "Kaden Dickens",
//                "835 Vincenza Loaf",
//                "k.dickens@gmail.com",
//                LocalDate.of(1953, Month.APRIL, 25)
//        );
//        expectedReturnedMember.setId(memberId);
//        given(memberRepository.save(member)).willReturn(expectedReturnedMember);
//
//        // when
//        Member returnedMember = underTest.add(member);
//
//        // then
//        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
//        verify(memberRepository).save(memberArgumentCaptor.capture());
//        Member capturedMember = memberArgumentCaptor.getValue();
//
//        assertThat(capturedMember).isEqualTo(member);
//        assertThat(returnedMember).isEqualTo(expectedReturnedMember);
//    }
//
//    @Test
//    void addMemberWithEmailTakenByExistingMember() {
//        // given
//        String email = "k.dickens@gmail.com";
//        Member member1 = new Member(
//                "Kaden Dickens",
//                "835 Vincenza Loaf",
//                email,
//                LocalDate.of(1953, Month.APRIL, 25)
//        );
//        Member member2 = new Member(
//                "Iain Carter",
//                "950 Poplar St.",
//                email,
//                LocalDate.of(1998, Month.JUNE, 8)
//        );
//
//        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member1));
//
//        // when
//        // then
//        assertThatThrownBy(() -> underTest.add(member2))
//                .isInstanceOf(IllegalStateException.class)
//                .hasMessageContaining("email " + member2.getEmailAddress() + " already exists");
//        verify(memberRepository, never()).save(any());
//    }
//
//    @Test
//    void addMemberWithEmailTakenByDeletedMember() {
//        // given
//        Long deletedMemberInDBId = 1L;
//        String email = "k.dickens@gmail.com";
//        Member deletedMemberInDB = new Member(
//                "Kaden Dickens",
//                "835 Vincenza Loaf",
//                email,
//                LocalDate.of(1953, Month.APRIL, 25)
//        );
//        deletedMemberInDB.setId(deletedMemberInDBId);
//        deletedMemberInDB.setDeleted(true);
//
//        Member newMember = new Member(
//                "Iain Carter",
//                "950 Poplar St.",
//                email,
//                LocalDate.of(1998, Month.JUNE, 8)
//        );
//        Member expectedReturnedMember = new Member(
//                "Iain Carter",
//                "950 Poplar St.",
//                email,
//                LocalDate.of(1998, Month.JUNE, 8)
//        );
//        expectedReturnedMember.setId(deletedMemberInDBId);
//
//        given(memberRepository.save(newMember)).willReturn(expectedReturnedMember);
//        given(memberRepository.findByEmail(email)).willReturn(Optional.of(deletedMemberInDB));
//
//        // when
//        Member returnedMember = underTest.add(newMember);
//
//        // then
//        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
//        verify(memberRepository).save(memberArgumentCaptor.capture());
//        Member capturedMember = memberArgumentCaptor.getValue();
//
//        assertThat(capturedMember).isEqualTo(expectedReturnedMember);
//        assertThat(returnedMember).isEqualTo(expectedReturnedMember);
//    }
//
//    @Test
//    void fullUpdateExistingMemberSameEmail() {
//        // given
//        Long memberId = 1L;
//        String email = "k.dickens@gmail.com";
//        Member initialMember = new Member(
//                "Kaden Dickens",
//                "835 Vincenza Loaf",
//                email,
//                LocalDate.of(1953, Month.APRIL, 25)
//        );
//        initialMember.setId(memberId);
//
//        Member updatedMember = new Member(
//                "Kaden Hickens",
//                "834 Vincenza Loaf",
//                email,
//                LocalDate.of(1953, Month.MAY, 25)
//        );
//
//        Member expectedReturnedMember = new Member(
//                "Kaden Hickens",
//                "834 Vincenza Loaf",
//                email,
//                LocalDate.of(1953, Month.MAY, 25)
//        );
//        expectedReturnedMember.setId(memberId);
//
//        given(memberRepository.findById(memberId)).willReturn(Optional.of(initialMember));
//        given(memberRepository.save(updatedMember)).willReturn(expectedReturnedMember);
//
//        // when
//        Member returnedMember = underTest.fullUpdate(memberId, updatedMember);
//
//        // then
//        verify(memberRepository).findById(
//                argThat(id -> id.equals(memberId))
//        );
//
//        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
//        verify(memberRepository).save(memberArgumentCaptor.capture());
//        Member capturedMember = memberArgumentCaptor.getValue();
//
//        assertThat(capturedMember).isEqualTo(updatedMember);
//        assertThat(returnedMember).isEqualTo(expectedReturnedMember);
//    }
//
//    @Test
//    void fullUpdateNonExistingMember() {
//        // given
//        Long memberId = 1L;
//        String email = "k.dickens@gmail.com";
//
//        Member updatedMember = new Member(
//                "Kaden Hickens",
//                "834 Vincenza Loaf",
//                email,
//                LocalDate.of(1953, Month.MAY, 25)
//        );
//        given(memberRepository.findById(memberId)).willReturn(Optional.empty());
//
//        // when
//        // then
//        assertThatThrownBy(() -> underTest.fullUpdate(memberId, updatedMember))
//                .isInstanceOf(IllegalStateException.class)
//                .hasMessageContaining("member with id " + memberId + " does not exist");
//        verify(memberRepository).findById(
//                argThat(id -> id.equals(memberId))
//        );
//        verify(memberRepository, never()).save(any());
//    }
//
//    @Test
//    void fullUpdateExistingMemberNewEmailExistsForNonDeletedMember() {
//        // given
//        Long member1Id = 1L;
//        Member existingMember1 = new Member(
//                "Kaden Dickens",
//                "835 Vincenza Loaf",
//                "k.dickens@gmail.com",
//                LocalDate.of(1953, Month.APRIL, 25)
//        );
//        existingMember1.setId(member1Id);
//
//        Long member2Id = 2L;
//        String newEmail = "iaincarter@hotmail.com";
//        Member existingMember2 = new Member(
//                "Iain Carter",
//                "950 Poplar St.",
//                newEmail,
//                LocalDate.of(1998, Month.JUNE, 8)
//        );
//        existingMember2.setId(member2Id);
//
//        Member updatedMember = new Member(
//                "Kaden Hickens",
//                "834 Vincenza Loaf",
//                newEmail,
//                LocalDate.of(1953, Month.MAY, 25)
//        );
//
//        given(memberRepository.findById(member1Id)).willReturn(Optional.of(existingMember1));
//        given(memberRepository.findByEmail(newEmail)).willReturn(Optional.of(existingMember2));
//
//        // when
//        // then
//        assertThatThrownBy(() -> underTest.fullUpdate(member1Id, updatedMember))
//                .isInstanceOf(IllegalStateException.class)
//                .hasMessageContaining("email " + newEmail + " already exists");
//        verify(memberRepository).findById(
//                argThat(id -> id.equals(member1Id))
//        );
//        verify(memberRepository, never()).save(any());
//    }
//
//    @Test
//    void deleteExistingNonDeletedMember() {
//        // given
//        Long memberId = 1L;
//        Member existingMember = new Member(
//                "Kaden Dickens",
//                "835 Vincenza Loaf",
//                "k.dickens@gmail.com",
//                LocalDate.of(1953, Month.APRIL, 25)
//        );
//        existingMember.setId(memberId);
//        given(memberRepository.findById(memberId)).willReturn(Optional.of(existingMember));
//
//        // when
//        Boolean result = underTest.delete(memberId);
//
//        // then
//        verify(memberRepository).findById(
//                argThat(id -> id.equals(memberId))
//        );
//        assertThat(result).isTrue();
//        assertThat(existingMember.getDeleted()).isTrue();
//    }
//
//    @Test
//    void deleteExistingDeletedMember() {
//        // given
//        Long memberId = 1L;
//        Member existingMember = new Member(
//                "Kaden Dickens",
//                "835 Vincenza Loaf",
//                "k.dickens@gmail.com",
//                LocalDate.of(1953, Month.APRIL, 25)
//        );
//        existingMember.setDeleted(true);
//        existingMember.setId(memberId);
//        given(memberRepository.findById(memberId)).willReturn(Optional.of(existingMember));
//
//        // when
//        // then
//        assertThatThrownBy(() -> underTest.delete(memberId))
//                .isInstanceOf(IllegalStateException.class)
//                .hasMessageContaining("member with id " + memberId + " has already been deleted");
//        verify(memberRepository).findById(
//                argThat(id -> id.equals(memberId))
//        );
//    }
//
//    @Test
//    void deleteNonExistingMember() {
//        // given
//        Long memberId = 1L;
//        given(memberRepository.findById(memberId)).willReturn(Optional.empty());
//
//        // when
//        // then
//        assertThatThrownBy(() -> underTest.delete(memberId))
//                .isInstanceOf(IllegalStateException.class)
//                .hasMessageContaining("member with id " + memberId + " does not exist");
//        verify(memberRepository).findById(
//                argThat(id -> id.equals(memberId))
//        );
//    }
//}