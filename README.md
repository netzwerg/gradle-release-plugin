gradle-release-plugin
=====================

Gradle plugin providing very minimal release version numbering.

Provides a `release` task which:

(During configuration phase)

* Reads version from `version.txt` (e.g. `0.0.1-SNAPSHOT`)
* Removes potential `-SNAPSHOT` postfix (e.g. `0.0.1`)
* Sets `project.version`

(During execution phase)

* Commits modified `version.txt`
* Tags Git repo (e.g. `v0.0.1`)
* Commits a modified `version.txt` with next SNAPSHOT version (e.g. `0.0.2-SNAPSHOT`)
* Pushes changes to Git remote

# Usage

Since the plugin is still in a very basic state, it is not yet published on Bintray. Feel free to build it
locally and include it as a jar (see `build.gradle` for an example, this plugin project is eating its own dog food).

# Acknowledgements

Uses Etienne Studer's excellent [plugindev](https://github.com/etiennestuder/gradle-plugindev-plugin).

# License

This plugin is available under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

(c) by Rahel Lüthy