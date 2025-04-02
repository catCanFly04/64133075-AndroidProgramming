package vy.recyclerview;


import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    LandScapeAdapter landScapeAdapter;
    ArrayList<LandScape> recyclerViewDatas;
    RecyclerView recyclerViewLandScape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        recyclerViewDatas = getDataForRecyclerView();

        recyclerViewLandScape = findViewById(R.id.recyclerLand);
        RecyclerView.LayoutManager layoutLinear = new LinearLayoutManager(this);

        recyclerViewLandScape.setLayoutManager(layoutLinear);

        landScapeAdapter = new LandScapeAdapter(this, recyclerViewDatas);

        recyclerViewLandScape.setAdapter(landScapeAdapter);
    }

    ArrayList<LandScape> getDataForRecyclerView() {
        ArrayList<LandScape> dsDulieu = new ArrayList<LandScape>();
        LandScape landScape1 = new LandScape("sanrio2","Hello Kitty và Những người bạn");
        dsDulieu.add(landScape1);
        dsDulieu.add( new LandScape("pom" , "Chú chó pom"));
        dsDulieu.add( new LandScape("pocha" , "Chú chó pocha"));
        dsDulieu.add( new LandScape("sanrio1" , "Các nhân vật sanrio"));
        return dsDulieu;

    }
}