package com.example.embr6monitoringapp.Service;

import android.content.Context;

import com.example.embr6monitoringapp.DAO.UserDao;
import com.example.embr6monitoringapp.DAO.UserDaoImpl;
import com.example.embr6monitoringapp.Models.UserModel;
import com.example.embr6monitoringapp.Utils.AES;


public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;

    public AuthServiceImpl(Context context) {
        this.userDao = new UserDaoImpl(context);
    }


    @Override
    public int register(UserModel user, String confirmPassword) {


        if (!user.getPassword().equals(confirmPassword)) {
            return -1;
        }

        String encrypted = AES.encrypt(user.getPassword());
        user.setPassword(encrypted);

        boolean inserted = userDao.registerUser(user);
        return inserted ? 0 : -2;
    }

    @Override
    public UserModel login(String username, String rawPassword) {
        UserModel user = userDao.findByUsername(username);
        if (user == null) return null;

        String decrypted = AES.decrypt(user.getPassword());
        return decrypted.equals(rawPassword) ? user : null;
    }
}