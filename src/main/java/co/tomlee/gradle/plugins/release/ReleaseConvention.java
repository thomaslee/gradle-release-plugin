package co.tomlee.gradle.plugins.release;

import org.gradle.api.Project;

import java.io.File;

public class ReleaseConvention {
    private Project project;

    private File propertiesFile;
    private String versionProperty = "version";
    private String preTagCommitMessage = "[release] {0} -> {1}";

    public void setProject(final Project project) {
        this.project = project;
    }

    public void propertiesFile(final String path) {
        this.propertiesFile = project.file(path);
    }

    public void propertiesFile(final File file) {
        this.propertiesFile = file;
    }

    public File getPropertiesFile() {
        if (propertiesFile == null) {
            propertiesFile = project.file("build.properties");
        }
        final File file = propertiesFile;
        if (!file.exists()) {
            throw new IllegalStateException(file + " does not exist");
        }
        return file;
    }

    public void versionProperty(final String versionProperty) {
        this.versionProperty = versionProperty;
    }

    public String getVersionProperty() {
        return versionProperty;
    }
}
