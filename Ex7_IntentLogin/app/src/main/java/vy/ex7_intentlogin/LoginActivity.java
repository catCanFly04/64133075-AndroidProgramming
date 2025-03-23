package vy.ex7_intentlogin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtUsername = findViewById(R.id.edtUsername);
        EditText edtPassword = findViewById(R.id.edtPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if (username.equals("admin") && password.equals("1")) {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    email ="vykhungkkk@gmsil.vom";
                    intent.putExtra("email", email);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}