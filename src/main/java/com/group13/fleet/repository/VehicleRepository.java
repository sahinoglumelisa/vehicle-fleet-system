package com.group13.fleet.repository;

import com.group13.fleet.entity.Vehicle;
import com.group13.fleet.entity.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Vehicle findByPlateNumber(String plateNumber);
    List<Vehicle> findVehicleByStatus(VehicleStatus status);
}
