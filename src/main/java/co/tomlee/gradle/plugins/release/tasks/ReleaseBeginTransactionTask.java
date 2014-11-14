package co.tomlee.gradle.plugins.release.tasks;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import static co.tomlee.gradle.plugins.release.tasks.TaskHelpers.getVersionWithoutSnapshot;
import static co.tomlee.gradle.plugins.release.tasks.TaskHelpers.git;

public class ReleaseBeginTransactionTask extends DefaultTask {
    @TaskAction
    public void tag() throws Exception {
        final Git git = git(getProject());

        final Repository repository = git.getRepository();
        if (repository == null) {
            throw new GradleException("Failed to retrieve git repository");
        }

        final ObjectId head = repository.resolve("HEAD");
        if (head == null) {
            throw new GradleException("Failed to resolve git HEAD");
        }

        final String sha = head.name();

        final File transactionFile =
            new File(git.getRepository().getDirectory().getParentFile(), ".releaseTransaction");
        final PrintWriter out = new PrintWriter(new FileWriter(transactionFile));
        final String version = getVersionWithoutSnapshot(getProject());
        try {
            out.write(sha);
            out.write(";");
            out.write(version);
        }
        finally {
            out.close();
        }
    }
}
