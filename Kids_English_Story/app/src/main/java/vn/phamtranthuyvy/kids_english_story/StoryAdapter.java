package vn.phamtranthuyvy.kids_english_story; // Nhớ thay bằng package name của bạn

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Import Glide để tải ảnh
import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private Context context;
    private List<Story> storyList;

    // Constructor của Adapter
    public StoryAdapter(Context context, List<Story> storyList) {
        this.context = context;
        this.storyList = storyList;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // "Thổi phồng" layout item_story.xml để tạo ra view cho mỗi item
        View view = LayoutInflater.from(context).inflate(R.layout.item_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        // Lấy đối tượng Story ở vị trí hiện tại
        Story currentStory = storyList.get(position);

        // Gán dữ liệu từ Story vào các view trong ViewHolder
        // Ưu tiên hiển thị tên tiếng Việt nếu có, nếu không thì hiển thị tên tiếng Anh
        String storyTitle;
        if (currentStory.getTitle_vi() != null && !currentStory.getTitle_vi().isEmpty()) {
            storyTitle = currentStory.getTitle_vi();
        } else {
            storyTitle = currentStory.getTitle_en();
        }
        holder.textViewStoryTitle.setText(storyTitle);

        // Sử dụng Glide để tải ảnh bìa từ URL vào ImageView
        // Đảm bảo bạn đã thêm thư viện Glide vào build.gradle
        if (currentStory.getCoverImageUrl() != null && !currentStory.getCoverImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(currentStory.getCoverImageUrl())
                    .placeholder(R.drawable.anh1) // Ảnh hiển thị trong khi tải
                    .error(R.drawable.anh1)       // Ảnh hiển thị nếu có lỗi tải
                    .into(holder.imageViewStoryCover);
        } else {
            // Nếu không có URL ảnh bìa, hiển thị ảnh placeholder mặc định
            holder.imageViewStoryCover.setImageResource(R.drawable.anh1);
        }

        // Xử lý sự kiện khi người dùng nhấn vào một item truyện
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để mở StoryDetailActivity (bạn cần tạo Activity này)
                Intent intent = new Intent(context, StoryDetailActivity.class);
                // Gửi ID của truyện được chọn qua Intent
                // StoryDetailActivity sẽ dùng ID này để lấy chi tiết truyện từ Firestore
                intent.putExtra("SELECTED_STORY_ID", currentStory.getStoryId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng truyện trong danh sách
        return storyList == null ? 0 : storyList.size();
    }

    // Lớp ViewHolder để giữ các view của mỗi item truyện
    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewStoryCover;
        TextView textViewStoryTitle;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các view từ layout item_story.xml
            imageViewStoryCover = itemView.findViewById(R.id.imageViewStoryCoverItem);
            textViewStoryTitle = itemView.findViewById(R.id.textViewStoryTitleItem);
        }
    }

    // (Tùy chọn) Hàm để cập nhật dữ liệu cho adapter nếu danh sách truyện thay đổi
    public void updateData(List<Story> newStoryList) {
        this.storyList.clear();
        if (newStoryList != null) {
            this.storyList.addAll(newStoryList);
        }
        notifyDataSetChanged(); // Thông báo cho RecyclerView cập nhật lại giao diện
    }
}
