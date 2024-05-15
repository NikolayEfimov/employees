package sandbox.challenge.employees.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sandbox.challenge.employees.service.EmployeeDeserializer;
import sandbox.challenge.employees.domain.Employee;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper(EmployeeDeserializer employeeDeserializer) {
        var mapper = new ObjectMapper();
        var module = new SimpleModule();
        module.addDeserializer(Employee.class, employeeDeserializer);
        mapper.registerModule(module);
        return mapper;
    }
}

