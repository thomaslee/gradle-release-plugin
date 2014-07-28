package co.tomlee.gradle.plugins.release.tasks;

import co.tomlee.gradle.plugins.release.ReleaseConvention;
import org.eclipse.jgit.api.Git;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.text.MessageFormat;

import static co.tomlee.gradle.plugins.release.tasks.TaskHelpers.*;

public class ReleaseVersionTask extends DefaultTask {
    @TaskAction
    public void removeSnapshotSuffix() throws Exception {
        String version = getVersion(getProject());
        final String commitVersion;
        if (version.endsWith("-SNAPSHOT")) {
            commitVersion = getVersionWithoutSnapshot(getProject());
        }
        else {
            commitVersion = version;
        }
        setVersion(getProject(), commitVersion);

        final Git git = git(getProject());
        if (git.status().call().hasUncommittedChanges()) {
            final ReleaseConvention releaseConvention = releaseConvention(getProject());
            final String commitMessage =
                MessageFormat.format(releaseConvention.getThisVersionCommitMessageFormat(), version);
            commitPropertiesFile(getProject(), commitMessage);
        }
    }
}
