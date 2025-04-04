package thigk2.phamtranthuyvy;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ItemDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // Lấy dữ liệu từ Intent
        String itemName = getIntent().getStringExtra("item_name");
        int itemImage = getIntent().getIntExtra("item_image", 0);
        String itemDescription = getIntent().getStringExtra("item_description");

        // Hiển thị dữ liệu lên TextView và ImageView
        TextView textViewName = findViewById(R.id.textViewItemName);
        ImageView imageView = findViewById(R.id.imageViewItem);


        textViewName.setText(itemName);
        imageView.setImageResource(itemImage);

    }
}