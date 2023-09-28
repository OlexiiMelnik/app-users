package app.users.mapper;

import app.users.config.MapperConfig;
import app.users.dto.UserResponseDto;
import app.users.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);
}
