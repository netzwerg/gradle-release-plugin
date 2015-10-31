package ch.netzwerg.gradle.release

interface VersionUpgradeStrategy {

    String getVersion(String currentVersion);

}