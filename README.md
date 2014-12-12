gradle-release-plugin
=====================

Gradle plugin providing very minimal release version numbering.

Provides a `release` task which:

* Reads version from `version.txt` (e.g. `0.0.1-SNAPSHOT`)
* Removes potential `-SNAPSHOT` postfix and commits modified `version.txt` (e.g. `0.0.1`)
* Tags Git repo (e.g. `v0.0.1`)
* Commits a modified `version.txt` with next snapshot version (e.g. `0.0.2-SNAPSHOT`)
* Pushes changes to Git remote

# Usage

Since the plugin is still in a very basic state, it is not yet published on Bintray. Feel free to build it
locally and include it as a jar (see `build.gradle` for an example, this plugin project is eating its own dog food).

# License

This plugin is available under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).