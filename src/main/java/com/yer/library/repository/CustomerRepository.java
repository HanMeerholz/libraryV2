package com.yer.library.repository;

import com.yer.library.model.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT c FROM Customer c WHERE c.emailAddress = ?1")
    Optional<Customer> findByEmail(String emailAddress);

    @Query("SELECT c FROM Customer c WHERE c.deleted = false")
    List<Customer> listAvailable(Pageable pageable);
}
