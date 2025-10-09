package com.app.inventory.service;

import com.app.inventory.model.User;

public interface EmailService {

    void sendAccountActivationEmail(User user);

}
