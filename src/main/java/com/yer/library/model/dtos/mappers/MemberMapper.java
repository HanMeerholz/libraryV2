package com.yer.library.model.dtos.mappers;

import com.yer.library.model.Member;
import com.yer.library.model.Membership;
import com.yer.library.model.dtos.MemberDTO;
import com.yer.library.repository.MembershipRepository;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    @Mapping(target = "membershipId", expression = "java(member.getMembership().getId())")
    MemberDTO toMemberDTO(Member member);

    @Mapping(target = "membership", ignore = true)
    Member toMember(MemberDTO memberDTO, @Context MembershipRepository membershipRepository);

    @AfterMapping
    default void toMember(@MappingTarget Member member, MemberDTO memberDTO, @Context MembershipRepository membershipRepository) {
        Long membershipId = memberDTO.getMembershipId();
        Membership membership = membershipRepository.findById(membershipId).orElseThrow(() ->
                new IllegalStateException("membership with ID " + membershipId + " does not exist")
        );
        if (membership.getDeleted()) {
            throw new IllegalStateException("membership with ID " + membershipId + " has been deleted");
        }
        member.setMembership(membership);
    }
}
