package co.tomlee.gradle.plugins.release.tasks;

import co.tomlee.gradle.plugins.release.ReleaseConvention;
import co.tomlee.gradle.plugins.release.ReleaseException;
import co.tomlee.gradle.plugins.release.ReleasePlugin;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.gradle.api.GradleException;
import org.gradle.api.Project;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;
import java.util.regex.Pattern;

public final class TaskHelpers {
    private TaskHelpers() {}

    public static String findProperty(final Project project, final String propertyName, final String defaultValue) {
        String value = System.getProperty(propertyName);
        if (StringUtils.isEmpty(value)) {
            value = (String) project.getProperties().get(propertyName);
            if (StringUtils.isEmpty(value)) {
                return defaultValue;
            }
            return value;
        }
        return value;
    }

    public static ReleaseConvention releaseConvention(final Project project) {
        return (ReleaseConvention) project.getConvention().getByName("release");
    }

    public static String getVersion(final Project project) throws IOException {
        final ReleaseConvention releaseConvention = releaseConvention(project);

        String version = findProperty(project, "gradle.release.version", null);
        if (StringUtils.isEmpty(version)) {
            final File propertiesFile = project.file(releaseConvention.getPropertiesFile());
            final Properties properties = new Properties();
            try (final FileReader reader = new FileReader(propertiesFile)) {
                properties.load(reader);
            }
            version = properties.getProperty(releaseConvention.getVersionProperty());
            if (StringUtils.isEmpty(version)) {
                throw new IllegalStateException("No version number found in " + propertiesFile);
            }
        }
        return version;
    }

    public static String getVersionWithoutSnapshot(final Project project) throws IOException {
        final String version = getVersion(project);

        if (version.endsWith("-SNAPSHOT")) {
            return version.substring(0, version.length()-9);
        }
        return version;
    }

    public static void setVersion(final Project project, final String newVersion) throws IOException {
        final ReleaseConvention releaseConvention = releaseConvention(project);

        final File propertiesFile = releaseConvention.getPropertiesFile();

        project.setVersion(newVersion);

        try {
            project.getAnt().invokeMethod("replaceregexp", ImmutableMap.<String, Object>of(
                    "file", propertiesFile,
                    "match", "^(\\s*)" + Pattern.quote(releaseConvention.getVersionProperty()) + "(\\s*)=(\\s*).*",
                    "replace", "\\1" + releaseConvention.getVersionProperty() + "\\2=\\3" + newVersion,
                    "byline", true));
        }
        catch (BuildException e) {
            throw new GradleException("Unable to set new version in properties file", e);
        }
    }

    public static void commitPropertiesFile(final Project project, final String commitMessage) throws Exception {
        final Git git = new Git(repository(project));
        try {
            final ReleaseConvention releaseConvention = releaseConvention(project);
            final File propertiesFile = releaseConvention.getPropertiesFile();
            final Path relativePath = project.getRootDir().toPath().relativize(propertiesFile.toPath());
            git.commit()
                .setOnly(relativePath.toString())
                .setMessage(commitMessage)
                .call();
        }
        finally {
            git.close();
        }
    }

    public static Repository repository(final Project project) {
        try {
            final File projectDir = project.getProjectDir();
            final FileRepositoryBuilder repoBuilder = new FileRepositoryBuilder().readEnvironment().findGitDir(projectDir);
            if (repoBuilder.getGitDir() == null) {
                throw new ReleaseException("No git directory found!");
            }
            return repoBuilder.build();
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
