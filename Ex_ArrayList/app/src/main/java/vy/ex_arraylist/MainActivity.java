package vy.ex_arraylist;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView listViewNNLT;
    private ArrayList<String> danhSachNgonNgu;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo ListView với ID mới
        listViewNNLT = findViewById(R.id.listViewNNLT);

        // Tạo danh sách ngôn ngữ lập trình
        danhSachNgonNgu = new ArrayList<>();
        danhSachNgonNgu.add("Java");
        danhSachNgonNgu.add("Python");
        danhSachNgonNgu.add("C++");
        danhSachNgonNgu.add("JavaScript");
        danhSachNgonNgu.add("Swift");
        danhSachNgonNgu.add("Kotlin");
        danhSachNgonNgu.add("Go");
        danhSachNgonNgu.add("Ruby");

        // Tạo Adapter để hiển thị danh sách trong ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, danhSachNgonNgu);
        listViewNNLT.setAdapter(adapter);

        // Bộ lắng nghe sự kiện khi người dùng nhấn vào một mục trong ListView
        listViewNNLT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ngonNgu = danhSachNgonNgu.get(position);
                Toast.makeText(MainActivity.this, "Bạn chọn: " + ngonNgu, Toast.LENGTH_SHORT).show();
            }
        });
    }
}