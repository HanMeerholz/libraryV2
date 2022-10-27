package com.yer.library.repository;

import com.yer.library.model.Membership;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    @Query("SELECT m FROM Membership m WHERE m.deleted = false")
    List<Membership> listAvailable(Pageable pageable);

    @Query("SELECT m FROM Membership m WHERE m.membershipType.id = ?1 AND m.deleted = false")
    List<Membership> listByMembershipType(Long membershipId, Pageable pageable);
}
