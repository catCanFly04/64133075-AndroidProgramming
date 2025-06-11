package vn.phamtranthuyvy.kids_english_story;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns; // Import để kiểm tra định dạng email
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import vn.phamtranthuyvy.kids_english_story.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        // Sự kiện nút Đăng ký
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Sự kiện nhấn vào "Đã có tài khoản? Đăng nhập"
        binding.tvLoginPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kết thúc màn hình Đăng ký và quay lại màn hình trước đó (thường là Login)
                finish();
            }
        });
    }

    private void registerUser() {
        String email = binding.etRegisterEmail.getText().toString().trim();
        String password = binding.etRegisterPassword.getText().toString().trim();
        String confirmPassword = binding.etRegisterConfirmPassword.getText().toString().trim();

        // --- Kiểm tra dữ liệu đầu vào ---
        if (TextUtils.isEmpty(email)) {
            binding.etRegisterEmail.setError("Email không được để trống");
            binding.etRegisterEmail.requestFocus();
            return;
        }
        // Kiểm tra định dạng email hợp lệ
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etRegisterEmail.setError("Vui lòng nhập email hợp lệ");
            binding.etRegisterEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            binding.etRegisterPassword.setError("Mật khẩu không được để trống");
            binding.etRegisterPassword.requestFocus();
            return;
        }
        // Firebase yêu cầu mật khẩu ít nhất 6 ký tự
        if (password.length() < 6) {
            binding.etRegisterPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            binding.etRegisterPassword.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            binding.etRegisterConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            binding.etRegisterConfirmPassword.requestFocus();
            return;
        }
        // Kiểm tra mật khẩu xác nhận có khớp không
        if (!password.equals(confirmPassword)) {
            binding.etRegisterConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            binding.etRegisterConfirmPassword.requestFocus();
            // Xóa nội dung ô xác nhận mật khẩu để người dùng nhập lại
            binding.etRegisterConfirmPassword.setText("");
            return;
        }
        // --- Kết thúc kiểm tra ---

        // TODO: Hiển thị ProgressBar

        // Gọi Firebase Authentication để tạo người dùng mới
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // TODO: Ẩn ProgressBar
                        if (task.isSuccessful()) {
                            // Đăng ký thành công, Firebase tự động đăng nhập cho người dùng mới
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                            // Chuyển thẳng vào màn hình Home sau khi đăng ký thành công
                            navigateToHome();

                        } else {
                            // Nếu đăng ký thất bại, hiển thị thông báo lỗi
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            // Hiển thị lỗi cụ thể hơn từ Firebase (ví dụ: email đã tồn tại)
                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // Hàm chuyển sang HomeActivity và xóa các Activity cũ khỏi back stack
    private void navigateToHome() {
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Kết thúc RegisterActivity
    }
}