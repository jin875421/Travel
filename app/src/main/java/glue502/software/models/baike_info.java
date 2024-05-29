package glue502.software.models;

public class baike_info {
    private String baike_url;
    private String image_url;
    private String description;

    public baike_info(String baike_url, String image_url, String description) {
        this.baike_url = baike_url;
        this.image_url = image_url;
        this.description = description;
    }

    @Override
    public String toString() {
        return "baike_info{" +
                "baike_url='" + baike_url + '\'' +
                ", image_url='" + image_url + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getBaike_url() {
        return baike_url;
    }

    public void setBaike_url(String baike_url) {
        this.baike_url = baike_url;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
