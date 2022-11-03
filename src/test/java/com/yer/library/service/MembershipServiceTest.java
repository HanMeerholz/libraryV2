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
import com.yer.library.repository.MembershipRepository;
import com.yer.library.repository.MembershipTypeRepository;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTest {
    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private MembershipTypeRepository membershipTypeRepository;

    @Mock
    private Logger logger;

    @InjectMocks
    @Spy
    private MembershipService underTest;

    @Test
    void getExistingNonDeletedMembership() {
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

        given(membershipRepository.findById(membershipId)).willReturn(Optional.of(membership));

        // when
        Membership returnedMembership = underTest.get(membershipId);

        // then
        verify(membershipRepository).findById(
                argThat(id -> id.equals(membershipId))
        );
        assertThat(returnedMembership).isEqualTo(membership);
    }

    @Test
    void getExistingDeletedMembership() {
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
        membership.setDeleted(true);

        given(membershipRepository.findById(membershipId)).willReturn(Optional.of(membership));

        // when
        assertThatThrownBy(() -> underTest.get(membershipId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("membership with ID " + membershipId + " has been cancelled, deleted, or expired");
        verify(membershipRepository).findById(
                argThat(id -> id.equals(membershipId))
        );
        verify(membershipRepository, never()).save(any());
    }

    @Test
    void getNonExistingMembership() {
        // given
        Long membershipId = 1L;

        given(membershipRepository.findById(membershipId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.get(membershipId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("membership with ID " + membershipId + " does not exist");
        verify(membershipRepository).findById(
                argThat(id -> id.equals(membershipId))
        );
        verify(membershipRepository, never()).save(any());
    }

    @Test
    void list() {
        // given
        int limit = 100;

        // when
        underTest.list(limit);

        // then
        verify(membershipRepository).listAvailable(
                argThat(pageable -> pageable.equals(Pageable.ofSize(limit)))
        );
    }

    @Test
    void listByType() {
        // given
        Long membershipTypeId = 1L;
        int limit = 100;

        // when
        underTest.listByMembershipType(membershipTypeId, limit);

        // then
        verify(membershipRepository).listByMembershipType(eq(membershipTypeId), argThat(
                pageable -> pageable.equals(Pageable.ofSize(limit))
        ));
    }

    @Test
    void addValidMembershipWithType() {
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

        Membership expectedReturnedMembership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));

        expectedReturnedMembership.setId(membershipId);

        given(membershipRepository.save(membership)).willReturn(expectedReturnedMembership);

        // when
        Membership returnedMembership = underTest.add(membership);

        // then
        ArgumentCaptor<Membership> membershipArgumentCaptor = ArgumentCaptor.forClass(Membership.class);
        verify(membershipRepository).save(membershipArgumentCaptor.capture());
        Membership capturedMembership = membershipArgumentCaptor.getValue();

        assertThat(capturedMembership).isEqualTo(membership);
        assertThat(returnedMembership).isEqualTo(expectedReturnedMembership);
    }

    @Test
    void addMembershipWithoutMembershipTypeId() {
        // given
        Membership membership = new Membership(
                null,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));

        // when
        // then
        assertThatThrownBy(() -> underTest.add(membership, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot add membership without specifying a membership type ID");
        verify(membershipRepository, never()).save(any());
    }

    @Test
    void addMembershipForNonExistingMembershipType() {
        // given
        Long membershipTypeId = 1L;
        Membership membership = new Membership(
                null,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        given(membershipTypeRepository.findById(membershipTypeId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.add(membership, membershipTypeId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot add membership for membership type: membership type with ID " + membershipTypeId + " does not exist");
        verify(membershipRepository, never()).save(any());
    }

    @Test
    void addValidMembership() {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Membership membership = new Membership(
                null,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        Membership membershipWithType = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        Long membershipId = 1L;
        Membership expectedReturnedMembership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3));
        expectedReturnedMembership.setId(membershipId);

        willReturn(Optional.of(membershipType)).given(membershipTypeRepository).findById(membershipTypeId);
        // We pass in an "any" argument since the specific membership has a null id and
        // will return false if .equals() is called on it
        willReturn(expectedReturnedMembership).given(underTest).add(any(Membership.class));

        // when
        Membership returnedMembership = underTest.add(membership, membershipTypeId);

        // then
        ArgumentCaptor<Membership> membershipArgumentCaptor = ArgumentCaptor.forClass(Membership.class);
        verify(underTest).add(membershipArgumentCaptor.capture());
        Membership capturedMembership = membershipArgumentCaptor.getValue();

        // Since membershipId is null, equals() won't work, so we compare fields instead
        assertThat(capturedMembership.getId()).isEqualTo(membershipWithType.getId());
        assertThat(capturedMembership.getMembershipType()).isEqualTo(membershipWithType.getMembershipType());
        assertThat(capturedMembership.getStartDate()).isEqualTo(membershipWithType.getStartDate());
        assertThat(capturedMembership.getEndDate()).isEqualTo(membershipWithType.getEndDate());
        assertThat(capturedMembership.getMembers()).isEqualTo(membershipWithType.getMembers());
        assertThat(capturedMembership.getDeleted()).isEqualTo(membershipWithType.getDeleted());

        assertThat(returnedMembership).isEqualTo(expectedReturnedMembership);
    }

    @Test
    void fullUpdateMembershipWithType() {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long membershipId = 1L;
        Membership updatedMembership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        Membership updatedMembershipCopyWithId = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        Membership expectedReturnedMembership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        expectedReturnedMembership.setId(membershipId);

        given(membershipRepository.existsById(membershipId)).willReturn(true);
        given(membershipRepository.save(updatedMembership)).willReturn(expectedReturnedMembership);

        // when
        Membership returnedMembership = underTest.fullUpdate(membershipId, updatedMembership);
        updatedMembershipCopyWithId.setId(returnedMembership.getId());

        // then
        verify(membershipRepository).existsById(
                argThat(id -> id.equals(membershipId))
        );
        ArgumentCaptor<Membership> membershipArgumentCaptor = ArgumentCaptor.forClass(Membership.class);
        verify(membershipRepository).save(membershipArgumentCaptor.capture());
        Membership capturedMembership = membershipArgumentCaptor.getValue();

        assertThat(capturedMembership).isSameAs(updatedMembership);

        assertThat(capturedMembership.getId()).isEqualTo(updatedMembershipCopyWithId.getId());
        assertThat(capturedMembership.getMembershipType()).isEqualTo(updatedMembershipCopyWithId.getMembershipType());
        assertThat(capturedMembership.getStartDate()).isEqualTo(updatedMembershipCopyWithId.getStartDate());
        assertThat(capturedMembership.getEndDate()).isEqualTo(updatedMembershipCopyWithId.getEndDate());
        assertThat(capturedMembership.getMembers()).isEqualTo(updatedMembershipCopyWithId.getMembers());
        assertThat(capturedMembership.getDeleted()).isEqualTo(updatedMembershipCopyWithId.getDeleted());

        assertThat(returnedMembership).isEqualTo(expectedReturnedMembership);
    }

    @Test
    void fullUpdateNonExistingMembershipWithType() {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long membershipId = 1L;
        Membership updatedMembership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        Membership expectedReturnedMembership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        expectedReturnedMembership.setId(membershipId);

        given(membershipRepository.existsById(membershipId)).willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> underTest.fullUpdate(membershipId, updatedMembership))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("membership with ID " + membershipId + " does not exist");
        verify(membershipRepository).existsById(
                argThat(id -> id.equals(membershipId))
        );
        verify(membershipRepository, never()).save(any());
    }

    @Test
    void fullUpdateMembershipForNonExistingType() {
        // given
        Membership updatedMembership = new Membership(
                null,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        Long membershipId = 1L;

        Long membershipTypeId = 1L;

        given(membershipTypeRepository.findById(membershipTypeId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.fullUpdate(membershipId, updatedMembership, membershipTypeId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot update membership for membership type: membership type with ID " + membershipTypeId + " does not exist");
        verify(membershipTypeRepository).findById(
                argThat(id -> id.equals(membershipTypeId))
        );
        verify(underTest, never()).fullUpdate(any(), any());
    }


    @Test
    void fullUpdateValidMembership() {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);


        Membership updatedMembership = new Membership(
                null,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        Long membershipId = 1L;
        Membership updatedMembershipWithType = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        Membership expectedReturnedMembership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        expectedReturnedMembership.setId(membershipId);

        willReturn(Optional.of(membershipType)).given(membershipTypeRepository).findById(membershipTypeId);
        // once again expected with any() argument because of Membership#equals() returning false if ID is null
        willReturn(expectedReturnedMembership).given(underTest).fullUpdate(eq(membershipId), any(Membership.class));

        // when
        Membership returnedMembership = underTest.fullUpdate(membershipId, updatedMembership, membershipTypeId);

        // then
        ArgumentCaptor<Membership> membershipArgumentCaptor = ArgumentCaptor.forClass(Membership.class);
        verify(underTest).fullUpdate(
                argThat(id -> id.equals(membershipId)),
                membershipArgumentCaptor.capture()
        );
        Membership capturedMembership = membershipArgumentCaptor.getValue();
        // compare fields instead of object directly, because Membership#equals() returns "false"
        // if ID is null
        assertThat(capturedMembership.getId()).isEqualTo(updatedMembershipWithType.getId());
        assertThat(capturedMembership.getMembershipType()).isEqualTo(updatedMembershipWithType.getMembershipType());
        assertThat(capturedMembership.getStartDate()).isEqualTo(updatedMembershipWithType.getStartDate());
        assertThat(capturedMembership.getEndDate()).isEqualTo(updatedMembershipWithType.getEndDate());
        assertThat(capturedMembership.getMembers()).isEqualTo(updatedMembershipWithType.getMembers());
        assertThat(capturedMembership.getDeleted()).isEqualTo(updatedMembershipWithType.getDeleted());

        assertThat(returnedMembership).isEqualTo(expectedReturnedMembership);
    }

    @Test
    void fullUpdateValidMembershipNewId() {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long newMembershipId = 2L;
        Membership updatedMembership = new Membership(
                null,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        updatedMembership.setId(newMembershipId);
        Membership updatedMembershipWithType = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        updatedMembershipWithType.setId(newMembershipId);
        Long membershipId = 1L;
        Membership expectedReturnedMembership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        expectedReturnedMembership.setId(membershipId);

        willReturn(Optional.of(membershipType)).given(membershipTypeRepository).findById(membershipTypeId);
        // once again expected with any() argument because of BookCopy#equals() returning false if id is null
        willReturn(expectedReturnedMembership).given(underTest).fullUpdate(eq(membershipId), any(Membership.class));

        // when
        Membership returnedMembership = underTest.fullUpdate(membershipId, updatedMembership, membershipTypeId);

        // then
        ArgumentCaptor<Membership> membershipArgumentCaptor = ArgumentCaptor.forClass(Membership.class);
        verify(underTest).fullUpdate(
                argThat(id -> id.equals(membershipId)),
                membershipArgumentCaptor.capture()
        );
        verify(logger).warn("Cannot update internal membership ID from {} to {}; saving under ID {}", membershipId, newMembershipId, membershipId);
        Membership capturedMembership = membershipArgumentCaptor.getValue();
        // compare fields instead of object directly, because Membership#equals() returns "false"
        // if ID is null
        assertThat(capturedMembership.getId()).isEqualTo(updatedMembershipWithType.getId());
        assertThat(capturedMembership.getMembershipType()).isEqualTo(updatedMembershipWithType.getMembershipType());
        assertThat(capturedMembership.getStartDate()).isEqualTo(updatedMembershipWithType.getStartDate());
        assertThat(capturedMembership.getEndDate()).isEqualTo(updatedMembershipWithType.getEndDate());
        assertThat(capturedMembership.getMembers()).isEqualTo(updatedMembershipWithType.getMembers());
        assertThat(capturedMembership.getDeleted()).isEqualTo(updatedMembershipWithType.getDeleted());

        assertThat(returnedMembership).isEqualTo(expectedReturnedMembership);
    }

    @Test
    void partialUpdateValidMembership() throws IOException, JsonPatchException {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long membershipId = 1L;
        Membership existingMembership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        existingMembership.setId(membershipId);

        LocalDate updatedEndDate = LocalDate.of(2022, Month.MARCH, 3);
        Membership updatedMembership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                updatedEndDate
        );
        updatedMembership.setId(membershipId);


        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        String jsonString = "[{" +
                "\"op\": \"replace\"," +
                "\"path\": \"/endDate\"," +
                "\"value\":\"" + updatedEndDate + "\"" +
                "}]";
        JsonNode membershipJson = mapper.readTree(jsonString);
        JsonPatch jsonPatch = JsonPatch.fromJson(membershipJson);

        given(membershipRepository.findById(membershipId)).willReturn(Optional.of(existingMembership));
        given(membershipTypeRepository.findById(membershipTypeId)).willReturn(Optional.of(membershipType));
        given(membershipRepository.save(updatedMembership)).willReturn(updatedMembership);

        // when
        Membership returnedMembership = underTest.partialUpdate(membershipId, jsonPatch);

        // then
        verify(membershipRepository).findById(
                argThat(id -> id.equals(membershipId))
        );

        ArgumentCaptor<Membership> bookCopyArgumentCaptor = ArgumentCaptor.forClass(Membership.class);
        verify(membershipRepository).save(bookCopyArgumentCaptor.capture());
        Membership capturedMembership = bookCopyArgumentCaptor.getValue();

        assertThat(capturedMembership).isEqualTo(updatedMembership);
        assertThat(returnedMembership).isEqualTo(updatedMembership);
        assertThat(returnedMembership.getEndDate()).isEqualTo(updatedEndDate);
    }


    @Test
    void partialUpdateNonExistingMembership() throws IOException {
        // given
        Long membershipId = 1L;

        LocalDate updatedEndDate = LocalDate.of(2022, Month.MARCH, 3);

        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        String jsonString = "[{" +
                "\"op\": \"replace\"," +
                "\"path\": \"/endDate\"," +
                "\"value\":\"" + updatedEndDate + "\"" +
                "}]";
        JsonNode membershipJson = mapper.readTree(jsonString);
        JsonPatch jsonPatch = JsonPatch.fromJson(membershipJson);

        given(membershipRepository.findById(membershipId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.partialUpdate(membershipId, jsonPatch))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("membership with ID " + membershipId + " does not exist");
        verify(membershipRepository).findById(
                argThat(id -> id.equals(membershipId))
        );
        verify(membershipRepository, never()).save(any());
    }

    @Test
    void partialUpdateExistingDeletedMembership() throws IOException {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long membershipId = 1L;
        Membership existingMembership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        existingMembership.setId(membershipId);
        existingMembership.setDeleted(true);

        LocalDate updatedEndDate = LocalDate.of(2022, Month.MARCH, 3);


        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        String jsonString = "[{" +
                "\"op\": \"replace\"," +
                "\"path\": \"/endDate\"," +
                "\"value\":\"" + updatedEndDate + "\"" +
                "}]";
        JsonNode membershipJson = mapper.readTree(jsonString);
        JsonPatch jsonPatch = JsonPatch.fromJson(membershipJson);

        given(membershipRepository.findById(membershipId)).willReturn(Optional.of(existingMembership));

        // when
        // then
        assertThatThrownBy(() -> underTest.partialUpdate(membershipId, jsonPatch))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("membership with ID " + membershipId + " has been deleted");
        verify(membershipRepository).findById(
                argThat(id -> id.equals(membershipId))
        );
        verify(membershipRepository, never()).save(any());
    }

    @Test
    void partialUpdateMembershipMembershipTypeForNonExistingType() throws IOException {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long membershipId = 1L;
        Membership existingMembership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        existingMembership.setId(membershipId);

        long updatedMembershipTypeId = 2L;

        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        String jsonString = "[{" +
                "\"op\": \"replace\"," +
                "\"path\": \"/membershipTypeId\"," +
                "\"value\":" + updatedMembershipTypeId +
                "}]";
        JsonNode membershipJson = mapper.readTree(jsonString);
        JsonPatch jsonPatch = JsonPatch.fromJson(membershipJson);

        given(membershipRepository.findById(membershipId)).willReturn(Optional.of(existingMembership));
        given(membershipTypeRepository.findById(updatedMembershipTypeId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.partialUpdate(membershipId, jsonPatch))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("membership type with ID " + updatedMembershipTypeId + " does not exist");
        verify(membershipRepository).findById(
                argThat(id -> id.equals(membershipId))
        );
        verify(membershipRepository, never()).save(any());
    }


    @Test
    void deleteExistingNonDeletedMembership() {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long membershipId = 1L;
        Membership existingMembership = new Membership(
                null,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );

        given(membershipRepository.findById(membershipId)).willReturn(Optional.of(existingMembership));

        // when
        Boolean result = underTest.delete(membershipId);

        // then
        verify(membershipRepository).findById(
                argThat(id -> id.equals(membershipId))
        );
        assertThat(result).isTrue();
        assertThat(existingMembership.getDeleted()).isTrue();
    }

    @Test
    void deleteExistingNonDeletedMembershipWithMembers() {
        // given
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.FAMILY, 0
        );

        Long membershipId = 1L;
        Membership existingMembership = new Membership(
                membershipType,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );

        Member member1 = new Member(
                "Iain Carter",
                "950 Poplar St.",
                "iaincarter@hotmail.com",
                LocalDate.of(1998, Month.JUNE, 8),
                existingMembership
        );
        Member member2 = new Member(
                "Harry Carter",
                "950 Poplar St.",
                "harrycarter@hotmail.com",
                LocalDate.of(1996, Month.JULY, 4),
                existingMembership
        );

        existingMembership.setMembers(Collections.unmodifiableList(Arrays.asList(member1, member2)));

        given(membershipRepository.findById(membershipId)).willReturn(Optional.of(existingMembership));

        // when
        Boolean result = underTest.delete(membershipId);

        // then
        verify(membershipRepository).findById(
                argThat(id -> id.equals(membershipId))
        );
        assertThat(result).isTrue();
        assertThat(existingMembership.getDeleted()).isTrue();
        assertThat(existingMembership.getMembers()).allMatch(member -> member.getMembership() == null);
    }

    @Test
    void deleteExistingDeletedMembership() {
        // given
        Long membershipTypeId = 1L;
        MembershipType membershipType = new MembershipType(
                MembershipTypeName.CHILD, 0
        );
        membershipType.setId(membershipTypeId);

        Long membershipId = 1L;
        Membership existingMembership = new Membership(
                null,
                LocalDate.of(2019, Month.MARCH, 3),
                LocalDate.of(2021, Month.MARCH, 3)
        );
        existingMembership.setDeleted(true);

        given(membershipRepository.findById(membershipId)).willReturn(Optional.of(existingMembership));

        // when
        // then
        assertThatThrownBy(() -> underTest.delete(membershipId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("membership with ID " + membershipId + " has already been deleted");
        verify(membershipRepository).findById(
                argThat(id -> id.equals(membershipTypeId))
        );
    }

    @Test
    void deleteNonExistingMembership() {
        // given
        Long membershipId = 1L;
        given(membershipRepository.findById(membershipId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.delete(membershipId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("membership with ID " + membershipId + " does not exist");
        verify(membershipRepository).findById(
                argThat(id -> id.equals(membershipId))
        );
    }
}