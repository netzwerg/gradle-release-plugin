gradle-release-plugin [![Build Status](https://travis-ci.org/netzwerg/gradle-release-plugin.svg?branch=master)](https://travis-ci.org/netzwerg/gradle-release-plugin)
=====================

Gradle plugin providing very minimal release version numbering.

In contrast to other release plugins which offer a wealth of features and configuration
options, this plugin is intentionally kept simple.

Additional functionality can be added via composition:

* [gradle-release-pub-plugin](https://github.com/netzwerg/gradle-release-pub-plugin): Adds release publication
channels, e.g. to create a GitHub release via official REST API

# Introduction

The plugin assumes a `MAJOR.MINOR.PATCH[-SNAPSHOT]` version pattern kept in a `version.txt` file.

It does two things:

* Initialization of `project.version` from `version.txt` file
* Creation of tagged non-SNAPSHOT release (including preparation of `version.txt` for next `SNAPSHOT` iteration)

Because the plugin only increments the `PATCH` portion of the version, it runs non-interactively and is well suited
for continuous integration.

# Details

When applied, the plugin initializes the `project.version` according to the contents of a `version.txt` file (e.g.
`0.0.1-SNAPSHOT`).

In addition, it provides a `release` task which:

(During configuration phase)

* Removes potential `-SNAPSHOT` postfix (e.g. `0.0.1`)
* Updates `project.version` accordingly
* Updates `version.txt` accordingly

(During execution phase)

* Commits modified `version.txt`
* Tags Git repo (e.g. `v0.0.1`)
* Increments version number in `version.txt` to next SNAPSHOT (e.g. `0.0.2-SNAPSHOT`)
* Again commits modified `version.txt`
* Optionally pushes changes to Git remote

**Important**: The configuration phase is only executed if the `release` task is explicitly passed to Gradle as a
start parameter.

# Usage

1. Create a `version.txt` (with e.g. `0.1.0-SNAPSHOT`) right next to your build script
2. Apply the plugin in your build script (see below)
3. Call `gradle clean release`

# Applying the plugin

The plugin is hosted on [Bintray](https://bintray.com/netzwerg/gradle-plugins/gradle-release-plugin) and can be applied
as follows:

## New plugin mechanism (as of Gradle 2.1):

```groovy
plugins {
  id 'ch.netzwerg.release' version 'x.y.z'
}
```

## Older Gradle versions:

```groovy
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath 'ch.netzwerg:gradle-release-plugin:x.y.z'
  }
}
apply plugin: 'ch.netzwerg.release'
```

# Configuration

This plugin follows a "no surprise" policy. Consequently, no configuration is needed in the majority of cases. The
following snippet illustrates all configuration options, their default values, and possible alternatives:

```groovy
release {
  dependsOn build // 'distZip' could e.g be used in combination with the 'application' plugin
  push = false // 'true' would e.g. be useful when triggering the release task on a CI server
  versionSuffix = '-SNAPSHOT' // '.DEV' or '' (empty) could be useful alternatives
  tagPrefix = 'v' // 'r' or '' (empty) could be useful alternatives
}
```

# Read-Only Properties

Use `project.release.versionFile` to e.g. include the `version.txt` file in your final release bundle. The VCS release
tag can be accessed via `project.release.tagName`.

# Acknowledgements

* [etiennestuder](https://github.com/etiennestuder) (underlying [plugindev](https://github.com/etiennestuder/gradle-plugindev-plugin))
* [RichardGottschalk](https://github.com/RichardGottschalk) ([PR #16](https://github.com/netzwerg/gradle-release-plugin/pull/16))

# License

This plugin is available under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

&copy; by Rahel Lüthy
