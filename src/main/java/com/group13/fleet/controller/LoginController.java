package com.group13.fleet.controller;

import com.group13.fleet.entity.Customer;
import com.group13.fleet.entity.Driver;
import com.group13.fleet.entity.SystemAdmin;
import com.group13.fleet.repository.CustomerRepository;
import com.group13.fleet.repository.DriverRepository;
import com.group13.fleet.repository.SystemAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class LoginController {

    private final CustomerRepository customerRepository;
    private final DriverRepository driverRepository;
    private final SystemAdminRepository sysAdminRepository;

    @Autowired
    public LoginController(CustomerRepository customerRepository,
                           DriverRepository driverRepository,
                           SystemAdminRepository sysAdminRepository) {
        this.customerRepository = customerRepository;
        this.driverRepository = driverRepository;
        this.sysAdminRepository = sysAdminRepository;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        // Main login page that will redirect to specific login pages
        return "login";
    }

    @GetMapping("/customer-login-page")
    public String showCustomerLoginPage(Model model) {
        System.out.println("Customer Login Page");
        return "customer-login";
    }

    @GetMapping("/admin-login-page")
    public String showAdminLoginPage(Model model) {
        return "admin-login";
    }

    @GetMapping("/driver-login-page")
    public String showDriverLoginPage(Model model) {
        return "driver-login";
    }

    // Customer login handler
    @PostMapping("/customer-login")
    public String processCustomerLogin(@RequestParam("email") String email,
                                       @RequestParam("password") String password,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {

        Customer customer = customerRepository.findByEmail(email);
        System.out.println("Bu adam her şeyi biliyor ama her şeyi");

        if (customer != null && customer.getPassword().equals(password)) {
            // Authentication successful
            session.setAttribute("customerId", customer.getCompanyId());
            session.setAttribute("customerName", customer.getUsername());
            session.setAttribute("userType", "customer");

            // Redirect to customer dashboard
            return "redirect:/customer/dashboard";
        } else {
            // Authentication failed
            redirectAttributes.addFlashAttribute("error", "Invalid email or password");
            return "redirect:/customer-login-page";
        }
    }

    // Driver login handler
    @PostMapping("/driver-login")
    public String processDriverLogin(@RequestParam("email") String email,
                                     @RequestParam("password") String password,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {

        Driver driver = driverRepository.findByEmail(email);

        if (driver != null && driver.getPassword().equals(password)) {
            // Authentication successful
            session.setAttribute("driverId", driver.getUserId());
            session.setAttribute("driverName", driver.getUsername());
            session.setAttribute("userType", "driver");

            // Redirect to driver dashboard
            return "redirect:/driver/dashboard";
        } else {
            // Authentication failed
            redirectAttributes.addFlashAttribute("error", "Invalid email or password");
            return "redirect:/driver-login-page";
        }
    }

    // Admin login handler
    @PostMapping("/admin-login")
    public String processAdminLogin(@RequestParam("email") String email,
                                    @RequestParam("password") String password,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {

        SystemAdmin admin = sysAdminRepository.findByEmail(email);
        System.out.println("Admin Login Page");

        if (admin != null && admin.getPassword().equals(password)) {
            // Authentication successful
            session.setAttribute("adminId", admin.getUserId());
            session.setAttribute("adminName", admin.getUsername());
            session.setAttribute("userType", "admin");
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(email, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Redirect to admin dashboard
            return "redirect:/";
        } else {
            // Authentication failed
            redirectAttributes.addFlashAttribute("error", "Invalid email or password");
            return "redirect:/admin-login-page";
        }
    }

    // Logout handler for all user types
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Invalidate the session to log the user out
        session.invalidate();
        return "redirect:/login";
    }
}