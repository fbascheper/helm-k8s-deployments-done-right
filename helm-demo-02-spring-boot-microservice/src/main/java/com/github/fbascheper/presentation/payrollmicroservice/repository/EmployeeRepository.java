package com.github.fbascheper.presentation.payrollmicroservice.repository;

import com.github.fbascheper.presentation.payrollmicroservice.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring repository for {@link Employee}.
 *
 * @author Frederieke Scheper
 * @since 30-09-2023
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
