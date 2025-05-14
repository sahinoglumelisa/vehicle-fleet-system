package com.group13.fleet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.group13.fleet.entity.Customer;
import com.group13.fleet.entity.Driver;
import com.group13.fleet.entity.SystemAdmin;
import com.group13.fleet.repository.CustomerRepository;
import com.group13.fleet.repository.DriverRepository;
import com.group13.fleet.repository.SystemAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final CustomerRepository customerRepository;
    private final DriverRepository driverRepository;
    private final SystemAdminRepository sysAdminRepository;

    @Autowired
    public CustomUserDetailsService(CustomerRepository customerRepository,
                                    DriverRepository driverRepository,
                                    SystemAdminRepository sysAdminRepository) {
        this.customerRepository = customerRepository;
        this.driverRepository = driverRepository;
        this.sysAdminRepository = sysAdminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Check if user exists in any of the three repositories
        Optional<Customer> customer = customerRepository.findByUsername(username);
        if (customer.isPresent()) {
            return buildUserDetails(customer.get().getUsername(), customer.get().getPassword(), "CUSTOMER");
        }

        Optional<Driver> driver = driverRepository.findByUsername(username);
        if (driver.isPresent()) {
            return buildUserDetails(driver.get().getUsername(), driver.get().getPassword(), "DRIVER");
        }

        Optional<SystemAdmin> admin = sysAdminRepository.findByUsername(username);
        if (admin.isPresent()) {
            return buildUserDetails(admin.get().getUsername(), admin.get().getPassword(), "ADMIN");
        }

        // If not found in any repository, throw exception
        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    private UserDetails buildUserDetails(String username, String password, String role) {
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("{noop}" + password)
                .roles(role)
                .build();
    }
}