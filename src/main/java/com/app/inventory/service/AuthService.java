package com.app.inventory.service;

import com.app.inventory.dto.request.RegisterRequestDto;

public interface AuthService {

    void create(RegisterRequestDto request);

}
