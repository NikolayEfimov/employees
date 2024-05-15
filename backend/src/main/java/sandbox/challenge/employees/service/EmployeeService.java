package sandbox.challenge.employees.service;

import org.springframework.stereotype.Service;
import sandbox.challenge.employees.domain.Employee;
import sandbox.challenge.employees.exception.InfiniteRecursionException;
import sandbox.challenge.employees.exception.ResourceNotFoundException;
import sandbox.challenge.employees.repository.EmployeeRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.time.LocalDateTime.now;


@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee create(Employee employee) {
        validateSupervisor(employee);
        employee.setCreationDate(now());
        return employeeRepository.save(employee);
    }

    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getById(Long id) {
        return employeeRepository.findById(id);
    }

    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }

    public Optional<Employee> update(Long id, Map<String, String> employeeFieldsMap) {
        var existingEmployee = employeeRepository.findById(id);

        if (existingEmployee.isEmpty()) {
            return Optional.empty();
        }

        var employee = existingEmployee.get();
        updateFirstName(employeeFieldsMap, employee);
        updateLastName(employeeFieldsMap, employee);
        updatePosition(employeeFieldsMap, employee);
        updateSupervisor(employeeFieldsMap, employee);

        employeeRepository.save(employee);

        return Optional.of(employee);
    }

    private void updateFirstName(Map<String, String> fields, Employee employee) {
        if (fields.containsKey("firstName")) {
            employee.setFirstName(fields.get("firstName"));
        }
    }

    private void updateLastName(Map<String, String> fields, Employee employee) {
        if (fields.containsKey("lastName")) {
            employee.setLastName(fields.get("lastName"));
        }
    }

    private void updatePosition(Map<String, String> fields, Employee employee) {
        if (fields.containsKey("position")) {
            employee.setPosition(fields.get("position"));
        }
    }

    private void updateSupervisor(Map<String, String> fields, Employee employee) {
        if (fields.containsKey("supervisorId")) {
            var supervisorIdStr = fields.get("supervisorId");
            if (supervisorIdStr != null && !supervisorIdStr.isEmpty()) {
                try {
                    var supervisorId = Long.parseLong(supervisorIdStr);
                    var supervisor = getById(supervisorId).orElseThrow(() -> new ResourceNotFoundException("Supervisor not found"));
                    employee.setSupervisor(supervisor);
                    validateSupervisor(employee);
                } catch (NumberFormatException e) {
                    throw new ResourceNotFoundException("Invalid supervisor ID format");
                }
            } else {
                employee.setSupervisor(null);
            }
        }
    }

    private void validateSupervisor(Employee employee) {
        Employee supervisor = employee.getSupervisor();
        while (supervisor != null) {
            if (supervisor.getId().equals(employee.getId())) {
                throw new InfiniteRecursionException("Cannot assign supervisor that creates a cycle");
            }
            supervisor = supervisor.getSupervisor();
        }
    }

}
