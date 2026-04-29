package taskmanager.dto;

import taskmanager.model.Task;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private LocalDate dueDate;

    private Task.Priority priority;

    private Task.Status status;
}