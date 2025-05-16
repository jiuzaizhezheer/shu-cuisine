package com.hzy.service;

import com.hzy.dto.UserDTO;
import com.hzy.dto.UserLoginDTO;
import com.hzy.entity.User;

public interface UserService {
    User wxLogin(UserLoginDTO userLoginDTO);

    User getUser(Integer id);

    void update(UserDTO userDTO);
}
