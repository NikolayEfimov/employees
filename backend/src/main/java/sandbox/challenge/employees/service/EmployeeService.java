package sandbox.challenge.employees.service;

import org.springframework.stereotype.Service;
import sandbox.challenge.employees.domain.Employee;
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
        if (existingEmployee.isPresent()) {
            var employee = existingEmployee.get();
            if (employeeFieldsMap.containsKey("firstName")) {
                employee.setFirstName(employeeFieldsMap.get("firstName"));
            }
            if (employeeFieldsMap.containsKey("lastName")) {
                employee.setLastName(employeeFieldsMap.get("lastName"));
            }
            if (employeeFieldsMap.containsKey("position")) {
                employee.setPosition(employeeFieldsMap.get("position"));
            }
            if (employeeFieldsMap.containsKey("supervisorId")) {
                var supervisorId = Long.parseLong(employeeFieldsMap.get("supervisorId"));
                employee.setSupervisor(getById(supervisorId).orElse(null));
            }
            employeeRepository.save(employee);
            return Optional.of(employee);
        } else {
            return Optional.empty();
        }
    }

}