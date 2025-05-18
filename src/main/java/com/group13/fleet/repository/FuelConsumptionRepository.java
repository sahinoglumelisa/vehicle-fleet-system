package com.group13.fleet.repository;

import com.group13.fleet.entity.FuelConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface FuelConsumptionRepository extends JpaRepository<FuelConsumption, Long> {
    List<FuelConsumption> findAllByOrderByDateDesc();

}
