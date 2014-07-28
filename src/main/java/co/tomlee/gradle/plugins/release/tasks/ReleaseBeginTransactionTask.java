package co.tomlee.gradle.plugins.release.tasks;

import org.eclipse.jgit.api.Git;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import static co.tomlee.gradle.plugins.release.tasks.TaskHelpers.getThisVersionWithoutSnapshot;
import static co.tomlee.gradle.plugins.release.tasks.TaskHelpers.git;

public class ReleaseBeginTransactionTask extends DefaultTask {
    @TaskAction
    public void tag() throws Exception {
        final Git git = git(getProject());

        final String sha = git.getRepository().resolve("HEAD").name();

        final File transactionFile =
            new File(git.getRepository().getDirectory().getParentFile(), ".releaseTransaction");
        final PrintWriter out = new PrintWriter(new FileWriter(transactionFile));
        final String version = getThisVersionWithoutSnapshot(getProject());
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
