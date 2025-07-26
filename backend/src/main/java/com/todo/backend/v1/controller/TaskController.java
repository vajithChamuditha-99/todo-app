package com.todo.backend.v1.controller;

import com.todo.backend.v1.dto.Response;
import com.todo.backend.v1.dto.TaskDTO;
import com.todo.backend.v1.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Response> createTask(@RequestBody TaskDTO taskDTO) {
        try {
            Response createdTask = taskService.createTask(taskDTO);
            return ResponseEntity.ok().body(createdTask);
        } catch (Exception e) {
            log.error("Error creating task: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null, null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        try {
            Response updatedTask = taskService.updateTask(id, taskDTO);
            return ResponseEntity.ok().body(updatedTask);
        } catch (Exception e) {
            log.error("Error updating task with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null, null));
        }
    }

    @GetMapping
    public ResponseEntity<Response> getTasks(
            @RequestParam(defaultValue = "false") boolean completed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            Response response = taskService.getTasks(completed, page, size);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Error fetching tasks: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null, null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok().body(new Response(0, "Task deleted successfully", null, null));
        } catch (Exception e) {
            log.error("Error deleting task with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null, null));
        }
    }
}