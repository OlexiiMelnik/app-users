package app.users.service;

import app.users.dto.UserRegistrationRequestDto;
import app.users.dto.UserRequestUpdateDto;
import app.users.dto.UserResponseDto;
import app.users.exeption.RegistrationException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException;

    UserResponseDto update(String userEmail, UserRequestUpdateDto updateDto);

    List<UserResponseDto> findUsersByBirthDateRange(
            LocalDate fromDate, LocalDate toDate, Pageable pageable);

    void deleteById(Long id);
}
