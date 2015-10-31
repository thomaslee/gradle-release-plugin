package co.tomlee.gradle.plugins.release;

import co.tomlee.gradle.plugins.release.tasks.*;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class ReleasePlugin implements Plugin<Project> {
    @Override
    public void apply(final Project project) {
        final ReleaseConvention releaseConvention = project.getConvention().create("release", ReleaseConvention.class);
        releaseConvention.setProject(project);

        final ReleaseEnsureCleanWorkspaceTask releaseEnsureCleanWorkspaceTask =
                project.getTasks().create("releaseEnsureCleanWorkspace", ReleaseEnsureCleanWorkspaceTask.class);
        final ReleaseBeginTransactionTask releaseBeginTransactionTask =
                project.getTasks().create("releaseBeginTransaction", ReleaseBeginTransactionTask.class);
        releaseBeginTransactionTask.dependsOn(releaseEnsureCleanWorkspaceTask);
        final ReleaseVersionTask releaseVersionTask =
                project.getTasks().create("releaseVersion", ReleaseVersionTask.class);
        releaseVersionTask.dependsOn(releaseBeginTransactionTask);
        final ReleaseTagTask releaseTagTask =
                project.getTasks().create("releaseTag", ReleaseTagTask.class);
        releaseTagTask.dependsOn(releaseVersionTask);
        final ReleaseNextVersionTask releaseNextVersionTask =
                project.getTasks().create("releaseNextVersion", ReleaseNextVersionTask.class);
        releaseNextVersionTask.dependsOn(releaseTagTask);
        final ReleaseEndTransactionTask releaseCommitTask =
                project.getTasks().create("releaseEndTransaction", ReleaseEndTransactionTask.class);
        releaseCommitTask.dependsOn(releaseNextVersionTask);

        final Task releaseTask = project.getTasks().create("release").dependsOn(releaseCommitTask);
        releaseTask.setGroup("Release");
        releaseTask.setDescription("Cut a release from HEAD");

        final Task rollbackTask = project.getTasks().create("releaseRollback", ReleaseRollbackTransactionTask.class);
        rollbackTask.setGroup("Release");
        rollbackTask.setDescription("Rollback a failed release");
    }

}
