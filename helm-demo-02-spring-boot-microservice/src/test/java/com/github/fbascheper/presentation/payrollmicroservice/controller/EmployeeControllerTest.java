package com.github.fbascheper.presentation.payrollmicroservice.controller;

import com.github.fbascheper.presentation.payrollmicroservice.domain.Employee;
import com.github.fbascheper.presentation.payrollmicroservice.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link EmployeeController}.
 *
 * @author Frederieke Scheper
 * @since 30-09-2023
 */
@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    EmployeeRepository repository;

    /**
     * Test of all method, of class EmployeeController.
     */
    @Test
    void testAll() {
        var employeeController = new EmployeeController(repository);
        when(repository.findAll()).thenReturn(singletonList(new Employee("John Doe", "CEO")));

        var result = employeeController.all();

        assertThat(result.size() == 1);
        assertThat(result.get(0).getName().equals("John Doe"));
    }
}

