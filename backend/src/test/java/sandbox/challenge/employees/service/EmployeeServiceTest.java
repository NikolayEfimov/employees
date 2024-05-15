package sandbox.challenge.employees.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sandbox.challenge.employees.domain.Employee;
import sandbox.challenge.employees.repository.EmployeeRepository;

import java.util.Map;
import java.util.Optional;

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

    @Test
    void testUpdateEmployee() {
        var existingEmployee = new Employee();
        existingEmployee.setId(1L);
        existingEmployee.setFirstName("Steve");
        existingEmployee.setLastName("Jobs");
        existingEmployee.setPosition("CEO");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(existingEmployee)).thenReturn(existingEmployee);

        var updates = Map.of(
                "firstName", "Tim",
                "lastName", "Cook",
                "position", "CEO"
        );

        var updatedEmployee = employeeService.update(1L, updates).orElseThrow();

        assertThat(updatedEmployee.getFirstName()).isEqualTo("Tim");
        assertThat(updatedEmployee.getLastName()).isEqualTo("Cook");
        assertThat(updatedEmployee.getPosition()).isEqualTo("CEO");

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(updatedEmployee);
    }

    @Test
    void testUpdateEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        var employeeFieldsMap = Map.of("firstName", "Jane");
        var updatedEmployeeOpt = employeeService.update(1L, employeeFieldsMap);

        assertThat(updatedEmployeeOpt).isEmpty();

        verify(employeeRepository).findById(1L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void testUpdateEmployeeWithEmptySupervisorId() {
        var supervisor = new Employee();
        supervisor.setId(10L);
        supervisor.setFirstName("Dennis");
        supervisor.setLastName("Ritchie");
        supervisor.setPosition("Super Guru");

        var existingEmployee = new Employee();
        existingEmployee.setId(1L);
        existingEmployee.setFirstName("Robert");
        existingEmployee.setLastName("Martin");
        existingEmployee.setPosition("Guru");
        existingEmployee.setSupervisor(supervisor);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(existingEmployee)).thenReturn(existingEmployee);

        var fieldsMap = Map.of(
                "firstName", "Robert",
                "lastName", "Martin",
                "position", "Super Guru",
                "supervisorId", ""
        );

        var updatedEmployee = employeeService.update(1L, fieldsMap).orElseThrow();

        assertThat(updatedEmployee.getFirstName()).isEqualTo("Robert");
        assertThat(updatedEmployee.getLastName()).isEqualTo("Martin");
        assertThat(updatedEmployee.getPosition()).isEqualTo("Super Guru");
        assertThat(updatedEmployee.getSupervisor()).isNull();

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(updatedEmployee);
    }

}