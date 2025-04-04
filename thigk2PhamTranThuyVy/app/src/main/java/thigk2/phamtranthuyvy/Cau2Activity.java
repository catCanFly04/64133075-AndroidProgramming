package thigk2.phamtranthuyvy;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import thigk2.phamtranthuyvy.databinding.ActivityCau2Binding;

public class Cau2Activity extends AppCompatActivity {

    ListView listViewMonHoc;
    String[] danhSachMon = {
            "Tiến về Sài Gòn", "Giải Phóng Miền Nam", "Đất nước trọn niềm vui", "Bài ca thống nhất", "Mùa xuân trên thành phố HCM"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listViewMonHoc = findViewById(R.id.listViewMonHoc);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                danhSachMon
        );

        listViewMonHoc.setAdapter(adapter);
        listViewMonHoc.setOnItemClickListener((parent, view, position, id) -> {
            String monHoc = danhSachMon[position];
            Toast.makeText(Cau2Activity.this, "Bạn chọn: " + monHoc, Toast.LENGTH_SHORT).show();
        });
    }

}
