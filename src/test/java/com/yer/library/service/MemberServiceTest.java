package com.yer.library.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.yer.library.model.Member;
import com.yer.library.model.Membership;
import com.yer.library.model.MembershipType;
import com.yer.library.model.MembershipTypeName;
import com.yer.library.repository.MemberRepository;
import com.yer.library.repository.MembershipRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private Logger logger;

    @InjectMocks
    @Spy
    private MemberService underTest;

    @Test
    void getExistingNonDeletedMember() {
        // given
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        Long memberId = 1L;

        Member member = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );
        member.setId(memberId);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // when
        Member returnedMember = underTest.get(memberId);

        // then
        verify(memberRepository).findById(
                argThat(id -> id.equals(memberId))
        );
        assertThat(returnedMember).isEqualTo(member);
    }

    @Test
    void getExistingDeletedMember() {
        // given
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        membershipType.setId(1L);
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        membership.setId(1L);
        Long memberId = 1L;
        Member member = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );
        member.setId(memberId);
        member.setDeleted(true);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // when
        assertThatThrownBy(() -> underTest.get(memberId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("member with ID " + memberId + " does not exist");
        verify(memberRepository).findById(
                argThat(id -> id.equals(memberId))
        );
        verify(memberRepository, never()).save(any());
    }

    @Test
    void getNonExistingMember() {
        // given
        Long memberId = 1L;

        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.get(memberId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("member with ID " + memberId + " does not exist");
        verify(memberRepository).findById(
                argThat(id -> id.equals(memberId))
        );
        verify(memberRepository, never()).save(any());
    }

    @Test
    void list() {
        // given
        int limit = 100;

        // when
        underTest.list(limit);

        // then
        verify(memberRepository).listAvailable(
                argThat(pageable -> pageable.equals(Pageable.ofSize(limit)))
        );
    }

    @Test
    void listByBook() {
        // given
        Long membershipId = 1L;
        int limit = 100;

        // when
        underTest.listByMembership(membershipId, limit);

        // then
        verify(memberRepository).listByMembership(eq(membershipId), argThat(
                pageable -> pageable.equals(Pageable.ofSize(limit))
        ));
    }

    @Test
    void addValidMemberWithMembership() {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long membershipId = 1L;
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        membership.setId(membershipId);

        Long memberId = 1L;
        Member member = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );
        Member expectedReturnedMember = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );
        expectedReturnedMember.setId(memberId);


        given(memberRepository.save(member)).willReturn(expectedReturnedMember);

        // when
        Member returnedMember = underTest.add(member);

        // then
        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberArgumentCaptor.capture());
        Member capturedMember = memberArgumentCaptor.getValue();

        assertThat(capturedMember).isEqualTo(member);
        assertThat(returnedMember).isEqualTo(expectedReturnedMember);
    }

    @Test
    void addMemberWithMembershipWithEmailTakenByExistingMember() {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        membershipType.setId(membershipTypeId);

        Long membership1Id = 1L;
        Membership membership1 = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        membership1.setId(membership1Id);

        Long membership2Id = 2L;
        Membership membership2 = new Membership(
                membershipType,
                LocalDate.of(2020, Month.MAY, 12),
                LocalDate.of(2021, Month.MAY, 12));
        membership2.setId(membership2Id);

        String email = "k.dickens@gmail.com";
        Member member1 = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                email,
                LocalDate.of(1953, Month.APRIL, 25),
                membership1
        );
        Member member2 = new Member(
                "Iain Carter",
                "950 Poplar St.",
                email,
                LocalDate.of(1998, Month.JUNE, 8),
                membership2
        );

        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member1));

        // when
        // then
        assertThatThrownBy(() -> underTest.add(member2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("email " + member2.getEmailAddress() + " already exists");
        verify(memberRepository, never()).save(any());
    }

    @Test
    void addMemberWithEmailTakenByDeletedMember() {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.ADULT, 500
        );
        membershipType.setId(membershipTypeId);

        Long membership1Id = 1L;
        Membership membership1 = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        membership1.setId(membership1Id);

        Long membership2Id = 2L;
        Membership membership2 = new Membership(
                membershipType,
                LocalDate.of(2020, Month.MAY, 12),
                LocalDate.of(2021, Month.MAY, 12));
        membership2.setId(membership2Id);

        Long deletedMemberInDBId = 1L;
        String email = "k.dickens@gmail.com";
        Member deletedMemberInDB = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                email,
                LocalDate.of(1953, Month.APRIL, 25),
                membership1
        );
        deletedMemberInDB.setId(deletedMemberInDBId);
        deletedMemberInDB.setDeleted(true);

        Member newMember = new Member(
                "Iain Carter",
                "950 Poplar St.",
                email,
                LocalDate.of(1998, Month.JUNE, 8),
                membership2
        );
        Member expectedReturnedMember = new Member(
                "Iain Carter",
                "950 Poplar St.",
                email,
                LocalDate.of(1998, Month.JUNE, 8),
                membership2
        );
        expectedReturnedMember.setId(deletedMemberInDBId);

        given(memberRepository.save(newMember)).willReturn(expectedReturnedMember);
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(deletedMemberInDB));

        // when
        Member returnedMember = underTest.add(newMember);

        // then
        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberArgumentCaptor.capture());
        Member capturedMember = memberArgumentCaptor.getValue();

        assertThat(capturedMember).isEqualTo(expectedReturnedMember);
        assertThat(returnedMember).isEqualTo(expectedReturnedMember);
    }

    @Test
    void addMemberWithoutMembershipId() {
        // given
        Long memberId = 1L;

        Member member = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                null
        );

        Member memberCopy = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                null
        );

        Member expectedReturnedMember = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                null
        );
        expectedReturnedMember.setId(memberId);

        // We pass in an "any" argument since the specific member has a null id and
        // will return false if .equals() is called on it
        willReturn(expectedReturnedMember).given(underTest).add(any(Member.class));

        // when
        Member returnedMember = underTest.add(member, null);

        // then
        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(underTest).add(memberArgumentCaptor.capture());
        Member capturedMember = memberArgumentCaptor.getValue();

        // Since memberId is null, equals() won't work, so we compare fields instead
        assertThat(capturedMember.getId()).isEqualTo(memberCopy.getId());
        assertThat(capturedMember.getName()).isEqualTo(memberCopy.getName());
        assertThat(capturedMember.getEmailAddress()).isEqualTo(memberCopy.getEmailAddress());
        assertThat(capturedMember.getHomeAddress()).isEqualTo(memberCopy.getHomeAddress());
        assertThat(capturedMember.getBirthday()).isEqualTo(memberCopy.getBirthday());
        assertThat(capturedMember.getMembership()).isEqualTo(memberCopy.getMembership());
        assertThat(capturedMember.getDeleted()).isEqualTo(memberCopy.getDeleted());

        assertThat(returnedMember).isEqualTo(expectedReturnedMember);
    }

    @Test
    void addMemberForNonExistingMembership() {
        // given
        Long membershipId = 1L;
        Member member = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                null
        );
        given(membershipRepository.findById(membershipId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.add(member, membershipId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot add member for membership: membership with ID " + membershipId + " does not exist");
        verify(memberRepository, never()).save(any());
    }

    @Test
    void addValidMember() {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long membershipId = 1L;
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        membership.setId(membershipId);

        Member member = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                null
        );
        Member memberWithMembership = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );

        Long memberId = 1L;
        Member expectedReturnedMember = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );
        expectedReturnedMember.setId(memberId);


        ///
        willReturn(Optional.of(membership)).given(membershipRepository).findById(membershipId);
        // We pass in an "any" argument since the specific membership has a null id and
        // will return false if .equals() is called on it
        willReturn(expectedReturnedMember).given(underTest).add(any(Member.class));

        // when
        Member returnedMember = underTest.add(member, membershipId);

        // then
        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(underTest).add(memberArgumentCaptor.capture());
        Member capturedMember = memberArgumentCaptor.getValue();

        // Since memberId is null, equals() won't work, so we compare fields instead
        assertThat(capturedMember.getId()).isEqualTo(memberWithMembership.getId());
        assertThat(capturedMember.getName()).isEqualTo(memberWithMembership.getName());
        assertThat(capturedMember.getEmailAddress()).isEqualTo(memberWithMembership.getEmailAddress());
        assertThat(capturedMember.getHomeAddress()).isEqualTo(memberWithMembership.getHomeAddress());
        assertThat(capturedMember.getBirthday()).isEqualTo(memberWithMembership.getBirthday());
        assertThat(capturedMember.getMembership()).isEqualTo(memberWithMembership.getMembership());
        assertThat(capturedMember.getDeleted()).isEqualTo(memberWithMembership.getDeleted());

        assertThat(returnedMember).isEqualTo(expectedReturnedMember);
    }

    @Test
    void fullUpdateValidMemberWithMembershipSameEmail() {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long membershipId = 1L;
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        membership.setId(membershipId);

        Long memberId = 1L;
        Member initialMember = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 26),
                membership
        );
        initialMember.setId(memberId);

        Member updatedMember = new Member(
                "Cayden Dickens",
                "836 Vincenza Loaf",
                "c.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 26),
                membership
        );
        Member updatedMemberCopyWithId = new Member(
                "Cayden Dickens",
                "836 Vincenza Loaf",
                "c.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 26),
                membership
        );
        Member expectedReturnedMember = new Member(
                "Cayden Dickens",
                "836 Vincenza Loaf",
                "c.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 26),
                membership
        );
        expectedReturnedMember.setId(memberId);

        given(memberRepository.save(updatedMember)).willReturn(expectedReturnedMember);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(initialMember));

        // when
        Member returnedMember = underTest.fullUpdate(memberId, updatedMember);
        updatedMemberCopyWithId.setId(returnedMember.getId());

        // then
        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberArgumentCaptor.capture());
        Member capturedMember = memberArgumentCaptor.getValue();

        assertThat(capturedMember).isSameAs(updatedMember);

        assertThat(capturedMember.getId()).isEqualTo(updatedMember.getId());
        assertThat(capturedMember.getName()).isEqualTo(updatedMember.getName());
        assertThat(capturedMember.getEmailAddress()).isEqualTo(updatedMember.getEmailAddress());
        assertThat(capturedMember.getHomeAddress()).isEqualTo(updatedMember.getHomeAddress());
        assertThat(capturedMember.getBirthday()).isEqualTo(updatedMember.getBirthday());
        assertThat(capturedMember.getMembership()).isEqualTo(updatedMember.getMembership());
        assertThat(capturedMember.getDeleted()).isEqualTo(updatedMember.getDeleted());

        assertThat(returnedMember).isEqualTo(expectedReturnedMember);
    }

    @Test
    void fullUpdateNonExistingMember() {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long membershipId = 1L;
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        membership.setId(membershipId);

        Long memberId = 1L;
        Member updatedMember = new Member(
                "Cayden Dickens",
                "836 Vincenza Loaf",
                "c.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 26),
                membership
        );

        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.fullUpdate(memberId, updatedMember))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("member with ID " + memberId + " does not exist");
        verify(memberRepository).findById(
                argThat(id -> id.equals(memberId))
        );
        verify(memberRepository, never()).save(any());
    }

    @Test
    void fullUpdateExistingMemberNewEmailExistsForNonDeletedMember() {
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long membership1Id = 1L;
        Membership membership1 = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        membership1.setId(membership1Id);

        Long membership2Id = 1L;
        Membership membership2 = new Membership(
                membershipType,
                LocalDate.of(2020, Month.AUGUST, 3),
                LocalDate.of(2021, Month.AUGUST, 3));
        membership2.setId(membership2Id);

        Long member1Id = 1L;
        Member existingMember1 = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                membership1
        );
        existingMember1.setId(member1Id);

        Long member2Id = 1L;
        String newEmail = "c.dickens@gmail.com";
        Member existingMember2 = new Member(
                "Cathryn Dickens",
                "950 Poplar St.",
                newEmail,
                LocalDate.of(1998, Month.JUNE, 8),
                membership2
        );
        existingMember2.setId(member2Id);

        Member updatedMember = new Member(
                "Cayden Dickens",
                "836 Vincenza Loaf",
                newEmail,
                LocalDate.of(1953, Month.APRIL, 21),
                membership1
        );

        given(memberRepository.findById(member1Id)).willReturn(Optional.of(existingMember1));
        given(memberRepository.findByEmail(newEmail)).willReturn(Optional.of(existingMember2));

        // when
        // then
        assertThatThrownBy(() -> underTest.fullUpdate(member1Id, updatedMember))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("email " + newEmail + " already exists");
        verify(memberRepository).findById(
                argThat(id -> id.equals(member1Id))
        );
        verify(memberRepository, never()).save(any());
    }

    @Test
    void fullUpdateMemberForNonExistingMembership() {
        // given
        Member updatedMember = new Member(
                "Cayden Dickens",
                "836 Vincenza Loaf",
                "c.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 21),
                null
        );
        Long memberId = 1L;

        Long membershipId = 1L;

        given(membershipRepository.findById(membershipId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.fullUpdate(memberId, updatedMember, membershipId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot update member for membership: membership with ID " + membershipId + " does not exist");
        verify(membershipRepository).findById(
                argThat(id -> id.equals(membershipId))
        );
        verify(underTest, never()).fullUpdate(any(), any());
    }


    @Test
    void fullUpdateValidMember() {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long membershipId = 1L;
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        membership.setId(membershipId);

        Member updatedMember = new Member(
                "Cayden Dickens",
                "836 Vincenza Loaf",
                "c.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 21),
                null
        );
        Long memberId = 1L;

        Member updatedMemberWithMembership = new Member(
                "Cayden Dickens",
                "836 Vincenza Loaf",
                "c.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 21),
                membership
        );

        Member expectedReturnedMember = new Member(
                "Cayden Dickens",
                "836 Vincenza Loaf",
                "c.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 21),
                membership
        );
        expectedReturnedMember.setId(memberId);

        willReturn(Optional.of(membership)).given(membershipRepository).findById(membershipTypeId);
        // once again expected with any() argument because of Member#equals() returning false if ID is null
        willReturn(expectedReturnedMember).given(underTest).fullUpdate(eq(memberId), any(Member.class));

        // when
        Member returnedMember = underTest.fullUpdate(memberId, updatedMember, membershipId);

        // then
        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(underTest).fullUpdate(
                argThat(id -> id.equals(memberId)),
                memberArgumentCaptor.capture()
        );
        Member capturedMember = memberArgumentCaptor.getValue();

        // compare fields instead of object directly, because Member#equals() returns "false"
        // if ID is null
        assertThat(capturedMember.getId()).isEqualTo(updatedMemberWithMembership.getId());
        assertThat(capturedMember.getName()).isEqualTo(updatedMemberWithMembership.getName());
        assertThat(capturedMember.getEmailAddress()).isEqualTo(updatedMemberWithMembership.getEmailAddress());
        assertThat(capturedMember.getHomeAddress()).isEqualTo(updatedMemberWithMembership.getHomeAddress());
        assertThat(capturedMember.getBirthday()).isEqualTo(updatedMemberWithMembership.getBirthday());
        assertThat(capturedMember.getMembership()).isEqualTo(updatedMemberWithMembership.getMembership());
        assertThat(capturedMember.getDeleted()).isEqualTo(updatedMemberWithMembership.getDeleted());

        assertThat(returnedMember).isEqualTo(expectedReturnedMember);
    }

    @Test
    void fullUpdateValidMemberWithNullMembership() {
        Member updatedMember = new Member(
                "Cayden Dickens",
                "836 Vincenza Loaf",
                "c.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 21),
                null
        );
        Long memberId = 1L;

        Member updatedMemberCopy = new Member(
                "Cayden Dickens",
                "836 Vincenza Loaf",
                "c.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 21),
                null
        );

        Member expectedReturnedMember = new Member(
                "Cayden Dickens",
                "836 Vincenza Loaf",
                "c.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 21),
                null
        );
        expectedReturnedMember.setId(memberId);

        // once again expected with any() argument because of Member#equals() returning false if ID is null
        willReturn(expectedReturnedMember).given(underTest).fullUpdate(eq(memberId), any(Member.class));

        // when
        Member returnedMember = underTest.fullUpdate(memberId, updatedMember, null);

        // then
        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(underTest).fullUpdate(
                argThat(id -> id.equals(memberId)),
                memberArgumentCaptor.capture()
        );
        Member capturedMember = memberArgumentCaptor.getValue();

        // compare fields instead of object directly, because Member#equals() returns "false"
        // if ID is null
        assertThat(capturedMember.getId()).isEqualTo(updatedMemberCopy.getId());
        assertThat(capturedMember.getName()).isEqualTo(updatedMemberCopy.getName());
        assertThat(capturedMember.getEmailAddress()).isEqualTo(updatedMemberCopy.getEmailAddress());
        assertThat(capturedMember.getHomeAddress()).isEqualTo(updatedMemberCopy.getHomeAddress());
        assertThat(capturedMember.getBirthday()).isEqualTo(updatedMemberCopy.getBirthday());
        assertThat(capturedMember.getMembership()).isEqualTo(updatedMemberCopy.getMembership());
        assertThat(capturedMember.getDeleted()).isEqualTo(updatedMemberCopy.getDeleted());

        assertThat(returnedMember).isEqualTo(expectedReturnedMember);
    }

    @Test
    void fullUpdateValidMemberNewId() {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long membershipId = 1L;
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        membership.setId(membershipId);

        Long newMemberId = 2L;
        Member updatedMember = new Member(
                "Cayden Dickens",
                "836 Vincenza Loaf",
                "c.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 21),
                null
        );
        updatedMember.setId(newMemberId);
        Member updatedMemberWithMembership = new Member(
                "Cayden Dickens",
                "836 Vincenza Loaf",
                "c.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 21),
                membership
        );
        updatedMemberWithMembership.setId(newMemberId);
        Long memberId = 1L;
        Member expectedReturnedMember = new Member(
                "Cayden Dickens",
                "836 Vincenza Loaf",
                "c.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 21),
                membership
        );
        expectedReturnedMember.setId(memberId);

        willReturn(Optional.of(membership)).given(membershipRepository).findById(membershipTypeId);
        // once again expected with any() argument because of Member#equals() returning false if ID is null
        willReturn(expectedReturnedMember).given(underTest).fullUpdate(eq(memberId), any(Member.class));

        // when
        Member returnedMember = underTest.fullUpdate(memberId, updatedMember, membershipId);

        // then
        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(underTest).fullUpdate(
                argThat(id -> id.equals(memberId)),
                memberArgumentCaptor.capture()
        );
        verify(logger).warn("Cannot update internal member ID from {} to {}; saving under ID {}", memberId, newMemberId, memberId);
        Member capturedMember = memberArgumentCaptor.getValue();

        // compare fields instead of object directly, because Member#equals() returns "false"
        // if ID is null
        assertThat(capturedMember.getId()).isEqualTo(updatedMemberWithMembership.getId());
        assertThat(capturedMember.getName()).isEqualTo(updatedMemberWithMembership.getName());
        assertThat(capturedMember.getEmailAddress()).isEqualTo(updatedMemberWithMembership.getEmailAddress());
        assertThat(capturedMember.getHomeAddress()).isEqualTo(updatedMemberWithMembership.getHomeAddress());
        assertThat(capturedMember.getBirthday()).isEqualTo(updatedMemberWithMembership.getBirthday());
        assertThat(capturedMember.getMembership()).isEqualTo(updatedMemberWithMembership.getMembership());
        assertThat(capturedMember.getDeleted()).isEqualTo(updatedMemberWithMembership.getDeleted());

        assertThat(returnedMember).isEqualTo(expectedReturnedMember);
    }

    @Test
    void partialUpdateValidMember() throws IOException, JsonPatchException {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long membershipId = 1L;
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        membership.setId(membershipId);

        Long memberId = 1L;
        Member existingMember = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 26),
                membership
        );
        existingMember.setId(memberId);

        @SuppressWarnings("JsonStandardCompliance") String updatedName = "Cayden Dickens";

        Member updatedMember = new Member(
                updatedName,
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 26),
                membership
        );
        updatedMember.setId(memberId);

        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        String jsonString = "[{" +
                "\"op\": \"replace\"," +
                "\"path\": \"/name\"," +
                "\"value\":\"" + updatedName + "\"" +
                "}]";
        JsonNode memberJson = mapper.readTree(jsonString);
        JsonPatch jsonPatch = JsonPatch.fromJson(memberJson);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(existingMember));
        given(membershipRepository.findById(membershipId)).willReturn(Optional.of(membership));
        given(memberRepository.save(updatedMember)).willReturn(updatedMember);

        // when
        Member returnedMember = underTest.partialUpdate(memberId, jsonPatch);

        // then
        verify(memberRepository).findById(
                argThat(id -> id.equals(memberId))
        );

        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberArgumentCaptor.capture());
        Member capturedMember = memberArgumentCaptor.getValue();

        assertThat(capturedMember).isEqualTo(updatedMember);
        assertThat(returnedMember).isEqualTo(updatedMember);
        assertThat(returnedMember.getName()).isEqualTo(updatedName);
    }


    @Test
    void partialUpdateNonExistingMember() throws IOException {
        // given
        Long memberId = 1L;

        @SuppressWarnings("JsonStandardCompliance") String updatedName = "Cayden Dickens";

        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        String jsonString = "[{" +
                "\"op\": \"replace\"," +
                "\"path\": \"/name\"," +
                "\"value\":\"" + updatedName + "\"" +
                "}]";
        JsonNode memberJson = mapper.readTree(jsonString);
        JsonPatch jsonPatch = JsonPatch.fromJson(memberJson);

        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.partialUpdate(memberId, jsonPatch))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("member with ID " + memberId + " does not exist");
        verify(memberRepository).findById(
                argThat(id -> id.equals(memberId))
        );
        verify(memberRepository, never()).save(any());
    }

    @Test
    void partialUpdateExistingDeletedMember() throws IOException {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long membershipId = 1L;
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        membership.setId(membershipId);

        Long memberId = 1L;
        Member existingMember = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 26),
                membership
        );
        existingMember.setId(memberId);
        existingMember.setDeleted(true);

        @SuppressWarnings("JsonStandardCompliance") String updatedName = "Cayden Dickens";

        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        String jsonString = "[{" +
                "\"op\": \"replace\"," +
                "\"path\": \"/name\"," +
                "\"value\":\"" + updatedName + "\"" +
                "}]";
        JsonNode memberJson = mapper.readTree(jsonString);
        JsonPatch jsonPatch = JsonPatch.fromJson(memberJson);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(existingMember));

        // when
        // then
        assertThatThrownBy(() -> underTest.partialUpdate(memberId, jsonPatch))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("member with ID " + memberId + " has been deleted");
        verify(memberRepository).findById(
                argThat(id -> id.equals(memberId))
        );
        verify(memberRepository, never()).save(any());
    }

    @Test
    void partialUpdateMemberMembershipForNonExistingMembership() throws IOException {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long membershipId = 1L;
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        membership.setId(membershipId);

        Long memberId = 1L;
        Member existingMember = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 26),
                membership
        );
        existingMember.setId(memberId);

        Long updatedMemberShipId = 2L;

        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        String jsonString = "[{" +
                "\"op\": \"replace\"," +
                "\"path\": \"/membershipId\"," +
                "\"value\":" + updatedMemberShipId +
                "}]";
        JsonNode memberJson = mapper.readTree(jsonString);
        JsonPatch jsonPatch = JsonPatch.fromJson(memberJson);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(existingMember));
        given(membershipRepository.findById(updatedMemberShipId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.partialUpdate(memberId, jsonPatch))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("membership with ID " + updatedMemberShipId + " does not exist");
        verify(memberRepository).findById(
                argThat(id -> id.equals(memberId))
        );
        verify(memberRepository, never()).save(any());
    }

    @Test
    void partialUpdateMemberMembershipForExistingDeletedMembership() throws IOException {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long existingMembershipId = 1L;
        Membership existingMembership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        existingMembership.setId(existingMembershipId);

        Long existingMembership2Id = 2L;
        Membership existingMembership2 = new Membership(
                membershipType,
                LocalDate.of(2020, Month.AUGUST, 3),
                LocalDate.of(2021, Month.AUGUST, 3));
        existingMembership2.setId(existingMembership2Id);
        existingMembership2.setDeleted(true);

        Long memberId = 1L;
        Member existingMember = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 26),
                existingMembership
        );
        existingMember.setId(memberId);

        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        String jsonString = "[{" +
                "\"op\": \"replace\"," +
                "\"path\": \"/membershipId\"," +
                "\"value\":" + existingMembership2Id +
                "}]";
        JsonNode memberJson = mapper.readTree(jsonString);
        JsonPatch jsonPatch = JsonPatch.fromJson(memberJson);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(existingMember));
        given(membershipRepository.findById(existingMembership2Id)).willReturn(Optional.of(existingMembership2));

        // when
        // then
        assertThatThrownBy(() -> underTest.partialUpdate(memberId, jsonPatch))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("membership with ID " + existingMembership2Id + " has been deleted");
        verify(memberRepository).findById(
                argThat(id -> id.equals(memberId))
        );
        verify(memberRepository, never()).save(any());
    }

    @Test
    void deleteExistingNonDeletedMember() {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long membershipId = 1L;
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        membership.setId(membershipId);

        Long memberId = 1L;
        Member existingMember = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );
        existingMember.setId(memberId);

        membership.getMembers().add(existingMember);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(existingMember));

        // when
        Boolean result = underTest.delete(memberId);

        // then
        verify(memberRepository).findById(
                argThat(id -> id.equals(memberId))
        );
        assertThat(result).isTrue();
        assertThat(existingMember.getDeleted()).isTrue();
        assertThat(existingMember.getMembership().getMembers().size()).isZero();
    }

    @Test
    void deleteExistingDeletedMember() {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long membershipId = 1L;
        Membership membership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        membership.setId(membershipId);

        Long memberId = 1L;
        Member existingMember = new Member(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25),
                membership
        );
        existingMember.setDeleted(true);
        existingMember.setId(memberId);

        membership.getMembers().add(existingMember);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(existingMember));

        // when
        // then
        assertThatThrownBy(() -> underTest.delete(memberId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("member with ID " + memberId + " has already been deleted");
        verify(memberRepository).findById(
                argThat(id -> id.equals(memberId))
        );
    }

    @Test
    void deleteNonExistingMember() {
        // given
        Long memberId = 1L;
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.delete(memberId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("member with ID " + memberId + " does not exist");
        verify(memberRepository).findById(
                argThat(id -> id.equals(memberId))
        );
    }
}