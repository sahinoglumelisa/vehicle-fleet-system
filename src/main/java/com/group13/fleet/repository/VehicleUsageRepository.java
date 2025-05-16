package com.group13.fleet.repository;

import com.group13.fleet.entity.VehicleUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleUsageRepository extends JpaRepository<VehicleUsage, Integer> {
    List<VehicleUsage> findAllByDriver(Integer driverId);
    Optional<VehicleUsage> findTopByDriverOrderByStartDateDesc(Integer driverId);
    Optional<VehicleUsage> findVehicleUsageByDriver(Integer DriverId);
    @Query("SELECT v FROM VehicleUsage v WHERE v.driver = :driverId AND v.endDate >= CURRENT_DATE ORDER BY v.startDate ASC")
    Optional<VehicleUsage> findFirstOngoingOrUpcomingUsage(@Param("driverId") Integer driverId);

    Integer driver(Integer driver);
}
