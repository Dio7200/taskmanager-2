package taskmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskmanager.dto.SuggestRequestDto;
import taskmanager.dto.TaskResponseDto;
import taskmanager.service.OpenAiService;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AiController {

    private final OpenAiService openAiService;

    @PostMapping("/suggest")
    public ResponseEntity<TaskResponseDto> suggestTask(@Valid @RequestBody SuggestRequestDto request) {
        TaskResponseDto suggested = openAiService.suggestTask(request.getDescription());
        return ResponseEntity.ok(suggested);
    }
}