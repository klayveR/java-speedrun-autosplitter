package speedrunapi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Game implements Serializable {
    private String name;
    private List<String> categories = new ArrayList<>();

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void addCategory(String category) {
        if (!this.categories.contains(category))
            this.categories.add(category);
    }

    public List<String> getCategories() {
        return this.categories;
    }
}
