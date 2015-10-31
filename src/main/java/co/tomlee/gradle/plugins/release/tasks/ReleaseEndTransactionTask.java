package co.tomlee.gradle.plugins.release.tasks;

import org.eclipse.jgit.api.Git;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

import static co.tomlee.gradle.plugins.release.tasks.TaskHelpers.*;

public class ReleaseEndTransactionTask extends DefaultTask {
    private final Logger log = Logging.getLogger(ReleaseEndTransactionTask.class);

    @TaskAction
    public void endTransaction() throws Exception {
        final Git git = new Git(repository(getProject()));
        try {
            final File transactionFile =
                new File(git.getRepository().getDirectory().getParentFile(), ".releaseTransaction");
            if (transactionFile.exists() && !transactionFile.delete()) {
                log.error("Failed to delete transaction file: " + transactionFile);
            }
        }
        finally {
            git.close();
        }
    }
}
