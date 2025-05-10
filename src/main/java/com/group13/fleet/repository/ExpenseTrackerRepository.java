package com.group13.fleet.repository;

import com.group13.fleet.entity.ExpenseTracker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseTrackerRepository extends JpaRepository<ExpenseTracker, Long> {
}
