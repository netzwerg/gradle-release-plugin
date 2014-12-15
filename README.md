gradle-release-plugin
=====================

Gradle plugin providing very minimal release version numbering.

# Introduction

The plugin assumes a `MAJOR.MINOR.PATCH` version pattern kept in a `version.txt` file.
In contrast to other release plugins which offer a wealth of features and configuration
options, this plugin is intentionally kept simple.

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

Because the `version.txt` file is well suited to be included in your final release bundle, the plugin exposes a
handle to it via the `project.ext.versionFile` property.

# Usage

The plugin is hosted on [Bintray](https://bintray.com/netzwerg/gradle-plugins/gradle-release-plugin) and can be applied as follows (Gradle 2.1 and higher):

```groovy
plugins {
  id 'ch.netzwerg.release' version '0.1.0'
}
```

# Configuration

```groovy
release {
    dependsOn build
    push = true // e.g. useful when triggering release task on CI server
}
```

# Acknowledgements

Uses Etienne Studer's excellent [plugindev](https://github.com/etiennestuder/gradle-plugindev-plugin) plugin.

# License

This plugin is available under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

(c) by Rahel Lüthy
