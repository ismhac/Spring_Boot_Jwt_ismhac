package com.kocobiet.project.services.users;

import com.kocobiet.project.dtos.UserDto;
import com.kocobiet.project.models.User;

public interface IUserService {
    // register
    User create(UserDto userDto) throws Exception;

    // login
    String login(String email, String password, Long roleId) throws Exception;
}
