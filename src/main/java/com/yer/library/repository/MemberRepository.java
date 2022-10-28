package com.yer.library.repository;

import com.yer.library.model.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("SELECT m FROM Member m WHERE m.emailAddress = ?1 AND m.deleted = false")
    Optional<Member> findByEmail(String emailAddress);

    @Query("SELECT m FROM Member m WHERE m.deleted = false")
    List<Member> listAvailable(Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.membership.id = ?1 AND m.deleted = false")
    List<Member> listByMembership(Long membershipId, Pageable pageable);
}
