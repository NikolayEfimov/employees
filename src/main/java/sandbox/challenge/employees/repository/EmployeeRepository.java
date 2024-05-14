package sandbox.challenge.employees.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sandbox.challenge.employees.domain.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}

