package project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectManager {
    // Logger
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    // Projects file name
    private static final String PROJECTS_FILE = "projects.save";

    // Projects
    private List<Project> projects = new ArrayList<>();
    private Project currentProject = null;

    public ProjectManager() {
        this.loadProjects();
    }

    /**
     * Loads a project by specifying the name of a project, if it exists
     *
     * @param projectName The name of the project that should be loaded
     * @return true, if the project was loaded successfully
     */
    public boolean loadProject(String projectName) {
        // Check if project exists
        if (this.hasProject(projectName)) {
            this.currentProject = this.getProjectByName(projectName);
            return true;
        }
        return false;
    }

    /**
     * Loads a project by specifying a project object
     *
     * @param project The project that should be loaded
     * @return true, if the project was loaded successfully
     */
    public boolean loadProject(Project project) {
        if (this.projects.contains(project)) {
            this.currentProject = project;
            return true;
        }
        return false;
    }

    /**
     * Adds a project, if the name is not taken yet
     *
     * @param project The project to be added
     * @return true, if the project was added successfully
     */
    public boolean addProject(Project project) {
        // Add project if it's not added yet & name doesn't exist & name isn't empty
        if (!this.projects.contains(project)
                && !this.hasProject(project.getProjectName())
                && !project.getProjectName().isEmpty()) {
            this.projects.add(project);
            return true;
        }
        return false;
    }

    /**
     * Removes a project if it exists and not the current project
     *
     * @param project The project to be removed
     * @return true, if the project was removed successfully
     */
    public boolean removeProject(Project project) {
        if (this.projects.contains(project) && !project.equals(this.currentProject)) {
            this.projects.remove(project);
            return true;
        }
        return false;
    }

    /**
     * Loads existing projects into memory
     */
    private void loadProjects() {
        File projectsFile = new File(PROJECTS_FILE);
        if (projectsFile.exists() && !projectsFile.isDirectory()) {
            loadProjectsFile();
        } else {
            saveProjectsFile();
        }
    }

    /**
     * Loads projects from the project file
     */
    private void loadProjectsFile() {
        try {
            FileInputStream fis = new FileInputStream(PROJECTS_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.projects = (List<Project>) ois.readObject();
            ois.close();

            log.info("Project file loaded successfully");
        } catch (IOException e) {
            // TODO
            // Project file is most likely corrupted
            log.error("An error ocurred while reading from the projects file", e);
        } catch (ClassNotFoundException e) {
            // This should not happen...
            log.error("An error ocurred while reading from the projects file", e);
        }
    }

    /**
     * Saves any changes done to the projects to file
     */
    public void saveProjectsFile() {
        try {
            FileOutputStream fos = new FileOutputStream(PROJECTS_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.projects);
            oos.close();

            log.info("Project file saved successfully");
        } catch (IOException e) {
            // TODO
            // Might be permissions, whatever, we'll see
            log.error("An error ocurred while writing to the projects file", e);
        }
    }

    /**
     * Checks if a project exists by providing a project name
     *
     * @param projectName The name of the project
     * @return true, if the project exists
     */
    public boolean hasProject(String projectName) {
        for (Project project : this.projects) {
            if (project.getProjectName().equals(projectName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a project exists by providing a project object
     *
     * @param project A project object
     * @return true, if the project exists
     */
    public boolean hasProject(Project project) {
        return this.projects.contains(project);
    }

    /**
     * @param projectName The name of a project
     * @return The project object, if it exists
     */
    public Project getProjectByName(String projectName) {
        for (Project project : this.projects) {
            if (project.getProjectName().equals(projectName)) {
                return project;
            }
        }
        return null;
    }

    public Project getCurrentProject() {
        return this.currentProject;
    }

    public List<Project> getProjects() {
        return this.projects;
    }
}
