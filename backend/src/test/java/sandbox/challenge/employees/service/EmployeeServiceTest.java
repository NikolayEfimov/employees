package sandbox.challenge.employees.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sandbox.challenge.employees.domain.Employee;
import sandbox.challenge.employees.exception.InfiniteRecursionException;
import sandbox.challenge.employees.exception.ResourceNotFoundException;
import sandbox.challenge.employees.exception.SupervisorHasSubordinatesException;
import sandbox.challenge.employees.repository.EmployeeRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    public void testDeleteWithInvalidId() {
        var employeeId = 1L;

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.delete(employeeId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found");

        verify(employeeRepository, never()).deleteById(any());
    }

    @Test
    public void testDeleteSupervisorWithSubordinates() {
        var supervisorId = 1L;
        var emp = new Employee();

        when(employeeRepository.findById(supervisorId)).thenReturn(Optional.of(emp));
        when(employeeRepository.findBySupervisorId(supervisorId)).thenReturn(List.of(new Employee()));

        assertThatThrownBy(() -> employeeService.delete(supervisorId))
                .isInstanceOf(SupervisorHasSubordinatesException.class)
                .hasMessage("Cannot delete supervisor with subordinates. Reassign or remove subordinates first.");

        verify(employeeRepository, never()).deleteById(any());
    }

    @Test
    public void testDeleteWithValidId() {
        var employeeId = 1L;
        var emp = new Employee();

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(emp));
        when(employeeRepository.findBySupervisorId(employeeId)).thenReturn(new ArrayList<>());

        employeeService.delete(employeeId);

        verify(employeeRepository).deleteById(employeeId);
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

    @Test
    void testCreateEmployeeHasToThrowExceptionWhenCycleIsCreated() {
        var employee1 = new Employee();
        employee1.setId(1L);
        employee1.setFirstName("Donald");
        employee1.setLastName("Knuth");
        employee1.setPosition("Scientist");

        var employee2 = new Employee();
        employee2.setId(2L);
        employee2.setFirstName("Larry");
        employee2.setLastName("Page");
        employee2.setPosition("Manager");
        employee2.setSupervisor(employee1);

        employee1.setSupervisor(employee2);

        when(employeeRepository.save(employee1)).thenReturn(employee1);

        assertThatThrownBy(() -> employeeService.create(employee1))
                .isInstanceOf(InfiniteRecursionException.class)
                .hasMessage("Cannot assign supervisor that creates a cycle");
    }

    @Test
    void testUpdateEmployeeHasToThrowExceptionWhenCycleIsCreated() {
        var employee1 = new Employee();
        employee1.setId(1L);
        employee1.setFirstName("Ken");
        employee1.setLastName("Thompson");
        employee1.setPosition("Developer");

        var employee2 = new Employee();
        employee2.setId(2L);
        employee2.setFirstName("Barbara");
        employee2.setLastName("Liskov");
        employee2.setPosition("Developer");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee2));
        when(employeeRepository.save(employee1)).thenReturn(employee1);

        employee2.setSupervisor(employee1);

        assertThatThrownBy(() -> employeeService.update(1L, Map.of("supervisorId", "2")))
                .isInstanceOf(InfiniteRecursionException.class)
                .hasMessage("Cannot assign supervisor that creates a cycle");
    }

    @Test
    void testAddSubordinateThrowsExceptionWhenSupervisorNotFound() {
        var supervisor = new Employee();
        var subordinate = new Employee();
        supervisor.setId(1L);
        subordinate.setId(2L);

        when(employeeRepository.findById(supervisor.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.addSubordinates(supervisor.getId(), List.of(subordinate.getId()));
        });
    }

    @Test
    void testAddSubordinateThrowsExceptionWhenSubordinateNotFound() {
        var supervisor = new Employee();
        var subordinate = new Employee();
        supervisor.setId(1L);
        subordinate.setId(2L);

        when(employeeRepository.findById(supervisor.getId())).thenReturn(Optional.of(supervisor));
        when(employeeRepository.findById(subordinate.getId())).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.addSubordinates(supervisor.getId(), List.of(subordinate.getId()))
        );
    }

    @Test
    void testAddSubordinateWithValidData() {
        var supervisor = new Employee();
        var subordinate = new Employee();
        supervisor.setId(1L);
        subordinate.setId(2L);

        when(employeeRepository.findById(supervisor.getId())).thenReturn(Optional.of(supervisor));
        when(employeeRepository.findById(subordinate.getId())).thenReturn(Optional.of(subordinate));
        when(employeeRepository.save(any(Employee.class))).thenReturn(subordinate);

        var actual = employeeService.addSubordinates(supervisor.getId(), List.of(subordinate.getId()));

        assertThat(actual).isEqualTo(supervisor);

    }

}
