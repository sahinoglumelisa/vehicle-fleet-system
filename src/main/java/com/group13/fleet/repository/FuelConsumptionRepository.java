package com.group13.fleet.repository;

import com.group13.fleet.entity.FuelConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FuelConsumptionRepository extends JpaRepository<FuelConsumption, Integer> {
    @Query("SELECT DISTINCT fc.vehicle FROM FuelConsumption fc")
    List<Integer> findDistinctVehicleIds();

    List<FuelConsumption> findByVehicle(int vehicleId);
}
