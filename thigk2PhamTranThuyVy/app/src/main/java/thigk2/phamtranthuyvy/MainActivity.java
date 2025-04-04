package thigk2.phamtranthuyvy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

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

        Button btnCau1 = findViewById(R.id.btnCau1);
        Button btnCau2 = findViewById(R.id.btnCau2);
        Button btnCau3 = findViewById(R.id.btnCau3);
        Button btnCau4 = findViewById(R.id.btnCau4);
        Button btnCau5 = findViewById(R.id.btnCau5);

        btnCau1.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Cau1Activity.class);
            startActivity(intent);
        });

        btnCau2.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Cau2Activity.class);
            startActivity(intent);
        });

        btnCau3.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Cau3Activity.class);
            startActivity(intent);
        });

        btnCau4.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Cau4Activity.class);
            startActivity(intent);
        });

        btnCau5.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Cau5Activity.class);
            startActivity(intent);
        });
    }
}