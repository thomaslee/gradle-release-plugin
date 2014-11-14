package co.tomlee.gradle.plugins.release;

import org.gradle.api.Project;

import java.io.File;

public class ReleaseConvention {
    private Project project;

    private File propertiesFile;
    private String versionProperty = "version";
    private String preTagCommitMessage = "[release] cutting release of {1}: {0}";
    private String tagFormat = "v{0}";
    private String nextSnapshotCommitMessage = "[release] bumping version of {1}: {0}";
    private boolean usingSnapshots = true;
    private boolean ensureWorkspaceClean = true;

    public void setProject(final Project project) {
        this.project = project;
    }

    public void propertiesFile(final Object path) {
        this.propertiesFile = project.file(path);
    }

    public File getPropertiesFile() {
        if (propertiesFile == null) {
            propertiesFile = project.file("gradle.properties");
        }

        if (!propertiesFile.exists()) {
            throw new IllegalStateException(propertiesFile.getAbsolutePath() + " does not exist");
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

    public String getTagFormat() {
        return tagFormat;
    }

    public void tagFormat(final String tagFormat) {
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

    public boolean isEnsureWorkspaceClean() {
        return ensureWorkspaceClean;
    }

    public void ensureWorkspaceClean(final boolean ensureWorkspaceClean) {
        this.ensureWorkspaceClean = ensureWorkspaceClean;
    }
}
