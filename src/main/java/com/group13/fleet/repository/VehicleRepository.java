package com.group13.fleet.repository;

import com.group13.fleet.entity.Driver;
import com.group13.fleet.entity.Vehicle;
import com.group13.fleet.entity.VehicleStatus;
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

    @Query("SELECT DISTINCT v.driver FROM Vehicle v WHERE v.customer = :companyId")
    List<Driver> findDriversByCompanyId(@Param("companyId") Integer companyId);
}


