package app.users.dto;

import app.users.validation.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    @Email
    private String email;
    @NotNull
    @Size(min = 7, max = 60)
    private String password;
}
