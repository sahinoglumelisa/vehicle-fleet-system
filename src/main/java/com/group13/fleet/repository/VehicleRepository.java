package com.group13.fleet.repository;

import com.group13.fleet.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    Vehicle findByPlateNumber(String plateNumber);
    List<Vehicle> findByStatus(VehicleStatus status);
    List<Vehicle> findVehicleByCustomer(Integer customerId);
    Vehicle findFirstByCustomerIsNull();
    Optional<Vehicle> findVehicleByDriver(Integer driverId);
    List<Vehicle> findByOwnershipType(com.group13.fleet.entity.OwnershipType ownershipType);
    Vehicle findByVehicleId(Integer vehicleId);

    @Query("SELECT DISTINCT v.driver FROM Vehicle v WHERE v.customer = :companyId AND v.driver IS NOT NULL")
    List<Integer> findDriversByCompanyId(@Param("companyId") Integer companyId);

    List<Vehicle> findByCustomerAndStatus(Integer customer, VehicleStatus status);

    List<Vehicle> findByStatusAndCustomerIsNull(VehicleStatus vehicleStatus);

    List<Vehicle> findVehicleByCustomerIsNull();

    List<Vehicle> findVehiclesByCustomerAndStatus(Integer customer, VehicleStatus status);

    @Query("SELECT DISTINCT v FROM Vehicle v WHERE v.customer = :customerId AND v.status IN (:available)")
    List<Vehicle> findByCustomerAndStatuses(Integer customerId, List<VehicleStatus> available);

    List<Vehicle> findVehicleByCustomerAndOwnershipType(Integer customerId, OwnershipType ownershipType);

    boolean existsByPlateNumber(String plateNumber);

}


