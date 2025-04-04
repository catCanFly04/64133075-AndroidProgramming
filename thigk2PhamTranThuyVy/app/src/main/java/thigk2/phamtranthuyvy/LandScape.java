package thigk2.phamtranthuyvy;

public class LandScape {
    String landImageFileName;
    String landCaption;
    String description;  // Thêm trường mô tả

    public LandScape(String landImageFileName, String landCaption, String description) {
        this.landImageFileName = landImageFileName;
        this.landCaption = landCaption;
        this.description = description;
    }

    public String getLandImageFileName() {
        return landImageFileName;
    }

    public void setLandImageFileName(String landImageFileName) {
        this.landImageFileName = landImageFileName;
    }

    public String getLandCaption() {
        return landCaption;
    }

    public void setLandCaption(String landCaption) {
        this.landCaption = landCaption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}