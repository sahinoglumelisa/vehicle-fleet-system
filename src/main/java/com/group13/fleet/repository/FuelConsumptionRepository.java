package com.group13.fleet.repository;

import com.group13.fleet.entity.FuelConsumption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuelConsumptionRepository extends JpaRepository<FuelConsumption, Long> {
}
