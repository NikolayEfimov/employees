package sandbox.challenge.employees.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sandbox.challenge.employees.domain.Employee;
import sandbox.challenge.employees.repository.EmployeeRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EmployeeControllerTest {

    private static final long NON_EXISTING_EMPLOYEE_ID = 333L;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void testCreateEmployee() throws Exception {
        var employeeJson = """
                {
                    "firstName": "Nikolai",
                    "lastName": "Efimov",
                    "position": "Senior Software Engineer"
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(APPLICATION_JSON)
                        .content(employeeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Nikolai"))
                .andExpect(jsonPath("$.lastName").value("Efimov"))
                .andExpect(jsonPath("$.position").value("Senior Software Engineer"));
    }

    @Test
    void testCreateEmployeeInvalidData() throws Exception {
        var invalidEmployeeJson = """
                {
                    "firstName": "Elon",
                    "position": "CEO"
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(APPLICATION_JSON)
                        .content(invalidEmployeeJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.messages.lastName").value("Last name is required"));
    }

    @Test
    void testCreateEmployeeMultipleInvalidFields() throws Exception {
        var invalidEmployeeJson = """
                {
                    "firstName": "",
                    "lastName": "",
                    "position": ""
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(APPLICATION_JSON)
                        .content(invalidEmployeeJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.messages.firstName").value("First name is required"))
                .andExpect(jsonPath("$.messages.lastName").value("Last name is required"))
                .andExpect(jsonPath("$.messages.position").value("Position is required"));
    }

    @Test
    void testGetAllEmployees() throws Exception {
        assertThat(employeeRepository.findAll()).isEmpty();

        var employee = new Employee();
        employee.setFirstName("Nikolai");
        employee.setLastName("Efimov");
        employee.setPosition("Senior Software Engineer");
        employeeRepository.save(employee);

        assertThat(employeeRepository.findAll()).hasSize(1);

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Nikolai"))
                .andExpect(jsonPath("$[0].lastName").value("Efimov"))
                .andExpect(jsonPath("$[0].position").value("Senior Software Engineer"));
    }

    @Test
    void testGetAllEmployeesWithEmptyRepository() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetEmployeeById() throws Exception {
        var employee = new Employee();
        employee.setFirstName("Elon");
        employee.setLastName("Musk");
        employee.setPosition("CEO");
        var savedEmployee = employeeRepository.save(employee);

        mockMvc.perform(get("/api/employees/" + savedEmployee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Elon"))
                .andExpect(jsonPath("$.lastName").value("Musk"))
                .andExpect(jsonPath("$.position").value("CEO"));
    }

    @Test
    void testGetEmployeeByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/employees/{id}", NON_EXISTING_EMPLOYEE_ID)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteEmployee() throws Exception {
        var employee = new Employee();
        employee.setFirstName("Sam");
        employee.setLastName("Bankman-Fried");
        employee.setPosition("CEO");
        var savedEmployee = employeeRepository.save(employee);
        var employeeId = savedEmployee.getId();

        assertThat(employeeRepository.findById(employeeId)).isPresent();

        mockMvc.perform(delete("/api/employees/{id}", employeeId))
                .andExpect(status().isNoContent());

        assertThat(employeeRepository.findById(employeeId)).isNotPresent();
    }

    @Test
    void testDeleteEmployeeNotFound() throws Exception {
        mockMvc.perform(delete("/api/employees/{id}", NON_EXISTING_EMPLOYEE_ID)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateEmployee() throws Exception {
        var employee = new Employee();
        employee.setFirstName("Nikolai");
        employee.setLastName("Efimov");
        employee.setPosition("Senior Software Engineer");
        var savedEmployee = employeeRepository.save(employee);

        var updatedEmployeeJson = """
                {
                    "position": "Senior Software Developer"
                }
                """;

        var employeeId = savedEmployee.getId();
        mockMvc.perform(patch("/api/employees/{id}", employeeId)
                        .contentType(APPLICATION_JSON)
                        .content(updatedEmployeeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Nikolai"))
                .andExpect(jsonPath("$.lastName").value("Efimov"))
                .andExpect(jsonPath("$.position").value("Senior Software Developer"));

        var updatedEmployee = employeeRepository.findById(employeeId).orElse(null);

        assertThat(updatedEmployee).isNotNull();
        assertThat(updatedEmployee.getFirstName()).isEqualTo("Nikolai");
        assertThat(updatedEmployee.getLastName()).isEqualTo("Efimov");
        assertThat(updatedEmployee.getPosition()).isEqualTo("Senior Software Developer");
    }

    @Test
    void testUpdateEmployeeNotFound() throws Exception {
        var updatedEmployeeJson = """
                {
                    "firstName": "Luke",
                    "lastName": "Skywalker",
                    "position": "Product Owner"
                }
                """;

        mockMvc.perform(patch("/api/employees/{id}", NON_EXISTING_EMPLOYEE_ID)
                        .contentType(APPLICATION_JSON)
                        .content(updatedEmployeeJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @Disabled("flaky test - deeper investigation required")
    @DirtiesContext
    void testCreateEmployeeWithCycleThrowsException() throws Exception {
        var employee1 = """
                {
                    "firstName": "Nikolai",
                    "lastName": "Efimov",
                    "position": "Senior Software Engineer"
                }
                """;

        var employee2 = """
                {
                    "firstName": "Maksim",
                    "lastName": "Lazarev",
                    "position": "Team Lead"
                }
                """;

        var allowedSupervisorId = """
                {
                    "supervisorId": "2"
                }
                """;

        var willCreateCycle = """
                {
                    "supervisorId": "1"
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(APPLICATION_JSON)
                        .content(employee1))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/employees")
                        .contentType(APPLICATION_JSON)
                        .content(employee2))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/employees/{id}", 1)
                        .contentType(APPLICATION_JSON)
                        .content(allowedSupervisorId))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/employees/{id}", 2)
                        .contentType(APPLICATION_JSON)
                        .content(willCreateCycle))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("Cannot assign supervisor that creates a cycle"));
    }

    @Test
    @Disabled("flaky test - deeper investigation required")
    @DirtiesContext
    void testDeleteSupervisorWithSubordinatesThrowsException() throws Exception {
        var supervisorJson = """
                {
                    "firstName": "Nikolai",
                    "lastName": "Efimov",
                    "position": "Developer"
                }
                """;

        var employeeJson = """
                {
                    "firstName": "Javid",
                    "lastName": "Aliev",
                    "position": "Developer",
                    "supervisorId": "1"
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(APPLICATION_JSON)
                        .content(supervisorJson))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/employees")
                        .contentType(APPLICATION_JSON)
                        .content(employeeJson))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/employees/{id}", 1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Operation not allowed"))
                .andExpect(jsonPath("$.message").value("Cannot delete supervisor with subordinates. Reassign or remove subordinates first."));
    }

    @Test
    void testAddSubordinatesSuccess() throws Exception {
        var supervisor = new Employee();
        supervisor.setFirstName("Nikolai");
        supervisor.setLastName("Efimov");
        supervisor.setPosition("Senior Developer");
        var savedSupervisor = employeeRepository.save(supervisor);

        var subordinate1 = new Employee();
        subordinate1.setFirstName("Maksim");
        subordinate1.setLastName("Lazarev");
        subordinate1.setPosition("Developer");
        var savedSubordinate1 = employeeRepository.save(subordinate1);

        var subordinate2 = new Employee();
        subordinate2.setFirstName("Javid");
        subordinate2.setLastName("Aliev");
        subordinate2.setPosition("Developer");
        var savedSubordinate2 = employeeRepository.save(subordinate2);

        var subordinatesJson = String.format("[%d, %d]", savedSubordinate1.getId(), savedSubordinate2.getId());

        mockMvc.perform(post("/api/employees/{supervisorId}/add-subordinates", savedSupervisor.getId())
                        .contentType(APPLICATION_JSON)
                        .content(subordinatesJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedSupervisor.getId()))
                .andExpect(jsonPath("$.firstName").value("Nikolai"))
                .andExpect(jsonPath("$.lastName").value("Efimov"))
                .andExpect(jsonPath("$.position").value("Senior Developer"));

        var updatedSubordinate1 = employeeRepository.findById(savedSubordinate1.getId()).orElse(null);
        var updatedSubordinate2 = employeeRepository.findById(savedSubordinate2.getId()).orElse(null);

        assertThat(updatedSubordinate1).isNotNull();
        assertThat(updatedSubordinate1.getSupervisor().getId()).isEqualTo(savedSupervisor.getId());

        assertThat(updatedSubordinate2).isNotNull();
        assertThat(updatedSubordinate2.getSupervisor().getId()).isEqualTo(savedSupervisor.getId());
    }

    @Test
    void testAddSubordinatesWithInvalidSupervisor() throws Exception {
        var subordinateJson = "[1, 2]";

        mockMvc.perform(post("/api/employees/{supervisorId}/add-subordinates", NON_EXISTING_EMPLOYEE_ID)
                        .contentType(APPLICATION_JSON)
                        .content(subordinateJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddSubordinatesWithInvalidSubordinates() throws Exception {
        var supervisor = new Employee();
        supervisor.setFirstName("Nikolai");
        supervisor.setLastName("Efimov");
        supervisor.setPosition("Senior Developer");
        var savedSupervisor = employeeRepository.save(supervisor);

        var invalidSubordinateJson = "[222, 333]";

        mockMvc.perform(post("/api/employees/{supervisorId}/add-subordinates", savedSupervisor.getId())
                        .contentType(APPLICATION_JSON)
                        .content(invalidSubordinateJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.message").value("Subordinate not found"));
    }

    @Test
    void testAddSubordinatesWithEmptyListOfSubordinates() throws Exception {
        var supervisor = new Employee();
        supervisor.setFirstName("Nikolai");
        supervisor.setLastName("Efimov");
        supervisor.setPosition("Senior Developer");
        var savedSupervisor = employeeRepository.save(supervisor);

        var emptySubordinateJson = "[]";

        mockMvc.perform(post("/api/employees/{supervisorId}/add-subordinates", savedSupervisor.getId())
                        .contentType(APPLICATION_JSON)
                        .content(emptySubordinateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedSupervisor.getId()))
                .andExpect(jsonPath("$.firstName").value("Nikolai"))
                .andExpect(jsonPath("$.lastName").value("Efimov"))
                .andExpect(jsonPath("$.position").value("Senior Developer"));
    }

}