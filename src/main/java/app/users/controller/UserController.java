package app.users.controller;

import app.users.dto.UserRequestUpdateDto;
import app.users.dto.UserResponseDto;
import app.users.model.User;
import app.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoints for managing users")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Update the user",
            description = "Update the user by email into DB")
    @PutMapping()
    public UserResponseDto update(Authentication authentication,
                                  @RequestBody @Valid UserRequestUpdateDto userRequestUpdateDto) {
        User user = (User) authentication.getPrincipal();
        return userService.update(user.getEmail(), userRequestUpdateDto);
    }

    @Operation(summary = "Delete a user",
            description = "Delete a user by id into DB")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    void delete(@PathVariable Long id) {
        userService.deleteById(id);
    }

    @Operation(summary = "Get all users by date",
            description = "Search all users in DB by date")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public List<UserResponseDto> findUsersByBirthDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Pageable pageable) {
        return userService.findUsersByBirthDateRange(fromDate, toDate, pageable);
    }
}
