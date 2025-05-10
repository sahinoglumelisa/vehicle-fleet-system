package com.group13.fleet.service;

import com.group13.fleet.entity.Vehicle;
import com.group13.fleet.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public List<Vehicle> getVehiclesAndSortAndChangeSecondLetter() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return vehicles;
    }
}
