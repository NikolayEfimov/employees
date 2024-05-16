package sandbox.challenge.employees.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sandbox.challenge.employees.domain.Employee;
import sandbox.challenge.employees.service.EmployeeService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.create(employee));
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAll() {
        return ResponseEntity.ok(employeeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(@PathVariable Long id) {
        var employee = employeeService.getById(id);
        return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (employeeService.getById(id).isPresent()) {
            employeeService.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Employee> update(@PathVariable Long id, @RequestBody Map<String, String> employeeFieldsMap) {
        var employee = employeeService.update(id, employeeFieldsMap);
        return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{supervisorId}/add-subordinates")
    public ResponseEntity<Employee> addSubordinates(@PathVariable Long supervisorId, @RequestBody List<Long> subordinateIds) {
        return ResponseEntity.ok(employeeService.addSubordinates(supervisorId, subordinateIds));
    }

}

