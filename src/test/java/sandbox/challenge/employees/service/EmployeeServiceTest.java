package sandbox.challenge.employees.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sandbox.challenge.employees.domain.Employee;
import sandbox.challenge.employees.repository.EmployeeRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateEmployee() {
        var employee = new Employee();
        employee.setFirstName("Nikolai");
        employee.setLastName("Efimov");

        when(employeeRepository.save(employee)).thenReturn(employee);

        var createdEmployee = employeeService.create(employee);

        assertThat(createdEmployee).isNotNull();
        assertThat(createdEmployee.getFirstName()).isEqualTo("Nikolai");
        assertThat(createdEmployee.getLastName()).isEqualTo("Efimov");

        verify(employeeRepository).save(employee);
    }

}
