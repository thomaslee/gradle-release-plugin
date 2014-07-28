# gradle-release-plugin

## Status

Beta

## Overview

Yet another Gradle plugin for cutting release tags.
Only supports Git right now.

## Usage

    apply plugin: 'java'
    apply plugin: 'release'

    buildscript {
        repositories {
            mavenCentral()
        }

        dependencies {
            classpath 'co.tomlee.gradle.plugins:gradle-release-plugin:0.0.1'
        }
    }

Once everything's wired up, run `gradle release`. If anything goes wrong
during the release, run `gradle releaseRollback` to clean up tags & reset
HEAD to its location before the release started.

## License

Apache 2.0

## Support

Please log defects and feature requests using the issue tracker on [github](http://github.com/thomaslee/gradle-release-plugin/issues).

## About

gradle-release-plugin was written by [Tom Lee](http://tomlee.co).

Follow me on [Twitter](http://www.twitter.com/tglee) or
[LinkedIn](http://au.linkedin.com/pub/thomas-lee/2/386/629).


