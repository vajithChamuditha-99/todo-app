package com.todo.backend.service;

import com.todo.backend.v1.dto.Pagination;
import com.todo.backend.v1.dto.Response;
import com.todo.backend.v1.dto.TaskDTO;
import com.todo.backend.v1.exceptions.ItemNotFoundException;
import com.todo.backend.v1.exceptions.RequiredFieldMissingException;
import com.todo.backend.v1.model.Task;
import com.todo.backend.v1.repository.TaskRepository;
import com.todo.backend.v1.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private TaskDTO validTaskDTO;
    private Task validTask;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.now();

        validTaskDTO = new TaskDTO();
        validTaskDTO.setTitle("Test Task");
        validTaskDTO.setDescription("Test Description");
        validTaskDTO.setCompleted(false);

        validTask = new Task();
        validTask.setId(1L);
        validTask.setTitle("Test Task");
        validTask.setDescription("Test Description");
        validTask.setCompleted(false);
        validTask.setCreatedAt(testDateTime);
    }

    @Test
    void createTask_WithValidTaskDTO_ShouldCreateTaskSuccessfully() {
        when(taskRepository.save(any(Task.class))).thenReturn(validTask);

        Response response = taskService.createTask(validTaskDTO);

        assertNotNull(response);
        assertEquals(0, response.getStatus());
        assertEquals("Task created successfully", response.getMessage());

        TaskDTO returnedDTO = (TaskDTO) response.getObject();
        assertEquals(1L, returnedDTO.getId());
        assertEquals("Test Task", returnedDTO.getTitle());
        assertEquals("Test Description", returnedDTO.getDescription());
        assertFalse(returnedDTO.isCompleted());

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());

        Task capturedTask = taskCaptor.getValue();
        assertEquals("Test Task", capturedTask.getTitle());
        assertEquals("Test Description", capturedTask.getDescription());
        assertFalse(capturedTask.isCompleted());
        assertNotNull(capturedTask.getCreatedAt());
    }

    @Test
    void createTask_WithNullTitle_ShouldThrowRequiredFieldMissingException() {
        validTaskDTO.setTitle(null);

        RequiredFieldMissingException exception = assertThrows(
                RequiredFieldMissingException.class,
                () -> taskService.createTask(validTaskDTO)
        );

        assertEquals("Title is a required field", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void createTask_WithEmptyTitle_ShouldThrowRequiredFieldMissingException() {
        validTaskDTO.setTitle("");

        RequiredFieldMissingException exception = assertThrows(
                RequiredFieldMissingException.class,
                () -> taskService.createTask(validTaskDTO)
        );

        assertEquals("Title is a required field", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void createTask_WithWhitespaceTitle_ShouldThrowRequiredFieldMissingException() {
        validTaskDTO.setTitle("   ");

        RequiredFieldMissingException exception = assertThrows(
                RequiredFieldMissingException.class,
                () -> taskService.createTask(validTaskDTO)
        );

        assertEquals("Title is a required field", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void createTask_WithNullDescription_ShouldCreateTaskSuccessfully() {
        validTaskDTO.setDescription(null);
        when(taskRepository.save(any(Task.class))).thenReturn(validTask);

        Response response = taskService.createTask(validTaskDTO);

        assertNotNull(response);
        assertEquals(0, response.getStatus());
        assertEquals("Task created successfully", response.getMessage());

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());

        Task capturedTask = taskCaptor.getValue();
        assertNull(capturedTask.getDescription());
    }

    @Test
    void updateTask_WithValidIdAndCompletedTrue_ShouldUpdateTaskSuccessfully() {
        Long taskId = 1L;
        validTaskDTO.setCompleted(true);

        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Existing Task");
        existingTask.setCompleted(false);

        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setTitle("Existing Task");
        updatedTask.setCompleted(true);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        Response response = taskService.updateTask(taskId, validTaskDTO);

        assertNotNull(response);
        assertEquals(0, response.getStatus());
        assertEquals("Task updated successfully", response.getMessage());

        TaskDTO returnedDTO = (TaskDTO) response.getObject();
        assertEquals(taskId, returnedDTO.getId());
        assertTrue(returnedDTO.isCompleted());

        verify(taskRepository).findById(taskId);
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());

        Task capturedTask = taskCaptor.getValue();
        assertTrue(capturedTask.isCompleted());
    }

    @Test
    void updateTask_WithValidIdAndCompletedFalse_ShouldUpdateTaskSuccessfully() {
        Long taskId = 1L;
        validTaskDTO.setCompleted(false);

        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Existing Task");
        existingTask.setCompleted(true);

        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setTitle("Existing Task");
        updatedTask.setCompleted(false);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        Response response = taskService.updateTask(taskId, validTaskDTO);

        assertNotNull(response);
        assertEquals(0, response.getStatus());
        assertEquals("Task updated successfully", response.getMessage());

        TaskDTO returnedDTO = (TaskDTO) response.getObject();
        assertEquals(taskId, returnedDTO.getId());
        assertFalse(returnedDTO.isCompleted());

        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_WithSameCompletedStatus_ShouldUpdateTaskSuccessfully() {
        Long taskId = 1L;
        validTaskDTO.setCompleted(false);

        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Existing Task");
        existingTask.setCompleted(false);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

        Response response = taskService.updateTask(taskId, validTaskDTO);

        assertNotNull(response);
        assertEquals(0, response.getStatus());
        assertEquals("Task updated successfully", response.getMessage());

        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_WithInvalidId_ShouldThrowItemNotFoundException() {
        Long invalidId = 999L;
        when(taskRepository.findById(invalidId)).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> taskService.updateTask(invalidId, validTaskDTO)
        );

        assertEquals("Task not found with id: " + invalidId, exception.getMessage());
        verify(taskRepository).findById(invalidId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void getTasks_WithCompletedTrue_ShouldReturnCompletedTasks() {
        boolean completed = true;
        int page = 0;
        int size = 5;

        Task task1 = createTaskWithId(1L, "Task 1", "Description 1", true);
        Task task2 = createTaskWithId(2L, "Task 2", "Description 2", true);
        List<Task> tasks = Arrays.asList(task1, task2);

        Page<Task> taskPage = new PageImpl<>(tasks, PageRequest.of(page, size), 10);
        when(taskRepository.findAllByCompleted(completed, PageRequest.of(page, size)))
                .thenReturn(taskPage);

        Response response = taskService.getTasks(completed, page, size);

        assertNotNull(response);
        assertEquals(0, response.getStatus());
        assertEquals("Tasks retrieved successfully", response.getMessage());

        @SuppressWarnings("unchecked")
        List<TaskDTO> returnedTasks = (List<TaskDTO>) response.getObject();
        assertEquals(2, returnedTasks.size());

        TaskDTO dto1 = returnedTasks.get(0);
        assertEquals(1L, dto1.getId());
        assertEquals("Task 1", dto1.getTitle());
        assertEquals("Description 1", dto1.getDescription());
        assertTrue(dto1.isCompleted());

        TaskDTO dto2 = returnedTasks.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals("Task 2", dto2.getTitle());
        assertEquals("Description 2", dto2.getDescription());
        assertTrue(dto2.isCompleted());

        Pagination pagination = response.getPagination();
        assertNotNull(pagination);
        assertEquals(10, pagination.getTotalElements());
        assertEquals(page, pagination.getCurrentPage());
        assertEquals(size, pagination.getPageSize());

        verify(taskRepository).findAllByCompleted(completed, PageRequest.of(page, size));
    }

    @Test
    void getTasks_WithCompletedFalse_ShouldReturnIncompleteTasks() {
        boolean completed = false;
        int page = 1;
        int size = 3;

        Task task1 = createTaskWithId(3L, "Task 3", "Description 3", false);
        List<Task> tasks = Arrays.asList(task1);

        Page<Task> taskPage = new PageImpl<>(tasks, PageRequest.of(page, size), 4);
        when(taskRepository.findAllByCompleted(completed, PageRequest.of(page, size)))
                .thenReturn(taskPage);

        Response response = taskService.getTasks(completed, page, size);

        assertNotNull(response);
        assertEquals(0, response.getStatus());
        assertEquals("Tasks retrieved successfully", response.getMessage());

        @SuppressWarnings("unchecked")
        List<TaskDTO> returnedTasks = (List<TaskDTO>) response.getObject();
        assertEquals(1, returnedTasks.size());

        TaskDTO dto = returnedTasks.get(0);
        assertEquals(3L, dto.getId());
        assertEquals("Task 3", dto.getTitle());
        assertEquals("Description 3", dto.getDescription());
        assertFalse(dto.isCompleted());

        Pagination pagination = response.getPagination();
        assertNotNull(pagination);
        assertEquals(4, pagination.getTotalElements());
        assertEquals(page, pagination.getCurrentPage());
        assertEquals(size, pagination.getPageSize());

        verify(taskRepository).findAllByCompleted(completed, PageRequest.of(page, size));
    }

    @Test
    void getTasks_WithEmptyResults_ShouldReturnEmptyList() {
        boolean completed = true;
        int page = 0;
        int size = 5;

        Page<Task> emptyTaskPage = new PageImpl<>(Arrays.asList(), PageRequest.of(page, size), 0);
        when(taskRepository.findAllByCompleted(completed, PageRequest.of(page, size)))
                .thenReturn(emptyTaskPage);

        Response response = taskService.getTasks(completed, page, size);

        assertNotNull(response);
        assertEquals(0, response.getStatus());
        assertEquals("Tasks retrieved successfully", response.getMessage());

        @SuppressWarnings("unchecked")
        List<TaskDTO> returnedTasks = (List<TaskDTO>) response.getObject();
        assertEquals(0, returnedTasks.size());

        Pagination pagination = response.getPagination();
        assertNotNull(pagination);
        assertEquals(0, pagination.getTotalElements());
        assertEquals(page, pagination.getCurrentPage());
        assertEquals(size, pagination.getPageSize());

        verify(taskRepository).findAllByCompleted(completed, PageRequest.of(page, size));
    }

    @Test
    void deleteTask_WithValidId_ShouldDeleteTaskSuccessfully() {
        Long taskId = 1L;
        when(taskRepository.existsById(taskId)).thenReturn(true);

        assertDoesNotThrow(() -> taskService.deleteTask(taskId));

        verify(taskRepository).existsById(taskId);
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void deleteTask_WithInvalidId_ShouldThrowItemNotFoundException() {
        Long invalidId = 999L;
        when(taskRepository.existsById(invalidId)).thenReturn(false);

        ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> taskService.deleteTask(invalidId)
        );

        assertEquals("Task not found with id: " + invalidId, exception.getMessage());
        verify(taskRepository).existsById(invalidId);
        verify(taskRepository, never()).deleteById(any(Long.class));
    }

    private Task createTaskWithId(Long id, String title, String description, boolean completed) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription(description);
        task.setCompleted(completed);
        task.setCreatedAt(testDateTime);
        return task;
    }
}