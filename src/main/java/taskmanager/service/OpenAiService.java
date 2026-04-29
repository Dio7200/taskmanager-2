package taskmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import taskmanager.dto.TaskResponseDto;
import taskmanager.model.Task;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.model}")
    private String model;

    public TaskResponseDto suggestTask(String description) {
        String prompt = """
                You are a task management assistant. Convert the following plain-language description into a structured task.
                Respond ONLY with a valid JSON object — no explanation, no markdown, no code blocks.
                The JSON must have exactly these fields:
                - title (string, concise)
                - description (string, detailed)
                - dueDate (string, format YYYY-MM-DD, infer from context or use %s if unclear)
                - priority (string, one of: LOW, MEDIUM, HIGH)
                - status (string, always TODO for new tasks)
                
                User description: %s
                """.formatted(LocalDate.now().plusDays(7), description);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.3
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            String content = root.path("choices").get(0).path("message").path("content").asText();

            // Strip markdown code blocks if model wraps response in them
            content = content.replaceAll("```json", "").replaceAll("```", "").trim();

            JsonNode taskNode = objectMapper.readTree(content);

            return TaskResponseDto.builder()
                    .title(taskNode.path("title").asText())
                    .description(taskNode.path("description").asText())
                    .dueDate(LocalDate.parse(taskNode.path("dueDate").asText()))
                    .priority(Task.Priority.valueOf(taskNode.path("priority").asText().toUpperCase()))
                    .status(Task.Status.valueOf(taskNode.path("status").asText().toUpperCase()))
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OpenAI response: " + e.getMessage(), e);
        }
    }
}