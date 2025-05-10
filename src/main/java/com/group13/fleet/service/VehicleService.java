package com.group13.fleet.service;


import com.group13.fleet.entity.Vehicle;

import java.util.List;

public interface VehicleService {
    List<Vehicle> getVehiclesAndSortAndChangeSecondLetter();
}
