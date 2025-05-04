package com.group13.fleet.controller;

import com.group13.fleet.entity.Company;
import com.group13.fleet.entity.User;
import com.group13.fleet.repository.CompanyRepository;
import com.group13.fleet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    UserRepository userRepository;
    CompanyRepository companyRepository;

    @Autowired
    public UserController(UserRepository userRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();

    }
}
