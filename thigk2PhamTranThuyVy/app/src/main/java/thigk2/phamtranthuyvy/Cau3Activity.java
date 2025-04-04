package thigk2.phamtranthuyvy;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Cau3Activity extends AppCompatActivity {
    private LandScapeAdapter landScapeAdapter;
    private ArrayList<LandScape> recyclerViewDatas;
    private RecyclerView recyclerViewLandScape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cau3); // Kiểm tra tên đúng của file layout

        // Lấy dữ liệu
        recyclerViewDatas = getDataForRecyclerView();

        // Ánh xạ RecyclerView từ layout
        recyclerViewLandScape = findViewById(R.id.recyclerLand);

        // Cài đặt LayoutManager cho RecyclerView
        RecyclerView.LayoutManager layoutLinear = new LinearLayoutManager(this);
        recyclerViewLandScape.setLayoutManager(layoutLinear);

        // Tạo và cài đặt adapter
        landScapeAdapter = new LandScapeAdapter(this, recyclerViewDatas);
        recyclerViewLandScape.setAdapter(landScapeAdapter);
    }

    private ArrayList<LandScape> getDataForRecyclerView() {
        ArrayList<LandScape> dsDulieu = new ArrayList<>();
        dsDulieu.add(new LandScape("sanrio2", "Hello Kitty và Những người bạn", "Một nhóm nhân vật dễ thương từ Sanrio."));
        dsDulieu.add(new LandScape("pom", "Chú chó Pom", "Một chú chó màu vàng đáng yêu."));
        dsDulieu.add(new LandScape("pocha", "Chú chó Pocha", "Nhân vật Sanrio dễ thương có đôi tai dài."));
        dsDulieu.add(new LandScape("sanrio1", "Các nhân vật Sanrio", "Tổng hợp nhiều nhân vật nổi tiếng của Sanrio."));
        return dsDulieu;
    }
}
