package app.users.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import app.users.dto.UserRegistrationRequestDto;
import app.users.dto.UserRequestUpdateDto;
import app.users.dto.UserResponseDto;
import app.users.exeption.RegistrationException;
import app.users.mapper.UserMapper;
import app.users.model.Role;
import app.users.model.User;
import app.users.repository.RoleRepository;
import app.users.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Verify save() method works")
    void save_ValidUserRegistrationRequestDto_ReturnsUserResponseDto()
            throws RegistrationException {
        // Given
        UserRegistrationRequestDto registrationRequestDto = getValidUserRegistrationRequestDto();
        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setRoleName(Role.RoleName.USER);
        User user = getUserFromRegistrationRequestDto(registrationRequestDto, userRole);
        UserResponseDto expectedResponseDto = getUserResponseDtoFromUser(user);

        Mockito.when(roleRepository.findRoleByRoleName(Role.RoleName.USER))
                .thenReturn(Optional.of(userRole));
        Mockito.when(passwordEncoder.encode(registrationRequestDto.getPassword()))
                .thenReturn("encodedPassword");
        Mockito.when(userRepository.findByEmail(registrationRequestDto.getEmail()))
                .thenReturn(Optional.empty());
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);
        Mockito.when(userMapper.toDto(user)).thenReturn(expectedResponseDto);

        // When
        UserResponseDto actualResponseDto = userService.register(registrationRequestDto);

        // Then
        assertThat(actualResponseDto).isEqualTo(expectedResponseDto);
        verify(roleRepository).findRoleByRoleName(Role.RoleName.USER);
        verify(userRepository).findByEmail(registrationRequestDto.getEmail());
        verify(userMapper).toDto(user);
    }

    @Test
    @DisplayName("Verify RegistrationException is thrown when user with email already exists")
    void register_UserWithEmailAlreadyExists_ThrowsRegistrationException() {
        // Given
        UserRegistrationRequestDto registrationRequestDto = getValidUserRegistrationRequestDto();

        Mockito.when(userRepository.findByEmail(registrationRequestDto.getEmail()))
                .thenReturn(Optional.of(new User()));

        // When and Then
        assertThrows(RegistrationException.class,
                () -> userService.register(registrationRequestDto));
        verify(userRepository).findByEmail(registrationRequestDto.getEmail());
    }

    @Test
    @DisplayName("Verify update() method works")
    void update_ValidUserUpdate_ReturnsUpdatedUserResponseDto() {
        // Given
        String userEmail = "example@example.com";
        UserRequestUpdateDto updateDto = getValidUserRequestUpdateDto();
        User existingUser = getValidUser();
        User updatedUser = getUpdatedUser(existingUser, updateDto);
        UserResponseDto expectedResponseDto = getUserResponseDtoFromUser(updatedUser);

        Mockito.when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        Mockito.when(userMapper.toDto(updatedUser)).thenReturn(expectedResponseDto);

        // When
        UserResponseDto actualResponseDto = userService.update(userEmail, updateDto);

        // Then
        assertThat(actualResponseDto).isEqualTo(expectedResponseDto);
        verify(userRepository).findByEmail(userEmail);
        verify(userRepository).save(existingUser);
        verify(userMapper).toDto(updatedUser);
    }

    @Test
    @DisplayName("Verify findUsersByBirthDateRange throws IllegalArgumentException")
    void findUsersByBirthDateRange_ThrowsIllegalArgumentException() {
        // Given
        LocalDate fromDate = LocalDate.of(2000, 1, 1);
        LocalDate toDate = LocalDate.of(1990, 12, 31);
        Pageable pageable = PageRequest.of(0, 10); // Параметри сторінки

        // When and Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.findUsersByBirthDateRange(fromDate, toDate, pageable);
        });
    }

    @Test
    @DisplayName("Verify findUsersByBirthDateRange works correctly")
    void findUsersByBirthDateRange_ReturnsUserResponseDtoList() {
        // Given
        List<User> users = new ArrayList<>();
        users.add(getUserWithBirthDate(LocalDate.of(1995, 5, 10)));
        users.add(getUserWithBirthDate(LocalDate.of(1992, 8, 20)));
        users.add(getUserWithBirthDate(LocalDate.of(1998, 3, 15)));

        List<UserResponseDto> expectedResponseDtoList = users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());

        LocalDate fromDate = LocalDate.of(1990, 1, 1);
        LocalDate toDate = LocalDate.of(2000, 12, 31);
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(userRepository.findAllByBirthDateBetween(fromDate, toDate, pageable))
                .thenReturn(users);

        // When
        List<UserResponseDto> actualResponseDtoList =
                userService.findUsersByBirthDateRange(fromDate, toDate, pageable);

        // Then
        assertThat(actualResponseDtoList).isEqualTo(expectedResponseDtoList);
        verify(userRepository).findAllByBirthDateBetween(fromDate, toDate, pageable);
    }

    @Test
    @DisplayName("Verify deleteById() method works")
    void deleteById_ValidId_DeletesUser() {
        // Given
        Long userId = 1L;

        // When
        userService.deleteById(userId);

        // Then
        verify(userRepository).deleteById(userId);
    }

    private User getUserWithBirthDate(LocalDate birthDate) {
        User user = new User();
        user.setEmail("example@example.com");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setBirthDate(birthDate);
        user.setAddress("Address");
        user.setPhone("Phone");
        return user;
    }

    private UserRequestUpdateDto getValidUserRequestUpdateDto() {
        UserRequestUpdateDto updateDto = new UserRequestUpdateDto();
        updateDto.setFirstName("UpdatedFirstName");
        updateDto.setLastName("UpdatedLastName");
        updateDto.setBirthDate(LocalDate.of(1990, 1, 1));
        updateDto.setAddress("UpdatedAddress");
        updateDto.setPhone("UpdatedPhone");
        return updateDto;
    }

    private User getValidUser() {
        User user = new User();
        user.setEmail("example@example.com");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setAddress("Address");
        user.setPhone("Phone");
        return user;
    }

    private User getUpdatedUser(User existingUser, UserRequestUpdateDto updateDto) {
        existingUser.setFirstName(updateDto.getFirstName());
        existingUser.setLastName(updateDto.getLastName());
        existingUser.setBirthDate(updateDto.getBirthDate());
        existingUser.setAddress(updateDto.getAddress());
        existingUser.setPhone(updateDto.getPhone());
        return existingUser;
    }

    private UserResponseDto getUserResponseDtoFromUser(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());
        userResponseDto.setBirthDate(user.getBirthDate());
        userResponseDto.setAddress(user.getAddress());
        userResponseDto.setPhone(user.getPhone());
        return userResponseDto;
    }

    private User getUserFromRegistrationRequestDto(
            UserRegistrationRequestDto registrationRequestDto, Role userRole) {
        User user = new User();
        user.setId(1L);
        user.setEmail(registrationRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationRequestDto.getPassword()));
        user.setBirthDate(registrationRequestDto.getBirthDate());
        user.setFirstName(registrationRequestDto.getFirstName());
        user.setLastName(registrationRequestDto.getLastName());
        user.setAddress(registrationRequestDto.getAddress());
        user.setPhone(registrationRequestDto.getPhone());
        user.setRoles(Set.of(userRole));
        return user;
    }

    private UserRegistrationRequestDto getValidUserRegistrationRequestDto() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("example@example.com");
        requestDto.setPassword("ValidPassword123");
        requestDto.setRepeatPassword("ValidPassword123");
        requestDto.setBirthDate(LocalDate.of(1990, 1, 1));
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");
        requestDto.setAddress("123 Main St");
        requestDto.setPhone("123-456-7890");
        return requestDto;
    }
}
