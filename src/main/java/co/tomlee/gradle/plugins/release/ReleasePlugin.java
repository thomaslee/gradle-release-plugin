package co.tomlee.gradle.plugins.release;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;

public class ReleasePlugin implements Plugin<Project> {
    protected static final String GIT_REPO_PROPERTY = "release.internal.git.repository";

    @Override
    public void apply(final Project project) {
        final ReleaseConvention releaseConvention = project.getConvention().create("release", ReleaseConvention.class);
        releaseConvention.setProject(project);

        // TODO automatically wire up the nexus & maven plugins?

        project.setProperty(GIT_REPO_PROPERTY, buildRepository(project));

        final EnsureCleanWorkspaceTask ensureCleanWorkspaceTask =
                project.getTasks().create("releaseEnsureCleanWorkspace", EnsureCleanWorkspaceTask.class);
        final RemoveSnapshotSuffixTask removeSnapshotSuffixTask =
                project.getTasks().create("releaseRemoveSnapshotSuffix", RemoveSnapshotSuffixTask.class);
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
