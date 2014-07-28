package co.tomlee.gradle.plugins.release;

import org.gradle.api.Project;

import java.io.File;

public class ReleaseConvention {
    private Project project;

    private String propertiesFile;
    private String versionProperty = "version";
    private String preTagCommitMessage = "[release] cutting release: v{0}";
    private String tagFormat = "v{0}";
    private String nextSnapshotCommitMessage = "[release] bumping version: v{0}";

    public void setProject(final Project project) {
        this.project = project;
    }

    public void propertiesFile(final String path) {
        this.propertiesFile = path;
    }

    public String getPropertiesFile() {
        if (propertiesFile == null) {
            propertiesFile = "build.properties";
        }
        final File file = project.file(propertiesFile);
        if (!file.exists()) {
            throw new IllegalStateException(file + " does not exist");
        }
        return propertiesFile;
    }

    public void versionProperty(final String versionProperty) {
        this.versionProperty = versionProperty;
    }

    public String getVersionProperty() {
        return versionProperty;
    }

    public String getPreTagCommitMessage() {
        return preTagCommitMessage;
    }

    public void preTagCommitMessage(final String preTagCommitMessage) {
        this.preTagCommitMessage = preTagCommitMessage;
    }

    public String getTagFormat() {
        return tagFormat;
    }

    public void tagFormat(final String tagFormat) {
        this.tagFormat = tagFormat;
    }

    public String getNextSnapshotCommitMessage() {
        return nextSnapshotCommitMessage;
    }

    public void nextSnapshotCommitMessage(final String nextSnapshotCommitMessage) {
        this.nextSnapshotCommitMessage = nextSnapshotCommitMessage;
    }
}
