package co.tomlee.gradle.plugins.release.tasks;

import co.tomlee.gradle.plugins.release.ReleaseConvention;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.lib.Repository;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.MessageFormat;

import static co.tomlee.gradle.plugins.release.tasks.TaskHelpers.*;

public class ReleaseRollbackTransactionTask extends DefaultTask {
    private static final Logger log = Logging.getLogger(ReleaseRollbackTransactionTask.class);

    @TaskAction
    public void rollback() throws Exception {
        final Git git = new Git(repository(getProject()));
        try {
            doRollback(git);
        } finally {
            git.close();
        }
    }

    private void doRollback(final Git git) throws Exception {
        final Repository repo = repository(getProject());
        final ReleaseConvention releaseConvention = releaseConvention(getProject());

        final File transactionFile = new File(repo.getDirectory().getParentFile(), ".releaseTransaction");
        final BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(transactionFile));
        }
        catch (FileNotFoundException e) {
            log.info("No transaction in progress");
            return;
        }
        final String line = reader.readLine();
        if (line == null) {
            log.info("Nothing to do for rollback");
            transactionFile.delete();
            return;
        }
        final String[] parts = line.split(";", 2);
        final String sha = parts[0];
        final String version = parts[1];

        final String tag = MessageFormat.format(releaseConvention.getTagFormat(), version);
        if (repo.resolve(tag) != null) {
            log.info("Deleting tag: " + tag);
            git.tagDelete().setTags(tag).call();
        }

        git.reset().setMode(ResetCommand.ResetType.HARD).setRef(sha).call();

        if (!transactionFile.delete()) {
            log.error("Failed to delete transaction file: " + transactionFile);
        }
    }
}
