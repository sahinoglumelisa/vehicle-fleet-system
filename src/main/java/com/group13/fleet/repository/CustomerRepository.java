package com.group13.fleet.repository;

import com.group13.fleet.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByUsername(String username);
    Customer findByEmail(String email);
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByCompanyId(Integer companyId);
}
