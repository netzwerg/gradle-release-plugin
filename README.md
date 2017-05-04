gradle-release-plugin [![Build Status](https://travis-ci.org/netzwerg/gradle-release-plugin.svg?branch=master)](https://travis-ci.org/netzwerg/gradle-release-plugin) [ ![Download](https://api.bintray.com/packages/netzwerg/gradle-plugins/gradle-release-plugin/images/download.svg) ](https://bintray.com/netzwerg/gradle-plugins/gradle-release-plugin/_latestVersion)
=====================

Gradle plugin providing very minimal release version numbering.

The plugin assumes a `MAJOR.MINOR.PATCH[-SNAPSHOT]` version pattern kept in a `version.txt` file.

It provides the following functionality:
* Initializes `project.version` from `version.txt`
* Offers tasks for different release strategies:
  * `release` - Creates a tagged non-SNAPSHOT release (using the current version as specified in the `version.txt` file)
  * `releaseMajorVersion` - Upgrades to next major version & creates a tagged non-SNAPSHOT release.
  * `releaseMinorVersion` - Upgrades to next minor version & creates a tagged non-SNAPSHOT release.
* Prepares `version.txt` for next SNAPSHOT iteration (i.e. bumps PATCH portion)

# Usage

**IMPORTANT:** In multi-module builds, these steps should only be performed on the **root project**

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

# Details

During the **application** phase, the plugin initializes the `project.version` according to the contents of a
`version.txt` file (e.g. `1.2.3-SNAPSHOT`).

During the **configuration** phase, the plugin checks if any of the `releaseXXX` tasks is called **explicitly**. It
then upgrades the `project.version` and `version.txt` contents according to the following strategies:

* Task `release`: Remove `-SNAPSHOT` from current version (e.g. `1.2.3`)
* Task `releaseMajorVersion`: Upgrade to next major version (e.g. `2.0.0`)
* Task `releaseMinorVersion`: Upgrade to next minor version (e.g. `1.3.0`)

During the **execution** phase, the `releaseXXX` tasks tag the Git repository and prepare the `version.txt` contents
for the next `SNAPSHOT` iteration. The tasks perform the following steps:

* Commit modified `version.txt`
* Tag Git repo (e.g. `v1.2.3`)
* Increment version number in `version.txt` to next SNAPSHOT (e.g. `1.2.4-SNAPSHOT`)
* Again commit modified `version.txt`
* Optionally push changes to Git remote (works from any branch)

Note that all `releaseXXX` tasks run non-interactively and are thus well suited for continuous integration.

# Configuration

This plugin follows a "no surprise" policy. Consequently, no configuration is needed in the majority of cases. The
following snippet illustrates all configuration options, their default values, and possible alternatives:

```groovy
release {
  dependsOn build // 'distZip' could e.g be used in combination with the 'application' plugin
  push = false // 'true' would e.g. be useful when triggering the release task on a CI server
  versionSuffix = '-SNAPSHOT' // '.DEV' or '' (empty) could be useful alternatives
  tagPrefix = 'v' // 'r' or '' (empty) could be useful alternatives
  pushToBranchPrefix = null // empty or null results in pushing to current branch
                            // nonempty creates new branch before push with name format: "$pushToBranchPrefix-$version-$versionSuffix"
}
```

**Note:** In multi-projects scenarios, the root project usually does not have a `build` task. Consequently, the
`release` task will fail with its current default settings. Please use `release.dependsOn subprojects.build`
(**after** the `subprojects` block) to work around
[this issue](https://github.com/netzwerg/gradle-release-plugin/issues/19). 

# Read-Only Properties

Use `project.release.versionFile` to e.g. include the `version.txt` file in your final release bundle. The Git release
tag can be accessed via `project.release.tagName`.

# Acknowledgements

* [etiennestuder](https://github.com/etiennestuder) (underlying [plugindev](https://github.com/etiennestuder/gradle-plugindev-plugin))
* [RichardGottschalk](https://github.com/RichardGottschalk) ([PR #16](https://github.com/netzwerg/gradle-release-plugin/pull/16))

# License

This plugin is available under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

&copy; by Rahel Lüthy
