package vy.ex4;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editTextA, editTextB;
    private TextView textViewKetQua;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ánh xạ thành phần giao diện
        editTextA = findViewById(R.id.edtA);
        editTextB = findViewById(R.id.edtB);
        textViewKetQua = findViewById(R.id.txtKetQua);
    }

    // Hàm kiểm tra và lấy số từ EditText
    private boolean laySoTuEditText() {
        String strA = editTextA.getText().toString();
        String strB = editTextB.getText().toString();

        if (strA.isEmpty() || strB.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ số!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Hàm xử lý phép cộng
    public void XuLyCong(View view) {
        if (!laySoTuEditText()) return;

        int soA = Integer.parseInt(editTextA.getText().toString());
        int soB = Integer.parseInt(editTextB.getText().toString());

        textViewKetQua.setText("Kết quả: " + (soA + soB));
    }

    // Hàm xử lý phép trừ
    public void XuLyTru(View view) {
        if (!laySoTuEditText()) return;

        int soA = Integer.parseInt(editTextA.getText().toString());
        int soB = Integer.parseInt(editTextB.getText().toString());

        textViewKetQua.setText("Kết quả: " + (soA - soB));
    }

    // Hàm xử lý phép nhân
    public void XuLyNhan(View view) {
        if (!laySoTuEditText()) return;

        int soA = Integer.parseInt(editTextA.getText().toString());
        int soB = Integer.parseInt(editTextB.getText().toString());

        textViewKetQua.setText("Kết quả: " + (soA * soB));
    }

    // Hàm xử lý phép chia
    public void XuLyChia(View view) {
        if (!laySoTuEditText()) return;

        int soA = Integer.parseInt(editTextA.getText().toString());
        int soB = Integer.parseInt(editTextB.getText().toString());

        if (soB == 0) {
            Toast.makeText(this, "Không thể chia cho 0!", Toast.LENGTH_SHORT).show();
            return;
        }

        textViewKetQua.setText("Kết quả: " + ((float) soA / soB));
    }
}