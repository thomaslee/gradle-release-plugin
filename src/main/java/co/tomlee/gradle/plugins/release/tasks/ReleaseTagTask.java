package co.tomlee.gradle.plugins.release.tasks;

import co.tomlee.gradle.plugins.release.ReleaseConvention;
import org.eclipse.jgit.api.Git;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.text.MessageFormat;

import static co.tomlee.gradle.plugins.release.tasks.TaskHelpers.*;

public class ReleaseTagTask extends DefaultTask {
    @TaskAction
    public void tag() throws Exception {
        final ReleaseConvention releaseConvention = releaseConvention(getProject());
        final Git git = new Git(repository(getProject()));
        try {
            git.tag().setName(MessageFormat.format(releaseConvention.getTagFormat(), getVersion(getProject()))).call();
        }
        finally {
            git.close();
        }
    }
}
