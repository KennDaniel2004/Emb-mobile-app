package com.example.embr6monitoringapp.Service;

import com.example.embr6monitoringapp.Models.UserModel;

public interface AuthService {


    int register(UserModel user, String confirmPassword);


    UserModel login(String username, String rawPassword);
}