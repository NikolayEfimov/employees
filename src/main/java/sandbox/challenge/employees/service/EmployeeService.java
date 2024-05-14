package sandbox.challenge.employees.service;

import org.springframework.stereotype.Service;
import sandbox.challenge.employees.domain.Employee;
import sandbox.challenge.employees.repository.EmployeeRepository;

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

}
