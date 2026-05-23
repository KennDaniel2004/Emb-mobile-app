package com.example.embr6monitoringapp.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.embr6monitoringapp.Models.UserModel;
import com.example.embr6monitoringapp.R;
import com.example.embr6monitoringapp.Service.AuthService;
import com.example.embr6monitoringapp.Service.AuthServiceImpl;
import com.example.embr6monitoringapp.Utils.SessionManager;
import com.example.embr6monitoringapp.Utils.SyncManager;

public class LoginController extends AppCompatActivity {

    EditText    username, password;
    ImageButton btnTogglePassword;
    Button      loginBtn;
    TextView    registerText;
    AuthService authService;
    SyncManager syncManager;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        username          = findViewById(R.id.username);
        password          = findViewById(R.id.password);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        loginBtn          = findViewById(R.id.loginBtn);
        registerText      = findViewById(R.id.Register);

        authService = new AuthServiceImpl(this);
        syncManager = new SyncManager(this);

        setupPasswordToggle();

        loginBtn.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            UserModel loggedUser = authService.login(user, pass);

            if (loggedUser != null) {

                SessionManager session = SessionManager.getInstance();
                session.setEmployeeId(loggedUser.getEmployeeId());
                session.setFullName(loggedUser.getFullName());
                session.setPosition(loggedUser.getPosition());

                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                syncManager.syncIfOnline();

                Intent intent = new Intent(this, DashboardController.class);
                intent.putExtra("EMPLOYEE_ID", loggedUser.getEmployeeId());
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this,
                        "Invalid Username or Password", Toast.LENGTH_LONG).show();
            }
        });

        registerText.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterController.class))
        );
    }

    private void setupPasswordToggle() {
        btnTogglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            password.setTransformationMethod(isPasswordVisible
                    ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            btnTogglePassword.setImageResource(isPasswordVisible
                    ? R.drawable.ic_eye_on
                    : R.drawable.ic_eye_off);
            password.setSelection(password.getText().length());
        });
    }
}