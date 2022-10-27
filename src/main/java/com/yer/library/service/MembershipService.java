package com.yer.library.service;

import com.yer.library.model.Membership;
import com.yer.library.model.MembershipType;
import com.yer.library.repository.MembershipRepository;
import com.yer.library.repository.MembershipTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;

import static java.lang.Boolean.TRUE;
import static org.springframework.data.domain.PageRequest.ofSize;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MembershipService implements CrudService<Membership> {
    private final MembershipTypeRepository membershipTypeRepository;
    private final MembershipRepository membershipRepository;

    @Override
    public Membership get(Long membershipId) {
        log.info("Fetching membership with ID: {}", membershipId);
        Membership membership = membershipRepository.findById(membershipId).orElseThrow(
                () -> new IllegalStateException(
                        "membership with ID " + membershipId + " does not exist"
                )
        );
        if (membership.getDeleted()) {
            throw new IllegalStateException("membership with ID " + membershipId + " has been cancelled, deleted, or expired");
        }
        return membership;
    }

    @Override
    public Collection<Membership> list(int limit) {
        log.info("Listing all memberships (up to a limit of {})", limit);
        return membershipRepository.listAvailable(ofSize(limit));
    }

    public Collection<Membership> listByMembershipType(Long membershipTypeId, int limit) {
        log.info("Listing all memberships for membership type with ID {} (up to a limit of {})", membershipTypeId, limit);
        return membershipRepository.listByMembershipType(membershipTypeId, ofSize(limit));
    }

    @Override
    public Membership add(Membership membership) {
        return membershipRepository.save(membership);
    }

    // TODO might want to change the signature to Membership add(Membership Membership, String customerEmail, MembershipType membershipType)
    public Membership add(Membership membership, Long membershipTypeId) {
        log.info("Adding new membership");

        if (membershipTypeId == null) {
            throw new IllegalArgumentException("cannot add membership without specifying a membership type ID");
        }

        MembershipType membershipType = membershipTypeRepository.findById(membershipTypeId).orElseThrow(
                () -> new IllegalStateException("cannot add membership for membership type: membership type with ID " + membershipTypeId + " does not exist")
        );

        membership.setMembershipType(membershipType);

        return add(membership);
    }

    @Override
    public Membership fullUpdate(Long membershipId, Membership membership) {
        if (!membershipRepository.existsById(membershipId)) {
            throw new IllegalStateException(
                    "membership with ID " + membershipId + " does not exist"
            );
        }
        membership.setId(membershipId);

        return membershipRepository.save(membership);
    }

    public Membership fullUpdate(Long membershipId, Membership membership, Long membershipTypeId) {
        log.info("Updating membership with ID: {}", membershipId);

        MembershipType membershipType = membershipTypeRepository.findById(membershipTypeId).orElseThrow(
                () -> new IllegalStateException("cannot update membership for membership type: membership type with ID " + membershipTypeId + " does not exist")
        );

        membership.setMembershipType(membershipType);

        return fullUpdate(membershipId, membership);
    }

    @Override
    public Boolean delete(Long membershipId) {
        log.info("Deleting membership with ID: {}", membershipId);

        Membership membership = membershipRepository.findById(membershipId).orElseThrow(
                () -> new IllegalStateException(
                        "membership with ID " + membershipId + " does not exist"
                )
        );

        if (membership.getDeleted()) {
            throw new IllegalStateException(
                    "membership with ID " + membershipId + " has already been deleted"
            );
        }
        membership.setDeleted(true);

        return TRUE;
    }
}
