package co.tomlee.gradle.plugins.release;

import co.tomlee.gradle.plugins.release.tasks.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

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

        final EnsureCleanWorkspaceTask ensureCleanWorkspaceTask =
                project.getTasks().create("releaseEnsureCleanWorkspace", EnsureCleanWorkspaceTask.class);
        final ReleaseBeginTransactionTask releaseBeginTransactionTask =
                project.getTasks().create("releaseBeginTransaction", ReleaseBeginTransactionTask.class);
        releaseBeginTransactionTask.dependsOn(ensureCleanWorkspaceTask);
        final RemoveSnapshotSuffixTask removeSnapshotSuffixTask =
                project.getTasks().create("releaseRemoveSnapshotSuffix", RemoveSnapshotSuffixTask.class);
        removeSnapshotSuffixTask.dependsOn(releaseBeginTransactionTask);
        final ReleaseTagTask releaseTagTask =
                project.getTasks().create("releaseTag", ReleaseTagTask.class);
        releaseTagTask.dependsOn(removeSnapshotSuffixTask);
        final NextSnapshotTask nextSnapshotTask =
                project.getTasks().create("releaseNextSnapshot", NextSnapshotTask.class);
        nextSnapshotTask.dependsOn(releaseTagTask);
        final ReleaseCommitTransactionTask releaseCommitTask =
                project.getTasks().create("releaseCommitTransaction", ReleaseCommitTransactionTask.class);
        releaseCommitTask.dependsOn(nextSnapshotTask);

        project.getTasks().create("release").dependsOn(releaseCommitTask);

        project.getTasks().create("releaseRollback", ReleaseRollbackTransactionTask.class);
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
