package com.clv.kanbanapp.service;

import com.clv.kanbanapp.dto.request.MoveTaskRequestBody;
import com.clv.kanbanapp.entity.Task;
import com.clv.kanbanapp.entity.TaskStatus;
import com.clv.kanbanapp.exception.ResourceNotFoundException;
import com.clv.kanbanapp.mapper.TaskMapper;
import com.clv.kanbanapp.repository.AppUserRepository;
import com.clv.kanbanapp.repository.TaskRepository;
import com.clv.kanbanapp.response.ServiceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    TaskMapper taskMapper;

    @Mock
    AppUserRepository appUserRepository;

    TaskService taskService;

    private List<Task> tasks;
    Task movedTask;


    @BeforeEach
    void setUp() {
        taskService = spy(new TaskService(taskRepository, taskMapper, appUserRepository));

        tasks = List.of(
                Task.builder()
                        .id(1L)
                        .title("Task 1")
                        .groupTask(false)
                        .position(4)
                        .build(),
                Task.builder()
                        .id(2L)
                        .groupTask(false)
                        .title("Task 2")
                        .position(3)
                        .build(),
                Task.builder()
                        .id(3L)
                        .groupTask(false)
                        .title("Task 3")
                        .position(2)
                        .build(),
                Task.builder()
                        .id(4L)
                        .groupTask(false)
                        .title("Task 4")
                        .position(1)
                        .build()
        );
    }


    @Test
    void testMoveTask_SameList() {

        movedTask = Task.builder()
                .id(1L)
                .title("Task 1")
                .groupTask(false)
                .position(1)
                .build();
        // Mocking SecurityContextHolder to return a mocked username
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@test.com");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mocking task repository methods
        when(taskRepository.findById(1L)).thenReturn(Optional.of(movedTask));
        List<Task> tasks = Arrays.asList(
                Task.builder().id(2L).position(1).title("Task 2").build(),
                Task.builder().id(3L).position(2).title("Task 3").build()
        );
        when(taskRepository.findPrivateTasks(TaskStatus.valueOf("TODO"), "user@test.com")).thenReturn(tasks);

        MoveTaskRequestBody request = new MoveTaskRequestBody(1L, 1, 0, "TODO", "TODO");

        // When
        ServiceResponse<?> response = taskService.moveTask(request);

        // Then
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).findPrivateTasks(TaskStatus.valueOf(request.getDestinationStatus()), "user@test.com");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("Task moved successfully", response.getMessage());

        // You can also verify that the internal method moveWithinSameList was called
        // In case you're using a spy on taskService, verify it
        // verify(taskService, times(1)).moveWithinSameList(movedTask, tasks, request.getSourceTaskPosition(), request.getDestinationTaskPosition());
    }

    @Test
    void testMoveTask_TaskNotFound() {
        // Mocking SecurityContextHolder to return a mocked username
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@test.com");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock taskRepository to throw ResourceNotFoundException
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        MoveTaskRequestBody request = new MoveTaskRequestBody(1L, 1, 0, "TODO", "IN_PROGRESS");

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.moveTask(request);
        });

        // Verify taskRepository was called
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testMoveTask_DifferentList() {
        movedTask = Task.builder()
                .id(1L)
                .title("Task 1")
                .groupTask(false)
                .position(1)
                .build();
        // Mocking SecurityContextHolder to return a mocked username
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@test.com");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mocking task repository methods
        when(taskRepository.findById(1L)).thenReturn(Optional.of(movedTask));
        List<Task> tasks = Arrays.asList(
                Task.builder().id(2L).position(1).title("Task 2").build(),
                Task.builder().id(3L).position(2).title("Task 3").build()
        );
        when(taskRepository.findPrivateTasks(TaskStatus.IN_PROGRESS, "user@test.com")).thenReturn(tasks);

        MoveTaskRequestBody request = new MoveTaskRequestBody(1L, 1, 0, "TODO", "IN_PROGRESS");

        // When
        ServiceResponse<?> response = taskService.moveTask(request);

        // Then
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).findPrivateTasks(TaskStatus.valueOf(request.getDestinationStatus()), "user@test.com");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("Task moved successfully", response.getMessage());

        // You can also verify that the internal method moveToDifferentList was called
        // In case you're using a spy on taskService, verify it
        // verify(taskService, times(1)).moveToDifferentList(movedTask, tasks, request);
    }


    @Test
    void testMoveToAnotherList_listEmpty(){
        // given
        List<Task> tasks = new ArrayList<>();
        movedTask = Task.builder()
                .id(1L)
                .title("Task 1")
                .groupTask(false)
                .position(1)
                .build();
        MoveTaskRequestBody request = new MoveTaskRequestBody(1L, 3, 0,
                "TODO", "IN_PROGRESS");


        // When
        taskService.moveToDifferentList(movedTask, tasks, request);

        verify(taskService, times(1)).moveToEmptyList(movedTask, request.getDestinationStatus());

    }

    @Test
    void testMoveToAnotherList_moveToEndOfList(){
        // given

        movedTask = Task.builder()
                .id(1L)
                .title("Task moved")
                .groupTask(false)
                .position(1)
                .build();
        MoveTaskRequestBody request = new MoveTaskRequestBody(1L, 0, 4,
                "TODO", "IN_PROGRESS");


        System.out.println(tasks.size());
        // When
        taskService.moveToDifferentList(movedTask, tasks, request);

        verify(taskService, times(1)).moveToEndOfList(movedTask, tasks,
                request.getDestinationStatus());

    }

    @Test
    void testMoveToDifferentList_middleOfList() {
        // Given
        movedTask = Task.builder()
                .id(1L)
                .title("Task moved")
                .groupTask(false)
                .status(TaskStatus.TODO)
                .position(1)
                .build();
        List<Task> tasks = List.of(
                Task.builder().id(2L).title("Task 2").position(0).build(),
                Task.builder().id(3L).title("Task 3").position(1).build(),
                Task.builder().id(4L).title("Task 4").position(2).build()
        );
        MoveTaskRequestBody request = new MoveTaskRequestBody(1L, 1, 1, "TODO", "IN_PROGRESS");

        // When
        taskService.moveToDifferentList(movedTask, tasks, request);

        // Then
        verify(taskService, times(1)).moveToMiddleOfList(movedTask, tasks, 1, "IN_PROGRESS");

    }

    @Test
    void testMoveInSameList_moveDown(){
        int sourcePosition = 0;
        int destinationPosition = 2;
        movedTask = tasks.get(0);

        // When
        taskService.moveWithinSameList(movedTask, tasks, sourcePosition, destinationPosition);

        // Then
        assertEquals(2, tasks.get(0).getPosition()); // moved Task
        assertEquals(4, tasks.get(1).getPosition());
        assertEquals(3, tasks.get(2).getPosition());
        assertEquals(1, tasks.get(3).getPosition());

        verify(taskRepository, times(1)).save(movedTask);
    }

    @Test
    void testMoveInSameList_moveUp(){
        int sourcePosition = 2;
        int destinationPosition = 0;
        movedTask = tasks.get(2);


        // When
        taskService.moveWithinSameList(movedTask, tasks, sourcePosition, destinationPosition);

        // Then
        assertEquals(3, tasks.get(0).getPosition()); // moved Task
        assertEquals(2, tasks.get(1).getPosition());
        assertEquals(4, tasks.get(2).getPosition());
        assertEquals(1, tasks.get(3).getPosition());

        verify(taskRepository, times(1)).save(movedTask);
    }

    @Test
    void testMoveUp(){
        int sourcePosition = 3;
        int destinationPosition = 1;

        // When
        taskService.moveUp(tasks, sourcePosition, destinationPosition);

        // Then
        assertEquals(4, tasks.get(0).getPosition());
        assertEquals(2, tasks.get(1).getPosition());
        assertEquals(1, tasks.get(2).getPosition());
        assertEquals(1, tasks.get(3).getPosition());
    }

    @Test
    void testMoveDown(){
        int sourcePosition = 0;
        int destinationPosition = 2;

        // When
        taskService.moveDown(tasks, sourcePosition, destinationPosition);

        // Then
        assertEquals(4, tasks.get(0).getPosition());
        assertEquals(4, tasks.get(1).getPosition());
        assertEquals(3, tasks.get(2).getPosition());
        assertEquals(1, tasks.get(3).getPosition());
    }
}