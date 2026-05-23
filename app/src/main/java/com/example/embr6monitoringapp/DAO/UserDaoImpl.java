package com.example.embr6monitoringapp.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.embr6monitoringapp.Database.DatabaseConnection;
import com.example.embr6monitoringapp.Models.UserModel;

public class UserDaoImpl implements UserDao {

    private final DatabaseConnection dbHelper;

    public UserDaoImpl(Context context) {
        dbHelper = DatabaseConnection.getInstance(context);
    }


    @Override
    public boolean registerUser(UserModel user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Employee_Id", user.getEmployeeId());
        values.put("First_Name",  user.getFirstName());
        values.put("Last_Name",   user.getLastName());
        values.put("Middle_Name", user.getMiddleName());
        values.put("Position",    user.getPosition());
        values.put("Username",    user.getUsername());
        values.put("Password",    user.getPassword()); // AES-encrypted
        values.put("is_synced",   0);

        long result = db.insert("Register", null, values);
        return result != -1;
    }

    @Override
    public UserModel findByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM Register WHERE Username = ?",
                new String[]{username}
        );

        if (cursor.moveToFirst()) {
            UserModel user = mapCursor(cursor);
            cursor.close();
            return user;
        }

        cursor.close();
        return null;
    }

    private UserModel mapCursor(Cursor cursor) {
        UserModel user = new UserModel();
        user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        user.setEmployeeId(cursor.getString(cursor.getColumnIndexOrThrow("Employee_Id")));
        user.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow("First_Name")));
        user.setLastName(cursor.getString(cursor.getColumnIndexOrThrow("Last_Name")));
        user.setMiddleName(cursor.getString(cursor.getColumnIndexOrThrow("Middle_Name")));
        user.setPosition(cursor.getString(cursor.getColumnIndexOrThrow("Position")));
        user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("Username")));
        user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("Password")));
        return user;
    }
}