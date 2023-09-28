package app.users.dto;

import app.users.validation.AgeOver;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import lombok.Data;

@Data
public class UserRequestUpdateDto {
    @NotNull
    @Past
    @AgeOver
    private LocalDate birthDate;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private String address;
    private String phone;
}
