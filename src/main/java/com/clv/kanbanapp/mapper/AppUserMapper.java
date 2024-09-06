package com.clv.kanbanapp.mapper;

import com.clv.kanbanapp.dto.RegisterRequestBody;
import com.clv.kanbanapp.entity.AppUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppUserMapper {
    AppUser registerRequestBodyToAppUser(RegisterRequestBody user);
}
