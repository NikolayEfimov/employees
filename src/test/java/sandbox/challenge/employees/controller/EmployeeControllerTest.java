package sandbox.challenge.employees.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sandbox.challenge.employees.domain.Employee;
import sandbox.challenge.employees.repository.EmployeeRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EmployeeControllerTest {

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
}