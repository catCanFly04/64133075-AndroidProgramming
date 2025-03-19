package vy.ex_ktptc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private int a, b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Khởi tạo TextView hiển thị số ngẫu nhiên
        TextView textViewA = findViewById(R.id.textViewA);
        TextView textViewB = findViewById(R.id.textViewB);
        EditText editText = findViewById(R.id.edtTextHT);

        // Sinh số ngẫu nhiên bằng Math.random()
        a = (int) (Math.random() * 10); // Số từ 0 đến 9
        b = (int) (Math.random() * 10);

        // Hiển thị số lên TextView
        textViewA.setText(String.valueOf(a));
        textViewB.setText(String.valueOf(b));
    }

    public void onNumberClick(View view) {
        EditText editText = findViewById(R.id.edtTextHT);
        Button clickedButton = (Button) view;
        editText.setText(editText.getText().toString() + clickedButton.getText().toString());
    }

    public void onClearClick(View view) {
        EditText editText = findViewById(R.id.edtTextHT);
        editText.setText("");
    }

    public void onCheckResultClick(View view) {
        EditText editText = findViewById(R.id.edtTextHT);
        String input = editText.getText().toString();

        if (input.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập kết quả!", Toast.LENGTH_SHORT).show();
            return;
        }

        int userAnswer = Integer.parseInt(input);
        int correctAnswer = a + b;

        if (userAnswer == correctAnswer) {
            Toast.makeText(this, "Đúng rồi!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sai rồi! Hãy nhập lại kết quả" , Toast.LENGTH_SHORT).show();
        }
    }
}
