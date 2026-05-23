package com.example.embr6monitoringapp.DAO;

import com.example.embr6monitoringapp.Models.UserModel;

public interface UserDao {


    boolean registerUser(UserModel user);


    UserModel findByUsername(String username);
}