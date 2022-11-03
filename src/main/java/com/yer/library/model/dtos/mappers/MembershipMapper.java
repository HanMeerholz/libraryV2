package com.yer.library.model.dtos.mappers;

import com.yer.library.model.Membership;
import com.yer.library.model.MembershipType;
import com.yer.library.model.dtos.MembershipDTO;
import com.yer.library.repository.MembershipTypeRepository;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MembershipMapper {
    MembershipMapper INSTANCE = Mappers.getMapper(MembershipMapper.class);

    @Mapping(target = "membershipTypeId", expression = "java(membership.getMembershipType().getId())")
    MembershipDTO toMembershipDTO(Membership membership);

    @Mapping(target = "membershipType", ignore = true)
    Membership toMembership(MembershipDTO membershipDTO, @Context MembershipTypeRepository membershipTypeRepository);

    @AfterMapping
    default void toMembership(@MappingTarget Membership membership, MembershipDTO membershipDTO, @Context MembershipTypeRepository membershipTypeRepository) {
        Long membershipTypeId = membershipDTO.getMembershipTypeId();
        MembershipType membershipType = membershipTypeRepository.findById(membershipTypeId).orElseThrow(() ->
                new IllegalStateException("membership type with ID " + membershipTypeId + " does not exist")
        );
        membership.setMembershipType(membershipType);
    }
}
