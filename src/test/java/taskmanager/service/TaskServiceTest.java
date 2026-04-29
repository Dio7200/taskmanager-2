package taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import taskmanager.dto.TaskRequestDto;
import taskmanager.dto.TaskResponseDto;
import taskmanager.model.Task;
import taskmanager.repository.TaskRepository;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;
    private TaskRequestDto sampleRequest;

    @BeforeEach
    void setUp() {
        sampleTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .dueDate(LocalDate.of(2026, 6, 1))
                .priority(Task.Priority.HIGH)
                .status(Task.Status.TODO)
                .build();

        sampleRequest = TaskRequestDto.builder()
                .title("Test Task")
                .description("Test Description")
                .dueDate(LocalDate.of(2026, 6, 1))
                .priority(Task.Priority.HIGH)
                .status(Task.Status.TODO)
                .build();
    }

    @Test
    void createTask_shouldReturnSavedTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponseDto result = taskService.createTask(sampleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Task");
        assertThat(result.getPriority()).isEqualTo(Task.Priority.HIGH);
        assertThat(result.getStatus()).isEqualTo(Task.Status.TODO);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void getAllTasks_shouldReturnListOfTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(sampleTask));

        List<TaskResponseDto> result = taskService.getAllTasks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Task");
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTaskById_shouldReturnTask_whenExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        TaskResponseDto result = taskService.getTaskById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Task");
    }

    @Test
    void getTaskById_shouldThrow_whenNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateTask_shouldReturnUpdatedTask() {
        TaskRequestDto updateRequest = TaskRequestDto.builder()
                .title("Updated Title")
                .description("Updated Description")
                .dueDate(LocalDate.of(2026, 7, 1))
                .priority(Task.Priority.LOW)
                .status(Task.Status.IN_PROGRESS)
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponseDto result = taskService.updateTask(1L, updateRequest);

        assertThat(result).isNotNull();
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_shouldThrow_whenNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(99L, sampleRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteTask_shouldDelete_whenExists() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        assertThatCode(() -> taskService.deleteTask(1L)).doesNotThrowAnyException();
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTask_shouldThrow_whenNotFound() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> taskService.deleteTask(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }
}