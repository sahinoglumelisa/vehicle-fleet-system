package com.group13.fleet.repository;

import com.group13.fleet.entity.VehicleUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleUsageRepository extends JpaRepository<VehicleUsage, Integer> {
}
