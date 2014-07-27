package co.tomlee.gradle.plugins.release;

import co.tomlee.gradle.plugins.release.steps.EnsureCleanWorkspaceStep;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class EnsureCleanWorkspaceTask extends DefaultTask {
    @TaskAction
    public void release() throws Exception {
        new EnsureCleanWorkspaceStep(this).execute();
    }
}
