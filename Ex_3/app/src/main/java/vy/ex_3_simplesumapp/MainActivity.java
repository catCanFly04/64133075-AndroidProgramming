package vy.ex_3_simplesumapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
    }
        public void XulyCong (View view){
            EditText editTextSoA = findViewById(R.id.edtA);
            EditText editTextSoB = findViewById(R.id.edtB);
            EditText editTextKQ = findViewById(R.id.edtKQ);

            String strA = editTextSoA.getText().toString();
            String strB = editTextSoB.getText().toString();
            //chuyer du lieu sang dang so

            int so_A = Integer.parseInt(strA);
            int so_B = Integer.parseInt(strB);

            //tinh tong

            int tong = so_A + so_B;
            String strTong = String.valueOf(tong);

            //hien ra man hinh

            editTextKQ.setText(strTong);


        }
    }
