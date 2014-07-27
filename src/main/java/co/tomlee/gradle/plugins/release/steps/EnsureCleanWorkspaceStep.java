package co.tomlee.gradle.plugins.release.steps;

import co.tomlee.gradle.plugins.release.EnsureCleanWorkspaceTask;
import co.tomlee.gradle.plugins.release.ReleaseException;
import org.eclipse.jgit.lib.IndexDiff;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.gradle.api.Project;

import java.io.File;

import static co.tomlee.gradle.plugins.release.TaskHelpers.*;

public class EnsureCleanWorkspaceStep {
    private final EnsureCleanWorkspaceTask ensureCleanWorkspaceTask;

    public EnsureCleanWorkspaceStep(final EnsureCleanWorkspaceTask ensureCleanWorkspaceTask) {
        this.ensureCleanWorkspaceTask = ensureCleanWorkspaceTask;
    }

    public void execute() throws Exception {
        final Repository repo = getGitRepository(getProject());

        if (repo.getRepositoryState().isRebasing()) {
            throw new ReleaseException("Rebase is in-progress: cannot release!");
        }

        final Ref ref = repo.getRef("HEAD");
        final FileTreeIterator iter = new FileTreeIterator(repo);
        final IndexDiff indexDiff = new IndexDiff(repo, ref.getObjectId(), iter);

        boolean anyChanges = !indexDiff.getModified().isEmpty();
        anyChanges |= !indexDiff.getRemoved().isEmpty();
        anyChanges |= !indexDiff.getAdded().isEmpty();
        anyChanges |= !indexDiff.getChanged().isEmpty();
        anyChanges |= !indexDiff.getMissing().isEmpty();

        if (anyChanges) {
            throw new ReleaseException("The working tree is not clean");
        }
    }

    private Project getProject() {
        return ensureCleanWorkspaceTask.getProject();
    }
}
