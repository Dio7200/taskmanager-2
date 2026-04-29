package taskmanager.service;

import taskmanager.dto.TaskRequestDto;
import taskmanager.dto.TaskResponseDto;
import taskmanager.model.Task;
import taskmanager.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskResponseDto createTask(TaskRequestDto request) {
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .priority(request.getPriority() != null ? request.getPriority() : Task.Priority.MEDIUM)
                .status(request.getStatus() != null ? request.getStatus() : Task.Status.TODO)
                .build();
        return TaskResponseDto.fromEntity(taskRepository.save(task));
    }

    public List<TaskResponseDto> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(TaskResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));
        return TaskResponseDto.fromEntity(task);
    }

    public TaskResponseDto updateTask(Long id, TaskRequestDto request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        return TaskResponseDto.fromEntity(taskRepository.save(task));
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }
}