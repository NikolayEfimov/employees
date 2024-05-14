package sandbox.challenge.employees.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
}