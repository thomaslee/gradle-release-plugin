package co.tomlee.gradle.plugins.release.steps;

import co.tomlee.gradle.plugins.release.RemoveSnapshotSuffixTask;
import org.gradle.api.Project;

import static co.tomlee.gradle.plugins.release.TaskHelpers.getThisVersion;
import static co.tomlee.gradle.plugins.release.TaskHelpers.setVersion;

public class RemoveSnapshotSuffixStep {
    // private final Logger log = Logging.getLogger(RemoveSnapshotSuffixStep.class);

    private final RemoveSnapshotSuffixTask removeSnapshotSuffixTask;

    public RemoveSnapshotSuffixStep(final RemoveSnapshotSuffixTask removeSnapshotSuffixTask) {
        this.removeSnapshotSuffixTask = removeSnapshotSuffixTask;
    }

    public void execute() throws Exception {
        String version = getThisVersion(getProject());
        if (version.endsWith("-SNAPSHOT")) {
            version = version.substring(0, version.length() - 9);
            setVersion(getProject(), version);
        }
    }

    private Project getProject() {
        return removeSnapshotSuffixTask.getProject();
    }
}
