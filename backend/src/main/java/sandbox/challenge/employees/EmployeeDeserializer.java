package sandbox.challenge.employees;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sandbox.challenge.employees.domain.Employee;
import sandbox.challenge.employees.exception.ResourceNotFoundException;
import sandbox.challenge.employees.service.EmployeeService;

import java.io.IOException;

@Component
public class EmployeeDeserializer extends JsonDeserializer<Employee> {

    private EmployeeService employeeService;

    @Autowired
    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public Employee deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        var id = node.has("id") ? node.get("id").asLong() : null;
        var firstName = node.has("firstName") ? node.get("firstName").asText() : null;
        var lastName = node.has("lastName") ? node.get("lastName").asText() : null;
        var position = node.has("position") ? node.get("position").asText() : null;

        var employee = new Employee();
        employee.setId(id);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setPosition(position);

        if (node.has("supervisorId") && node.get("supervisorId") != null) {
            var supervisorIdStr = node.get("supervisorId").asText();
            if (supervisorIdStr != null && !supervisorIdStr.isEmpty()) {
                try {
                    var supervisorId = Long.parseLong(supervisorIdStr);
                    var supervisor = employeeService.getById(supervisorId).orElseThrow(() -> new ResourceNotFoundException("Supervisor not found"));
                    employee.setSupervisor(supervisor);
                } catch (NumberFormatException e) {
                    employee.setSupervisor(null);
                }
            }
        }

        return employee;
    }
}

