package taskmanager.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import taskmanager.dto.TaskResponseDto;
import taskmanager.model.Task;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenAiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OpenAiService openAiService;

    private static final String MOCK_RESPONSE = """
            {
              "choices": [
                {
                  "message": {
                    "content": "{\\"title\\":\\"Submit Quarterly Report\\",\\"description\\":\\"Submit the quarterly report before Friday.\\",\\"dueDate\\":\\"2026-05-02\\",\\"priority\\":\\"HIGH\\",\\"status\\":\\"TODO\\"}"
                  }
                }
              ]
            }
            """;

    @Test
    void suggestTask_shouldReturnStructuredTask() {
        ReflectionTestUtils.setField(openAiService, "apiKey", "test-key");
        ReflectionTestUtils.setField(openAiService, "apiUrl", "https://api.openai.com/v1/chat/completions");
        ReflectionTestUtils.setField(openAiService, "model", "gpt-4o-mini");

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(MOCK_RESPONSE, HttpStatus.OK));

        TaskResponseDto result = openAiService.suggestTask("remind me to submit the quarterly report before Friday");

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Submit Quarterly Report");
        assertThat(result.getPriority()).isEqualTo(Task.Priority.HIGH);
        assertThat(result.getStatus()).isEqualTo(Task.Status.TODO);
        assertThat(result.getDueDate()).isNotNull();

        verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void suggestTask_shouldThrow_whenResponseIsInvalid() {
        ReflectionTestUtils.setField(openAiService, "apiKey", "test-key");
        ReflectionTestUtils.setField(openAiService, "apiUrl", "https://api.openai.com/v1/chat/completions");
        ReflectionTestUtils.setField(openAiService, "model", "gpt-4o-mini");

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("invalid json", HttpStatus.OK));

        assertThatThrownBy(() -> openAiService.suggestTask("some description"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to parse OpenAI response");
    }
}