package vy.ex5_addsubmuldiv_anynomous;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

        // Ánh xạ các thành phần giao diện
        editTextA = findViewById(R.id.edtA);
        editTextB = findViewById(R.id.edtB);
        textViewKetQua = findViewById(R.id.txtKetQua);

        Button btnCong = findViewById(R.id.btnCong);
        Button btnTru = findViewById(R.id.btnTru);
        Button btnNhan = findViewById(R.id.btnNhan);
        Button btnChia = findViewById(R.id.btnChia);

        // Đăng ký bộ lắng nghe ẩn danh
        btnCong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyPhepToan("+");
            }
        });

        btnTru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyPhepToan("-");
            }
        });

        btnNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyPhepToan("*");
            }
        });

        btnChia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyPhepToan("/");
            }
        });
    }

    // Hàm xử lý phép toán chung
    private void xuLyPhepToan(String phepToan) {
        String strA = editTextA.getText().toString();
        String strB = editTextB.getText().toString();

        if (strA.isEmpty() || strB.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ số!", Toast.LENGTH_SHORT).show();
            return;
        }

        int soA = Integer.parseInt(strA);
        int soB = Integer.parseInt(strB);
        int ketQua = 0;
        float ketQuaChia = 0;

        switch (phepToan) {
            case "+":
                ketQua = soA + soB;
                textViewKetQua.setText("Kết quả: " + ketQua);
                break;
            case "-":
                ketQua = soA - soB;
                textViewKetQua.setText("Kết quả: " + ketQua);
                break;
            case "*":
                ketQua = soA * soB;
                textViewKetQua.setText("Kết quả: " + ketQua);
                break;
            case "/":
                if (soB == 0) {
                    Toast.makeText(this, "Không thể chia cho 0!", Toast.LENGTH_SHORT).show();
                    return;
                }
                ketQuaChia = (float) soA / soB;
                textViewKetQua.setText("Kết quả: " + ketQuaChia);
                break;
        }
    }
}