package com.todo.backend.v1.service;

import com.todo.backend.v1.dto.Pagination;
import com.todo.backend.v1.dto.Response;
import com.todo.backend.v1.dto.TaskDTO;
import com.todo.backend.v1.exceptions.ItemNotFoundException;
import com.todo.backend.v1.exceptions.RequiredFieldMissingException;
import com.todo.backend.v1.model.Task;
import com.todo.backend.v1.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Response createTask(TaskDTO taskDTO) {
        log.info("Creating task with title: {}", taskDTO.getTitle());
        if (taskDTO.getTitle() == null || taskDTO.getTitle().trim().isEmpty()) {
            throw new RequiredFieldMissingException("Title is a required field");
        }
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        task = taskRepository.save(task);
        taskDTO.setId(task.getId());

        log.info("Task created successfully with title: {}", task.getTitle());
        return new Response(0, "Task created successfully", taskDTO, null);
    }

    public Response updateTask(Long id, TaskDTO taskDTO) {
        log.info("Updating task with id: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Task not found with id: " + id));

        if (taskDTO.isCompleted() != task.isCompleted()) {
            task.setCompleted(taskDTO.isCompleted());
        }
        task = taskRepository.save(task);
        taskDTO.setId(task.getId());

        log.info("Task updated successfully with id: {}", task.getId());
        return new Response(0, "Task updated successfully", taskDTO, null);
    }

    public Response getTasks(boolean completed, int page, int size) {
        log.info("Retrieving tasks with completed status: {}, page: {}, size: {}", completed, page, size);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Task> taskPage = taskRepository.findAllByCompleted(completed, pageRequest);

        List<TaskDTO> taskDTOs = taskPage.getContent().stream().map(task -> {
            TaskDTO dto = new TaskDTO();
            dto.setId(task.getId());
            dto.setTitle(task.getTitle());
            dto.setDescription(task.getDescription());
            dto.setCompleted(task.isCompleted());
            dto.setCreatedAt(task.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());

        Pagination pagination = new Pagination((int) taskPage.getTotalElements(), page, size);
        return new Response(0, "Tasks retrieved successfully", taskDTOs, pagination);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ItemNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }
}