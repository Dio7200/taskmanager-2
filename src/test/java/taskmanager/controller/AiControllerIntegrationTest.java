package taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import taskmanager.dto.SuggestRequestDto;
import taskmanager.dto.TaskResponseDto;
import taskmanager.model.Task;
import taskmanager.service.OpenAiService;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpenAiService openAiService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void suggestTask_shouldReturn200WithStructuredTask() throws Exception {
        TaskResponseDto mockResponse = TaskResponseDto.builder()
                .title("Submit Quarterly Report")
                .description("Submit the quarterly report before Friday.")
                .dueDate(LocalDate.of(2026, 5, 2))
                .priority(Task.Priority.HIGH)
                .status(Task.Status.TODO)
                .build();

        when(openAiService.suggestTask(anyString())).thenReturn(mockResponse);

        SuggestRequestDto request = new SuggestRequestDto(
                "remind me to submit the quarterly report before Friday"
        );

        mockMvc.perform(post("/tasks/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Submit Quarterly Report"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.dueDate").exists());
    }

    @Test
    void suggestTask_shouldReturn400_whenDescriptionIsBlank() throws Exception {
        SuggestRequestDto request = new SuggestRequestDto("");

        mockMvc.perform(post("/tasks/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}