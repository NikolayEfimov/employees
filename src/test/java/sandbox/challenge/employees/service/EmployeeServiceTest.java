package sandbox.challenge.employees.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sandbox.challenge.employees.domain.Employee;
import sandbox.challenge.employees.repository.EmployeeRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void testGetAllEmployees() {
        employeeService.getAll();

        verify(employeeRepository).findAll();
    }

    @Test
    void testGetEmployeeById() {
        var employee = new Employee();
        employee.setId(1L);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        var foundEmployee = employeeService.getById(1L);
        assertThat(foundEmployee).isPresent();
        assertThat(foundEmployee.get().getId()).isEqualTo(1L);
    }

    @Test
    void testGetEmployeeByIdWhenEmployeeNotFound() {
        when(employeeRepository.findById(333L)).thenReturn(Optional.empty());

        var employee = employeeService.getById(333L);

        assertThat(employee).isEmpty();
    }

    @Test
    void testDeleteEmployee() {
        employeeService.delete(1L);

        verify(employeeRepository).deleteById(1L);
    }

}
