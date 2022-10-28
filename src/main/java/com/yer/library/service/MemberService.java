package com.yer.library.service;

import com.yer.library.model.Member;
import com.yer.library.model.Membership;
import com.yer.library.repository.MemberRepository;
import com.yer.library.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;

import static java.lang.Boolean.TRUE;
import static org.springframework.data.domain.PageRequest.of;
import static org.springframework.data.domain.PageRequest.ofSize;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService implements CrudService<Member> {
    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;

    @Override
    public Member get(Long memberId) {
        log.info("Fetching member with ID: {}", memberId);
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalStateException(
                        "member with ID " + memberId + " does not exist"
                )
        );
        if (member.getDeleted()) {
            throw new IllegalStateException("member with ID " + member.getId() + " does not exist");
        }

        return member;
    }

    @Override
    public Collection<Member> list(int limit) {
        log.info("Listing all members (up to a limit of {})", limit);

        return memberRepository.listAvailable(of(0, limit));
    }

    public Collection<Member> listByMembership(Long membershipId, int limit) {
        log.info("Listing all memberships for membership type with ID {} (up to a limit of {})", membershipId, limit);

        return memberRepository.listByMembership(membershipId, ofSize(limit));
    }

    @Override
    public Member add(Member member) {
        memberRepository.findByEmail(member.getEmailAddress()).ifPresent(existingMember -> {
            if (existingMember.getDeleted()) {
                member.setId(existingMember.getId());
            } else {
                throw new IllegalStateException("email " + member.getEmailAddress() + " already exists.");
            }
        });
        return memberRepository.save(member);
    }

    public Member add(Member member, Long membershipId) {
        log.info("Adding new member (name = {})", member.getName());

        if (membershipId == null) {
            return add(member);
        }

        Membership membership = membershipRepository.findById(membershipId).orElseThrow(
                () -> new IllegalStateException("cannot add member for membership: membership with ID " + membershipId + " does not exist")
        );

        member.setMembership(membership);

        return add(member);
    }

    @Override
    public Member fullUpdate(Long memberId, Member updatedMember) {
        Member existingMember = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalStateException(
                        "member with ID " + memberId + " does not exist"
                )
        );

        if (!updatedMember.getEmailAddress().equals(existingMember.getEmailAddress())) {
            memberRepository.findByEmail(updatedMember.getEmailAddress()).ifPresent(memberWithSameEmail -> {
                if (!memberWithSameEmail.getDeleted()) {
                    throw new IllegalStateException("email " + memberWithSameEmail.getEmailAddress() + " already exists");
                }
            });
        }

        updatedMember.setId(memberId);
        memberRepository.save(updatedMember);

        return updatedMember;
    }

    public Member fullUpdate(Long memberId, Member member, Long membershipId) {
        log.info("Updating member with ID: {}", memberId);

        if (membershipId == null) {
            member.setMembership(null);
            return fullUpdate(memberId, member);
        }

        Membership membership = membershipRepository.findById(membershipId).orElseThrow(
                () -> new IllegalStateException("cannot update member for membership: membership with ID " + membershipId + " does not exist")
        );

        member.setMembership(membership);

        return fullUpdate(memberId, member);
    }

    @Override
    public Boolean delete(Long memberId) {
        log.info("Deleting member with ID: {}", memberId);

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalStateException(
                        "member with ID " + memberId + " does not exist"
                )
        );

        if (member.getDeleted()) {
            throw new IllegalStateException(
                    "member with ID " + memberId + " has already been deleted"
            );
        }
        member.setDeleted(true);
        member.getMembership().getMembers().remove(member);

        return TRUE;
    }
}
