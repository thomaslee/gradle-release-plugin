package co.tomlee.gradle.plugins.release;

import org.gradle.api.Project;

import java.io.File;

public class ReleaseConvention {
    private Project project;

    private String propertiesFile = "gradle.properties";
    private String versionProperty = "version";
    private String preTagCommitMessage = "[release] cutting release: v{0}";
    private String tagFormat = "v{0}";
    private String nextSnapshotCommitMessage = "[release] bumping version: v{0}";
    private boolean usingSnapshots = true;

    public void setProject(final Project project) {
        this.project = project;
    }

    public void propertiesFile(final String path) {
        this.propertiesFile = path;
    }

    public String getPropertiesFile() {
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

    public String getThisVersionCommitMessageFormat() {
        return preTagCommitMessage;
    }

    public void thisVersionCommitMessageFormat(final String preTagCommitMessage) {
        this.preTagCommitMessage = preTagCommitMessage;
    }

    public String getReleaseTagFormat() {
        return tagFormat;
    }

    public void releaseTagFormat(final String tagFormat) {
        this.tagFormat = tagFormat;
    }

    public String getNextVersionCommitMessageFormat() {
        return nextSnapshotCommitMessage;
    }

    public void nextVersionCommitMessageFormat(final String nextSnapshotCommitMessage) {
        this.nextSnapshotCommitMessage = nextSnapshotCommitMessage;
    }

    public boolean isUsingSnapshots() {
        return usingSnapshots;
    }

    public void usingSnapshots(final boolean snapshot) {
        this.usingSnapshots = snapshot;
    }
}
