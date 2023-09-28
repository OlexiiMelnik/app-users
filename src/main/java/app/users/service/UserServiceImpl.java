package app.users.service;

import app.users.dto.UserRegistrationRequestDto;
import app.users.dto.UserRequestUpdateDto;
import app.users.dto.UserResponseDto;
import app.users.exeption.EntityNotFoundException;
import app.users.exeption.RegistrationException;
import app.users.mapper.UserMapper;
import app.users.model.Role;
import app.users.model.User;
import app.users.repository.RoleRepository;
import app.users.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(
            UserRegistrationRequestDto requestDto) throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Unable to complete registration");
        }
        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setBirthDate(requestDto.getBirthDate());
        user.setAddress(requestDto.getAddress());
        user.setPhone(requestDto.getPhone());
        Role userRole = roleRepository.findRoleByRoleName(Role.RoleName.USER)
                .orElseThrow(() -> new RegistrationException("Can't find role by name"));
        user.setRoles(Set.of(userRole));
        User saveUser = userRepository.save(user);
        return userMapper.toDto(saveUser);
    }

    @Override
    public UserResponseDto update(String userEmail, UserRequestUpdateDto updateDto) {
        User userToUpdate = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find user by email: " + userEmail));
        userToUpdate.setFirstName(updateDto.getFirstName());
        userToUpdate.setLastName(updateDto.getLastName());
        userToUpdate.setBirthDate(updateDto.getBirthDate());
        userToUpdate.setAddress(updateDto.getAddress());
        userToUpdate.setPhone(updateDto.getPhone());
        User updatedUser = userRepository.save(userToUpdate);
        return userMapper.toDto(updatedUser);
    }

    @Override
    public List<UserResponseDto> findUsersByBirthDateRange(
            LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("'From' date must be less than 'To' date.");
        }
        return userRepository.findAllByBirthDateBetween(fromDate, toDate, pageable).stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
