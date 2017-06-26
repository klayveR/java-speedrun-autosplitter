package project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {
    private List<Split> splits;
    private String gameName;
    private String categoryName;

    public Project(String gameName) {
        this.gameName = gameName;
        this.splits = new ArrayList<>();
    }

    /**
     * Adds a new split to the project
     *
     * @param split The split to be added
     */
    public void addSplit(Split split) {
        if (!this.splits.contains(split))
            this.splits.add(split);
    }

    /**
     * Removes a split from the project
     *
     * @param split The split to be removed
     */
    public void removeSplit(Split split) {
        if (this.splits.contains(split))
            this.splits.remove(split);
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getProjectName() {
        if (!this.categoryName.isEmpty()) {
            return this.gameName + " - " + this.categoryName;
        } else {
            return this.gameName;
        }
    }
}
