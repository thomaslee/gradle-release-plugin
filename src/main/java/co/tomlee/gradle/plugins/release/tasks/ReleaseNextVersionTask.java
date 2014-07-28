package co.tomlee.gradle.plugins.release.tasks;

import co.tomlee.gradle.plugins.release.ReleaseConvention;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.text.MessageFormat;

import static co.tomlee.gradle.plugins.release.tasks.TaskHelpers.*;

public class ReleaseNextVersionTask extends DefaultTask {
    @TaskAction
    public void updateVersion() throws Exception {
        String version = findProperty(getProject(), "gradle.release.nextVersion", null);
        final String nextVersion;
        if (StringUtils.isEmpty(version)) {
            version = getVersion(getProject());
            final String[] parts = version.split("\\.");
            final int last = Integer.parseInt(parts[parts.length - 1]);
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                sb.append(parts[i]).append(".");
            }
            sb.append(last + 1);
            sb.append("-SNAPSHOT");
            nextVersion = sb.toString();
        }
        else {
            nextVersion = version;
        }
        setVersion(getProject(), nextVersion);

        final ReleaseConvention releaseConvention = releaseConvention(getProject());
        final String commitMessage =
            MessageFormat.format(releaseConvention.getNextVersionCommitMessageFormat(), nextVersion);
        commitPropertiesFile(getProject(), commitMessage);
    }
}
