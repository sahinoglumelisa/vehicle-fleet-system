package com.group13.fleet.repository;

import com.group13.fleet.entity.SystemAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemAdminRepository extends JpaRepository<SystemAdmin, Integer> {
    Optional<SystemAdmin> findByUsername(String username);
    SystemAdmin findByEmail(String email);
}
