package vn.phamtranthuyvy.kids_english_story; // Nhớ thay bằng package name của bạn

// Class này dùng để đại diện cho một đối tượng truyện lấy từ Firestore
public class Story {

    // Các trường dữ liệu (fields) cho một truyện
    // Tên trường nên khớp với tên field trong document Firestore để việc chuyển đổi tự động dễ dàng
    private String storyId;        // ID của truyện (có thể là ID document từ Firestore)
    private String themeId;        // ID của chủ đề truyện thuộc về
    private String title_en;       // Tên truyện bằng tiếng Anh
    private String title_vi;       // Tên truyện bằng tiếng Việt
    private String content_en;     // Nội dung truyện tiếng Anh
    private String content_vi;     // Nội dung truyện tiếng Việt
    private String coverImageUrl;  // URL của ảnh bìa truyện
    private String age_group;      // Nhóm tuổi phù hợp (ví dụ: "3-5", "6-8", "ALL")
    private long order;            // Số thứ tự (nếu cần sắp xếp truyện theo thứ tự cụ thể)

    // Constructor rỗng (BẮT BUỘC phải có để Firestore có thể tự động chuyển đổi dữ liệu thành đối tượng Story)
    public Story() {
        // Firestore cần constructor rỗng này
    }

    // Constructor đầy đủ (tùy chọn, tiện lợi khi bạn muốn tự tạo đối tượng Story trong code)
    public Story(String storyId, String themeId, String title_en, String title_vi,
                 String content_en, String content_vi, String coverImageUrl,
                 String age_group, long order) {
        this.storyId = storyId;
        this.themeId = themeId;
        this.title_en = title_en;
        this.title_vi = title_vi;
        this.content_en = content_en;
        this.content_vi = content_vi;
        this.coverImageUrl = coverImageUrl;
        this.age_group = age_group;
        this.order = order;
    }

    // Getters and Setters cho tất cả các trường
    // (Firestore cũng sử dụng getters và setters này)

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getThemeId() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = themeId;
    }

    public String getTitle_en() {
        return title_en;
    }

    public void setTitle_en(String title_en) {
        this.title_en = title_en;
    }

    public String getTitle_vi() {
        return title_vi;
    }

    public void setTitle_vi(String title_vi) {
        this.title_vi = title_vi;
    }

    public String getContent_en() {
        return content_en;
    }

    public void setContent_en(String content_en) {
        this.content_en = content_en;
    }

    public String getContent_vi() {
        return content_vi;
    }

    public void setContent_vi(String content_vi) {
        this.content_vi = content_vi;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getAge_group() {
        return age_group;
    }

    public void setAge_group(String age_group) {
        this.age_group = age_group;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }
}