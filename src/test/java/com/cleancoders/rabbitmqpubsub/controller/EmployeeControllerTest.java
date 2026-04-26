package com.cleancoders.rabbitmqpubsub.controller;

import com.cleancoders.rabbitmqpubsub.dto.Employee;
import com.cleancoders.rabbitmqpubsub.dto.EmployeeCreatedEvent;
import com.cleancoders.rabbitmqpubsub.publisher.EmployeeEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Slice test for {@link EmployeeController}.
 *
 * <p>Uses {@code @WebMvcTest} so only the web layer is loaded; the publisher is
 * replaced with a Mockito mock to avoid any AMQP infrastructure.
 */
@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(WRITE_DATES_AS_TIMESTAMPS);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeEventPublisher publisher;

    @Test
    void createEmployee_returns201WithBody() throws Exception {
        Employee employee = new Employee("emp-001", "Alice Smith", "alice@example.com", "Engineering");

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(employee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("emp-001"))
                .andExpect(jsonPath("$.name").value("Alice Smith"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.department").value("Engineering"));
    }

    @Test
    void createEmployee_publishesEmployeeCreatedEvent() throws Exception {
        Employee employee = new Employee("emp-002", "Bob Jones", "bob@example.com", "Finance");

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(employee)))
                .andExpect(status().isCreated());

        ArgumentCaptor<EmployeeCreatedEvent> captor = ArgumentCaptor.forClass(EmployeeCreatedEvent.class);
        verify(publisher).publishEmployeeCreated(captor.capture());

        EmployeeCreatedEvent event = captor.getValue();
        assertThat(event.eventType()).isEqualTo("EMPLOYEE_CREATED");
        assertThat(event.employeeId()).isEqualTo("emp-002");
        assertThat(event.name()).isEqualTo("Bob Jones");
        assertThat(event.email()).isEqualTo("bob@example.com");
        assertThat(event.department()).isEqualTo("Finance");
        assertThat(event.eventId()).isNotBlank();
        assertThat(event.occurredAt()).isNotNull();
    }

    @Test
    void createEmployee_returns400WhenEmailIsInvalid() throws Exception {
        String badPayload = """
                {
                  "id": "emp-003",
                  "name": "Charlie",
                  "email": "not-an-email",
                  "department": "IT"
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEmployee_returns400WhenNameIsBlank() throws Exception {
        String badPayload = """
                {
                  "id": "emp-004",
                  "name": "",
                  "email": "dave@example.com",
                  "department": "Operations"
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badPayload))
                .andExpect(status().isBadRequest());
    }
}
