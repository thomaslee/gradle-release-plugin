package co.tomlee.gradle.plugins.release;

import co.tomlee.gradle.plugins.release.tasks.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.File;
import java.io.IOException;

public class ReleasePlugin implements Plugin<Project> {
    public static final String GIT_REPO_PROPERTY = "release.internal.git.repository";
    public static final String GIT_PROPERTY = "release.internal.git.instance";

    @Override
    public void apply(final Project project) {
        final ReleaseConvention releaseConvention = project.getConvention().create("release", ReleaseConvention.class);
        releaseConvention.setProject(project);

        // TODO automatically wire up the nexus & maven plugins?

        final Repository repo = buildRepository(project);
        final Git git = new Git(repo);

        project.getExtensions().add(GIT_REPO_PROPERTY, repo);
        project.getExtensions().add(GIT_PROPERTY, git);

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
        releaseTask.setDescription("Cut a release from HEAD");

        final Task rollbackTask = project.getTasks().create("releaseRollback", ReleaseRollbackTransactionTask.class);
        rollbackTask.setDescription("Rollback a failed release");
    }

    private final Repository buildRepository(final Project project) {
        try {
            final File projectDir = project.getProjectDir();
            final FileRepositoryBuilder repoBuilder = new FileRepositoryBuilder().readEnvironment().findGitDir(projectDir);
            if (repoBuilder.getGitDir() == null) {
                throw new ReleaseException("No git directory found!");
            }
            return repoBuilder.build();
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
