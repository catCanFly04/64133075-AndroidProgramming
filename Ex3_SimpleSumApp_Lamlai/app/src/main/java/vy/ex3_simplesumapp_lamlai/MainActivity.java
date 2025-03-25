package vy.ex3_simplesumapp_lamlai;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    public void Xulycong(View view){
        EditText edtSoA = findViewById(R.id.edtSoA);
        EditText edtSoB = findViewById(R.id.edtSoB);
        EditText edtKQ = findViewById(R.id.edtKQ);

        String strA = edtSoA.getText().toString();
        String strB = edtSoB.getText().toString();

        int soA = Integer.parseInt(strA);
        int soB = Integer.parseInt(strB);

        int tong = soA + soB;
        String strTong = String.valueOf(tong);

        edtKQ.setText(strTong);
    }
}