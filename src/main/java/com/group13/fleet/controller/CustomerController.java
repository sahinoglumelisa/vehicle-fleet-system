package com.group13.fleet.controller;

import com.group13.fleet.entity.Customer;
import com.group13.fleet.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CustomerController {
    @Autowired
    private CustomerRepository customerRepository;

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @GetMapping("/customer-registration")
    public String customerRegistration() {
        return "customer-registration";
    }

    @PostMapping("/customer-registration")
    public String registerCustomer(@RequestParam(required = false) Integer companyId,
                                   @RequestParam(required = false) String username,
                                   @RequestParam(required = false) String email,
                                   @RequestParam(required = false) String password,
                                   @RequestParam(required = false) String confirmPassword,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        // Add detailed logging to debug
        logger.info("=== REGISTRATION ATTEMPT ===");
        logger.info("Company ID: {}", companyId);
        logger.info("Username: {}", username);
        logger.info("Email: {}", email);
        logger.info("Password provided: {}", password != null ? "Yes" : "No");
        logger.info("Confirm Password provided: {}", confirmPassword != null ? "Yes" : "No");

        try {
            // Validate input - check for null and empty strings
            if (companyId == null) {
                logger.warn("Company ID is null");
                model.addAttribute("error", "Company ID is required");
                return "customer-registration";
            }

            if (username == null || username.trim().isEmpty()) {
                logger.warn("Username is null or empty");
                model.addAttribute("error", "Username is required");
                return "customer-registration";
            }

            if (email == null || email.trim().isEmpty()) {
                logger.warn("Email is null or empty");
                model.addAttribute("error", "Email is required");
                return "customer-registration";
            }

            if (password == null || password.trim().isEmpty()) {
                logger.warn("Password is null or empty");
                model.addAttribute("error", "Password is required");
                return "customer-registration";
            }

            if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
                logger.warn("Confirm password is null or empty");
                model.addAttribute("error", "Please confirm your password");
                return "customer-registration";
            }

            // Trim whitespace from inputs
            username = username.trim();
            email = email.trim();

            // Check password confirmation
            if (!password.equals(confirmPassword)) {
                logger.warn("Passwords do not match");
                model.addAttribute("error", "Passwords do not match");
                return "customer-registration";
            }

            // Check if company ID already exists
            logger.info("Checking if company ID {} exists", companyId);
            if (customerRepository.existsById(companyId)) {
                logger.warn("Company ID {} already exists", companyId);
                model.addAttribute("error", "Company ID already exists");
                return "customer-registration";
            }

            // Check if username already exists
            logger.info("Checking if username {} exists", username);
            if (customerRepository.existsByUsername(username)) {
                logger.warn("Username {} already exists", username);
                model.addAttribute("error", "Username already exists");
                return "customer-registration";
            }

            // Check if email already exists
            logger.info("Checking if email {} exists", email);
            if (customerRepository.existsByEmail(email)) {
                logger.warn("Email {} already exists", email);
                model.addAttribute("error", "Email already exists");
                return "customer-registration";
            }

            // Create and save customer
            logger.info("Creating new customer object");
            Customer customer = new Customer();
            customer.setCompanyId(companyId);
            customer.setUsername(username);
            customer.setEmail(email);
            customer.setPassword(password);

            logger.info("Attempting to save customer to database...");
            Customer savedCustomer = customerRepository.save(customer);
            logger.info("Customer saved successfully! Saved customer ID: {}", savedCustomer.getCompanyId());

            // Verify the save by counting customers
            long customerCount = customerRepository.count();
            logger.info("Total customers in database: {}", customerCount);

            // Success - redirect to avoid form resubmission
            redirectAttributes.addFlashAttribute("success", "Registration successful! You can now login.");
            return "redirect:/customer-login";

        } catch (Exception e) {
            logger.error("Registration failed for username: {} with error: {}", username, e.getMessage(), e);
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "customer-registration";
        }
    }
    @GetMapping("/customer")
    public String showCustomerMenu(Model model) {
        return "customer"; // 3 butonlu sayfa
    }

    @GetMapping("/customer-login")
    public String customerLogin(Model model) {
        logger.info("=== CUSTOMER LOGIN PAGE ACCESSED ===");
        return "customer-login";
    }
    @GetMapping("/customer/vehicle-management")
    public String showCustomerVehicleManagement(Model model) {
        return "customer-vehicle-management";
    }

    @GetMapping("/customer/task-driver-management")
    public String showCustomerTaskDriverManagement(Model model) {
        return "customer-task-driver-management";
    }

    @GetMapping("/customer/report-prediction")
    public String showCustomerReportPrediction(Model model) {
        return "customer-report-prediction";
    }
}