package co.tomlee.gradle.plugins.release;

import co.tomlee.gradle.plugins.release.steps.RemoveSnapshotSuffixStep;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class RemoveSnapshotSuffixTask extends DefaultTask {
    @TaskAction
    public void removeSnapshotSuffix() throws Exception {
        new RemoveSnapshotSuffixStep(this).execute();
    }
}
