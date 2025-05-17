package com.group13.fleet.repository;

import com.group13.fleet.entity.FuelConsumption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FuelConsumptionRepository extends JpaRepository<FuelConsumption, Long> {

}
