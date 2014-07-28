package co.tomlee.gradle.plugins.release.tasks;

import co.tomlee.gradle.plugins.release.ReleaseConvention;
import co.tomlee.gradle.plugins.release.ReleaseException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;

import static co.tomlee.gradle.plugins.release.tasks.TaskHelpers.releaseConvention;
import static co.tomlee.gradle.plugins.release.tasks.TaskHelpers.repository;

public class ReleaseEnsureCleanWorkspaceTask extends DefaultTask {
    private static final Logger log = Logging.getLogger(ReleaseEnsureCleanWorkspaceTask.class);

    @TaskAction
    public void release() throws Exception {
        final ReleaseConvention releaseConvention = releaseConvention(getProject());

        if (releaseConvention.isEnsureWorkspaceClean()) {
            final Repository repo = repository(getProject());
            final Git git = new Git(repo);

            if (git.status().call().hasUncommittedChanges()) {
                throw new ReleaseException("The working tree has uncommitted changes");
            }
        }
        else {
            log.info("Skipping check for clean workspace");
        }
    }
}
