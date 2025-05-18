package com.group13.fleet;

import com.group13.fleet.controller.OutsourceServiceFuelController;
import com.group13.fleet.entity.*;
import com.group13.fleet.repository.DriverRepository;
import com.group13.fleet.repository.FuelConsumptionRepository;
import com.group13.fleet.repository.ServiceRepository;
import com.group13.fleet.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
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
				createMockFuelConsumption(1L, 1, 1),
				createMockFuelConsumption(2L, 2, 2)
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
				createMockFuelConsumption(1L, 1, 1),
				createMockFuelConsumption(2L, 2, 2)
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

	private FuelConsumption createMockFuelConsumption(Long fuelId, Integer vehicleId, Integer driverId) {
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
}