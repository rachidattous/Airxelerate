package com.axlab.Airxelerate.controller;

import com.axlab.Airxelerate.dto.UserDto;
import com.axlab.Airxelerate.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ){
        Sort sorting = Sort.by(
                Arrays.stream(sort.split(";"))
                        .map(s -> {
                            String[] parts = s.split(",");
                            Sort.Direction direction = (parts.length > 1 && parts[1].equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
                            return new Sort.Order(direction, parts[0]);
                        }).toList()
        );

        Page<UserDto> users = userService.findAll(PageRequest.of(page, size, sorting));
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
