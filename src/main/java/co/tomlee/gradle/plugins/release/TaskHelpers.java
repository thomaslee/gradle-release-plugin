package co.tomlee.gradle.plugins.release;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.eclipse.jgit.lib.Repository;
import org.gradle.api.GradleException;
import org.gradle.api.Project;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

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

    public static ReleaseConvention getReleaseConvention(final Project project) {
        return (ReleaseConvention) project.getConvention().getByName("release");
    }

    public static String getThisVersion(final Project project) throws IOException {
        final ReleaseConvention releaseConvention = getReleaseConvention(project);

        final File propertiesFile = releaseConvention.getPropertiesFile();
        final Properties properties = new Properties();
        final FileReader reader = new FileReader(propertiesFile);
        try {
            properties.load(reader);
        }
        finally {
            reader.close();
        }
        final String version = properties.getProperty(releaseConvention.getVersionProperty());
        if (StringUtils.isEmpty(version)) {
            throw new IllegalStateException("No version number found in " + propertiesFile);
        }
        return version;
    }

    public static void setVersion(final Project project, final String newVersion) throws IOException {
        final ReleaseConvention releaseConvention = getReleaseConvention(project);

        final File propertiesFile = releaseConvention.getPropertiesFile();

        project.setVersion(newVersion);

        try {
            project.getAnt().invokeMethod("replace", ImmutableMap.<String, Object>of(
                    "file", propertiesFile,
                    "token", releaseConvention.getVersionProperty() + "=" + getThisVersion(project),
                    "value", releaseConvention.getVersionProperty() + "=" + newVersion,
                    "failOnNoReplacements", true));
        }
        catch (BuildException e) {
            throw new GradleException("Unable to set new version in properties file");
        }
    }

    public static Repository getGitRepository(final Project project) {
        return (Repository) project.getProperties().get(ReleasePlugin.GIT_REPO_PROPERTY);
    }
}