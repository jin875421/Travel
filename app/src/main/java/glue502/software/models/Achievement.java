package glue502.software.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class Achievement {
    private int id;
    private String imageUrl;
    private String name;
    private String description;

    public Achievement(){

    }

    public Achievement(int id, String imageUrl, String name, String description) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.description = description;
    }

    public static List<Achievement> parseAchievement(String responseData) {
        Gson gson = new Gson();
        TypeToken<List<Achievement>> typeToken = new TypeToken<List<Achievement>>() {};
        return gson.fromJson(responseData, typeToken.getType());
    }

    public int getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Achievement{" +
                "id=" + id +
                ", imageUrl='" + imageUrl + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

