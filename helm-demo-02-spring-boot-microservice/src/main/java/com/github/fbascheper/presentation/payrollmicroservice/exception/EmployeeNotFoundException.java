package com.github.fbascheper.presentation.payrollmicroservice.exception;

/**
 * Exception to indicate that an employee could not be found.
 *
 * @author Frederieke Scheper
 * @since 30-09-2023
 */
public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(Long id) {
        super("Could not find employee " + id);

    }
}
