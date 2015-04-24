package ch.netzwerg.gradle.release

interface VersionUpgradeStrategy {

    String getReleaseVersion(String currentVersion);

}