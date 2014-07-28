package co.tomlee.gradle.plugins.release.tasks;

import co.tomlee.gradle.plugins.release.ReleaseConvention;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.text.MessageFormat;

import static co.tomlee.gradle.plugins.release.tasks.TaskHelpers.*;

public class RemoveSnapshotSuffixTask extends DefaultTask {
    @TaskAction
    public void removeSnapshotSuffix() throws Exception {
        String version = getThisVersion(getProject());
        if (version.endsWith("-SNAPSHOT")) {
            version = getThisVersionWithoutSnapshot(getProject());
            setVersion(getProject(), version);

            final ReleaseConvention releaseConvention = releaseConvention(getProject());
            final String commitMessage =
                MessageFormat.format(releaseConvention.getPreTagCommitMessage(), version);
            commitPropertiesFile(getProject(), commitMessage);
        }
    }
}
