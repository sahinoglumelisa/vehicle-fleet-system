package com.group13.fleet.repository;

import com.group13.fleet.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer> {
    Optional<Driver> findByUsername(String username);
    Driver findByEmail(String email);
    Driver findByUserId(Integer id);

    @Query("SELECT d FROM Driver d WHERE d.userId IN :driverIds")
    List<Driver> findDriversByIds(@Param("driverIds") List<Integer> driverIds);

    @Query("SELECT d FROM Driver d WHERE d.userId IN :driverIds AND d.isActive = :isActive")
    List<Driver> findDriversByIdsAndStatus(@Param("driverIds") List<Integer> driverIds,
                                           @Param("isActive") boolean isActive);

    @Query("SELECT d FROM Driver d WHERE d.companyId =:customerId AND d.isActive = :isActive")
    List<Driver> findDriversByCompanyIdAndStatus(@Param("customerId") Integer customerId,
                                           @Param("isActive") boolean isActive);

    List<Driver> findDriversByCompanyId(Integer companyId);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByLicenseNumber(String licenseNumber);

}
