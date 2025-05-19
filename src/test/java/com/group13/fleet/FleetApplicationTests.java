package com.group13.fleet;

import com.group13.fleet.controller.OutsourceServiceFuelController;
import com.group13.fleet.entity.*;
import com.group13.fleet.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.group13.fleet.controller.OutsourceVehicleManagementController;
import com.group13.fleet.entity.OwnershipType;
import com.group13.fleet.entity.VehicleStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class FleetApplicationTests {
	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private VehicleRepository vehicleRepository;

	@Mock
	private DriverRepository driverRepository;

	@Mock
	private FuelConsumptionRepository fuelConsumptionRepository;

	@Mock
	private ServiceRepository serviceRepository;

	@Mock
	private RedirectAttributes redirectAttributes;

	@InjectMocks
	private OutsourceServiceFuelController controller;

	@InjectMocks
	private OutsourceVehicleManagementController vehicleController;

	private MockMvc vehicleMockMvc;
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
		vehicleMockMvc = MockMvcBuilders.standaloneSetup(vehicleController).build();
	}

	@Test
	void testShowServiceFuelManagement() throws Exception {
		mockMvc.perform(get("/outsource/service-fuel-management"))
				.andExpect(status().isOk())
				.andExpect(view().name("outsource-service-fuel-management"));
	}

	@Test
	void testShowServiceEntryForm() throws Exception {
		// Mock data
		List<Vehicle> vehicles = Arrays.asList(
				createMockVehicle(1, "Toyota", "Camry", "ABC123"),
				createMockVehicle(2, "Honda", "Civic", "DEF456")
		);

		when(vehicleRepository.findAll()).thenReturn(vehicles);

		mockMvc.perform(get("/outsource/service-fuel-management/service-entry"))
				.andExpect(status().isOk())
				.andExpect(view().name("outsource-service-entry"))
				.andExpect(model().attributeExists("service"))
				.andExpect(model().attributeExists("serviceTypes"))
				.andExpect(model().attributeExists("vehicles"))
				.andExpect(model().attribute("vehicles", vehicles));

		verify(vehicleRepository).findAll();
	}

	@Test
	void testSubmitServiceEntry_Success() throws Exception {
		Service service = new Service();
		service.setServiceId(1L);
		service.setServiceType(ServiceType.REGULAR_MAINTENANCE); // Assuming this enum exists

		when(serviceRepository.save(any(Service.class))).thenReturn(service);

		String result = controller.submitServiceEntry(service, redirectAttributes);

		assertEquals("redirect:/outsource/service-fuel-management", result);
		verify(serviceRepository).save(service);
		verify(redirectAttributes).addFlashAttribute("successMessage", "Service entry saved successfully!");
		// Verify that the service date was set (should be today)
		assertNotNull(service.getServiceDate());
	}

	@Test
	void testSubmitServiceEntry_Exception() throws Exception {
		Service service = new Service();

		when(serviceRepository.save(any(Service.class))).thenThrow(new RuntimeException("Database error"));

		String result = controller.submitServiceEntry(service, redirectAttributes);

		assertEquals("redirect:/outsource/service-fuel-management", result);
		verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), contains("Error saving service entry"));
	}

	@Test
	void testShowFuelTrackingPage() throws Exception {
		// Mock data
		List<Vehicle> vehicles = Arrays.asList(
				createMockVehicle(1, "Toyota", "Camry", "ABC123"),
				createMockVehicle(2, "Honda", "Civic", "DEF456")
		);

		List<Driver> drivers = Arrays.asList(
				createMockDriver(1, "john_doe"),
				createMockDriver(2, "jane_smith")
		);

		List<FuelConsumption> fuelEntries = Arrays.asList(
				createMockFuelConsumption(1, 1, 1),
				createMockFuelConsumption(2, 2, 2)
		);

		when(vehicleRepository.findAll()).thenReturn(vehicles);
		when(driverRepository.findAll()).thenReturn(drivers);
		when(fuelConsumptionRepository.findAll()).thenReturn(fuelEntries);
		when(vehicleRepository.findByVehicleId(1)).thenReturn(vehicles.get(0));
		when(vehicleRepository.findByVehicleId(2)).thenReturn(vehicles.get(1));
		when(driverRepository.findByUserId(1)).thenReturn(drivers.get(0));
		when(driverRepository.findByUserId(2)).thenReturn(drivers.get(1));

		mockMvc.perform(get("/outsource/service-fuel-management/fuel-tracking"))
				.andExpect(status().isOk())
				.andExpect(view().name("outsource-fuel-tracking"))
				.andExpect(model().attributeExists("vehicles"))
				.andExpect(model().attributeExists("drivers"))
				.andExpect(model().attributeExists("fuelEntries"))
				.andExpect(model().attributeExists("vehicleNames"))
				.andExpect(model().attributeExists("driverNames"));

		verify(vehicleRepository).findAll();
		verify(driverRepository).findAll();
		verify(fuelConsumptionRepository).findAll();
	}

	@Test
	void testSaveFuelEntry_Success() throws Exception {
		Vehicle mockVehicle = createMockVehicle(1, "Toyota", "Camry", "ABC123");
		Driver mockDriver = createMockDriver(1, "john_doe");

		when(vehicleRepository.findByVehicleId(1)).thenReturn(mockVehicle);
		when(driverRepository.findByUserId(1)).thenReturn(mockDriver);
		when(fuelConsumptionRepository.save(any(FuelConsumption.class))).thenReturn(new FuelConsumption());

		mockMvc.perform(post("/outsource/service-fuel-management/fuel-tracking/save")
						.param("vehicle.vehicleId", "1")
						.param("driver.userId", "1")
						.param("date", "2024-01-15")
						.param("liters", "50.5")
						.param("cost", "75.25")
						.param("odometerReading", "25000.0"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/outsource/service-fuel-management/fuel-tracking"));

		verify(vehicleRepository).findByVehicleId(1);
		verify(driverRepository).findByUserId(1);
		verify(fuelConsumptionRepository).save(any(FuelConsumption.class));
	}

	@Test
	void testSaveFuelEntry_VehicleNotFound() throws Exception {
		when(vehicleRepository.findByVehicleId(1)).thenReturn(null);

		String result = controller.saveFuelEntry(1, 1, LocalDate.of(2024, 1, 15),
				50.5, 75.25, 25000.0, redirectAttributes);

		assertEquals("redirect:/outsource/service-fuel-management/fuel-tracking", result);
		verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), contains("Vehicle not found"));
	}

	@Test
	void testSaveFuelEntry_DriverNotFound() throws Exception {
		Vehicle mockVehicle = createMockVehicle(1, "Toyota", "Camry", "ABC123");

		when(vehicleRepository.findByVehicleId(1)).thenReturn(mockVehicle);
		when(driverRepository.findByUserId(1)).thenReturn(null);

		String result = controller.saveFuelEntry(1, 1, LocalDate.of(2024, 1, 15),
				50.5, 75.25, 25000.0, redirectAttributes);

		assertEquals("redirect:/outsource/service-fuel-management/fuel-tracking", result);
		verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), contains("Driver not found"));
	}

	@Test
	void testSaveFuelEntry_DatabaseException() throws Exception {
		Vehicle mockVehicle = createMockVehicle(1, "Toyota", "Camry", "ABC123");
		Driver mockDriver = createMockDriver(1, "john_doe");

		when(vehicleRepository.findByVehicleId(1)).thenReturn(mockVehicle);
		when(driverRepository.findByUserId(1)).thenReturn(mockDriver);
		when(fuelConsumptionRepository.save(any(FuelConsumption.class)))
				.thenThrow(new RuntimeException("Database connection failed"));

		String result = controller.saveFuelEntry(1, 1, LocalDate.of(2024, 1, 15),
				50.5, 75.25, 25000.0, redirectAttributes);

		assertEquals("redirect:/outsource/service-fuel-management/fuel-tracking", result);
		verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), contains("Error saving fuel entry"));
	}

	@Test
	void testFilterFuelEntries() throws Exception {
		List<FuelConsumption> fuelEntries = Arrays.asList(
				createMockFuelConsumption(1, 1, 1),
				createMockFuelConsumption(2, 2, 2)
		);

		when(fuelConsumptionRepository.findAll()).thenReturn(fuelEntries);

		mockMvc.perform(get("/outsource/service-fuel-management/fuel-tracking/filter")
						.param("vehicleId", "1")
						.param("driverId", "1")
						.param("startDate", "2024-01-01")
						.param("endDate", "2024-12-31"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"));

		verify(fuelConsumptionRepository).findAll();
	}

	// Helper methods to create mock objects
	private Vehicle createMockVehicle(int id, String brand, String model, String plateNumber) {
		Vehicle vehicle = new Vehicle();
		vehicle.setVehicleId(id);
		vehicle.setBrand(brand);
		vehicle.setModel(model);
		vehicle.setPlateNumber(plateNumber);
		return vehicle;
	}

	private Driver createMockDriver(int id, String username) {
		Driver driver = new Driver();
		driver.setUserId(id);
		driver.setUsername(username);
		return driver;
	}

	private FuelConsumption createMockFuelConsumption(Integer fuelId, Integer vehicleId, Integer driverId) {
		FuelConsumption fuelConsumption = new FuelConsumption();
		fuelConsumption.setFuelId(fuelId);
		fuelConsumption.setVehicle(vehicleId);
		fuelConsumption.setDriver(driverId);
		fuelConsumption.setLiters(50.0);
		fuelConsumption.setCost(75.0);
		fuelConsumption.setOdometerReading(25000.0);
		fuelConsumption.setDate(new java.sql.Date(System.currentTimeMillis()));
		return fuelConsumption;
	}

	@Test
	void testListVehicles() throws Exception {
		// Mock data - only leased vehicles
		List<Vehicle> leasedVehicles = Arrays.asList(
				createMockLeasedVehicle(1, "Toyota", "Camry", "ABC123"),
				createMockLeasedVehicle(2, "Honda", "Civic", "DEF456")
		);

		when(vehicleRepository.findByOwnershipType(OwnershipType.LEASED)).thenReturn(leasedVehicles);

		vehicleMockMvc.perform(get("/outsource/management-vehicles/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("outsource-vehicle-list"))
				.andExpect(model().attributeExists("vehicles"))
				.andExpect(model().attribute("vehicles", leasedVehicles));

		verify(vehicleRepository).findByOwnershipType(OwnershipType.LEASED);
	}

	@Test
	void testAddVehicleForm() throws Exception {
		vehicleMockMvc.perform(get("/outsource/management-vehicles/add"))
				.andExpect(status().isOk())
				.andExpect(view().name("outsource-vehicle-add"))
				.andExpect(model().attributeExists("vehicle"));
	}

	@Test
	void testAddVehicle_Success() throws Exception {
		// Tries to add vehicle from route
		Vehicle savedVehicle = createMockLeasedVehicle(1, "Ford", "Focus", "GHI789");

		when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);

		vehicleMockMvc.perform(post("/outsource/management-vehicles/add")
						.param("brand", "Ford")
						.param("model", "Focus")
						.param("plateNumber", "GHI789")
						.param("year", "2023")
						.param("currentOdometer", "15000")
						.param("type", "SEDAN")
						.param("status", "ASSIGNED"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/outsource/management-vehicles/list"));

		verify(vehicleRepository).save(any(Vehicle.class));
	}

	@Test
	void testAddVehicle_WithNullPreviousOdometer() throws Exception {
		// Test that previous month odometer is set to current odometer when null
		Vehicle capturedVehicle = null;

		when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> {
			Vehicle vehicle = invocation.getArgument(0);
			// Verify that the logic was applied correctly
			assertEquals(OwnershipType.LEASED, vehicle.getOwnershipType());
			assertEquals(vehicle.getCurrentOdometer(), vehicle.getPreviousMonthOdometer());
			assertNull(vehicle.getCustomer());
			return vehicle;
		});

		vehicleMockMvc.perform(post("/outsource/management-vehicles/add")
						.param("brand", "BMW")
						.param("model", "X3")
						.param("plateNumber", "JKL012")
						.param("year", "2022")
						.param("currentOdometer", "20000"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/outsource/management-vehicles/list"));

		verify(vehicleRepository).save(any(Vehicle.class));
	}

	@Test
	void testShowEditForm_VehicleExists() throws Exception {
		//Tries to access the edit form
		Vehicle existingVehicle = createMockLeasedVehicle(1, "Audi", "A4", "MNO345");

		when(vehicleRepository.findByPlateNumber("MNO345")).thenReturn(existingVehicle);

		vehicleMockMvc.perform(get("/outsource/management-vehicles/edit")
						.param("plate", "MNO345"))
				.andExpect(status().isOk())
				.andExpect(view().name("outsource-vehicle-edit"))
				.andExpect(model().attributeExists("vehicle"))
				.andExpect(model().attribute("vehicle", existingVehicle));

		verify(vehicleRepository).findByPlateNumber("MNO345");
	}

	@Test
	void testShowEditForm_VehicleNotFound() throws Exception {
		//Tries to update with notfound plateNum
		when(vehicleRepository.findByPlateNumber("NOTFOUND")).thenReturn(null);

		vehicleMockMvc.perform(get("/outsource/management-vehicles/edit")
						.param("plate", "NOTFOUND"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/outsource/management-vehicles/list"));

		verify(vehicleRepository).findByPlateNumber("NOTFOUND");
	}

	@Test
	void testUpdateVehicle_Success() throws Exception {
		/* MELİSA*/
		// Tries to update a vehicle by directly using route
		Vehicle existingVehicle = createMockLeasedVehicle(1, "Mercedes", "C-Class", "PQR678");
		existingVehicle.setYear(2020);
		existingVehicle.setCurrentOdometer(30000.0);

		when(vehicleRepository.findByPlateNumber("PQR678")).thenReturn(existingVehicle);
		when(vehicleRepository.save(any(Vehicle.class))).thenReturn(existingVehicle);

		vehicleMockMvc.perform(post("/outsource/management-vehicles/edit")
						.param("plateNumber", "PQR678")
						.param("brand", "Mercedes")
						.param("model", "C-Class")
						.param("year", "2021")
						.param("currentOdometer", "35000")
						.param("type", "SEDAN")
						.param("status", "ASSIGNED"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/outsource/management-vehicles/list"));

		verify(vehicleRepository).findByPlateNumber("PQR678");
		verify(vehicleRepository).save(existingVehicle);

		// Verify that the vehicle was updated
		assertEquals("Mercedes", existingVehicle.getBrand());
		assertEquals("C-Class", existingVehicle.getModel());
		assertEquals(2021, existingVehicle.getYear());
		assertEquals(35000.0, existingVehicle.getCurrentOdometer());
	}

	@Test
	void testUpdateVehicle_VehicleNotFound() throws Exception {
		/* DENİZ */
		//Scenario where the vehicle is not being found by using plate number
		when(vehicleRepository.findByPlateNumber("NOTFOUND")).thenReturn(null);

		vehicleMockMvc.perform(post("/outsource/management-vehicles/edit")
						.param("plateNumber", "NOTFOUND")
						.param("brand", "Toyota")
						.param("model", "Prius")
						.param("year", "2023"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/outsource/management-vehicles/list"));

		verify(vehicleRepository).findByPlateNumber("NOTFOUND");
		verify(vehicleRepository, never()).save(any(Vehicle.class));
	}

	@Test
	void testAddVehicle_DirectControllerCall() {
		//* MELİSA *//
		Vehicle vehicle = new Vehicle();
		vehicle.setBrand("Tesla");
		vehicle.setModel("Model 3");
		vehicle.setPlateNumber("STU901");
		vehicle.setCurrentOdometer(5000.0);

		when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

		// It directly tests add vehicle functionality of controller
		String result = vehicleController.addVehicle(vehicle);

		assertEquals("redirect:/outsource/management-vehicles/list", result);
		assertEquals(OwnershipType.LEASED, vehicle.getOwnershipType());
		assertNull(vehicle.getCustomer());
		assertEquals(vehicle.getCurrentOdometer(), vehicle.getPreviousMonthOdometer());

		verify(vehicleRepository).save(vehicle);
	}

	@Test
	void testUpdateVehicle_DirectControllerCall() {
		//* DENİZ *//
		// Test the controller method directly
		Vehicle existingVehicle = createMockLeasedVehicle(1, "Volkswagen", "Golf", "VWX234");
		Vehicle updateMockVehicle = new Vehicle();
		updateMockVehicle.setPlateNumber("VWX234");
		updateMockVehicle.setBrand("Volkswagen");
		updateMockVehicle.setModel("Golf GTI");
		updateMockVehicle.setYear(2023);
		updateMockVehicle.setStatus(VehicleStatus.AVAILABLE);
		updateMockVehicle.setCurrentOdometer(18000.0);

		when(vehicleRepository.findByPlateNumber("VWX234")).thenReturn(existingVehicle);
		when(vehicleRepository.save(existingVehicle)).thenReturn(existingVehicle);

		String result = vehicleController.updateVehicle(updateMockVehicle);

		assertEquals("redirect:/outsource/management-vehicles/list", result);

		// It checks whether the vehicle is being updated
		assertEquals("Volkswagen", existingVehicle.getBrand());
		assertEquals("Golf GTI", existingVehicle.getModel());
		assertEquals(2023, existingVehicle.getYear());
		assertEquals(VehicleStatus.AVAILABLE, existingVehicle.getStatus());
		assertEquals(18000.0, existingVehicle.getCurrentOdometer());

		verify(vehicleRepository).findByPlateNumber("VWX234");
		verify(vehicleRepository).save(existingVehicle);
	}

	// It is an helper method which creates a mock vehicle
	private Vehicle createMockLeasedVehicle(int id, String brand, String model, String plateNumber) {
		Vehicle vehicle = new Vehicle();
		vehicle.setVehicleId(id);
		vehicle.setBrand(brand);
		vehicle.setModel(model);
		vehicle.setPlateNumber(plateNumber);
		vehicle.setOwnershipType(OwnershipType.LEASED);
		vehicle.setYear(2022);
		vehicle.setStatus(VehicleStatus.AVAILABLE);
		vehicle.setCurrentOdometer(10000.0);
		vehicle.setPreviousMonthOdometer(9500.0);
		return vehicle;
	}

}