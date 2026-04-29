package taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import taskmanager.dto.TaskRequestDto;
import taskmanager.model.Task;
import taskmanager.repository.TaskRepository;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void createTask_shouldReturn201WithTask() throws Exception {
        TaskRequestDto request = TaskRequestDto.builder()
                .title("Integration Test Task")
                .description("Testing the full stack")
                .dueDate(LocalDate.of(2026, 6, 1))
                .priority(Task.Priority.HIGH)
                .status(Task.Status.TODO)
                .build();

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Integration Test Task"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    void getAllTasks_shouldReturnList() throws Exception {
        TaskRequestDto request = TaskRequestDto.builder()
                .title("Sample Task")
                .priority(Task.Priority.MEDIUM)
                .status(Task.Status.TODO)
                .build();

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void getTaskById_shouldReturnTask() throws Exception {
        TaskRequestDto request = TaskRequestDto.builder()
                .title("Find Me Task")
                .priority(Task.Priority.LOW)
                .status(Task.Status.TODO)
                .build();

        String response = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/tasks/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Find Me Task"));
    }

    @Test
    void updateTask_shouldReturnUpdatedTask() throws Exception {
        TaskRequestDto createRequest = TaskRequestDto.builder()
                .title("Original Title")
                .priority(Task.Priority.LOW)
                .status(Task.Status.TODO)
                .build();

        String response = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        TaskRequestDto updateRequest = TaskRequestDto.builder()
                .title("Updated Title")
                .priority(Task.Priority.HIGH)
                .status(Task.Status.IN_PROGRESS)
                .build();

        mockMvc.perform(put("/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void deleteTask_shouldReturn204() throws Exception {
        TaskRequestDto request = TaskRequestDto.builder()
                .title("Delete Me Task")
                .priority(Task.Priority.LOW)
                .status(Task.Status.TODO)
                .build();

        String response = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/tasks/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/tasks/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTask_shouldReturn400_whenTitleMissing() throws Exception {
        TaskRequestDto request = TaskRequestDto.builder()
                .description("No title here")
                .priority(Task.Priority.LOW)
                .status(Task.Status.TODO)
                .build();

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}