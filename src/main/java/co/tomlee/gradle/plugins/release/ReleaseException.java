package co.tomlee.gradle.plugins.release;

import org.gradle.api.GradleException;

public class ReleaseException extends GradleException {
    public ReleaseException() {
        super();
    }

    public ReleaseException(String message) {
        super(message);
    }

    public ReleaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
