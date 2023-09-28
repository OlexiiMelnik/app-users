package app.users.dto;

import app.users.validation.AgeOver;
import app.users.validation.Email;
import app.users.validation.FieldMatch;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

@Data
@FieldMatch(
        field = "password",
        fieldMatch = "repeatPassword",
        message = "Password and repeat password shouldn't be empty and should be equal"
)
public class UserRegistrationRequestDto {
    @Email
    private String email;
    @NotNull
    @Size(min = 7, max = 60)
    private String password;
    private String repeatPassword;
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
