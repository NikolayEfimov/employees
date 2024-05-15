package sandbox.challenge.employees.service;

import org.springframework.stereotype.Service;
import sandbox.challenge.employees.domain.Employee;
import sandbox.challenge.employees.repository.EmployeeRepository;

import java.util.List;
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

}
