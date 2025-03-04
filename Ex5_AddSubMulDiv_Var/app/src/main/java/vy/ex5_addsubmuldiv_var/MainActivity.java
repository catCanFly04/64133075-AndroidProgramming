package vy.ex5_addsubmuldiv_var;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private Button btnCong, btnTru, btnNhan, btnChia;
    private EditText edtA, edtB;
    private TextView txtKetQua;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        edtA = findViewById(R.id.edtA);
        edtB = findViewById(R.id.edtB);
        txtKetQua = findViewById(R.id.txtKetQua);
        btnCong = findViewById(R.id.btnCong);
        btnTru = findViewById(R.id.btnTru);
        btnNhan = findViewById(R.id.btnNhan);
        btnChia = findViewById(R.id.btnChia);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double a, b, result = 0;
                try {
                    a = Double.parseDouble(edtA.getText().toString());
                    b = Double.parseDouble(edtB.getText().toString());

                    if (v.getId() == R.id.btnCong) {
                        result = a + b;
                    } else if (v.getId() == R.id.btnTru) {
                        result = a - b;
                    } else if (v.getId() == R.id.btnNhan) {
                        result = a * b;
                    } else if (v.getId() == R.id.btnChia) {
                        if (b != 0) {
                            result = a / b;
                        } else {
                            txtKetQua.setText("Lỗi: Không thể chia cho 0");
                            return;
                        }
                    }
                    txtKetQua.setText("Kết quả: " + result);
                } catch (NumberFormatException e) {
                    txtKetQua.setText("Lỗi: Vui lòng nhập số hợp lệ");
                }
            }
        };
        btnCong.setOnClickListener(listener);
        btnTru.setOnClickListener(listener);
        btnNhan.setOnClickListener(listener);
        btnChia.setOnClickListener(listener);



    }
}