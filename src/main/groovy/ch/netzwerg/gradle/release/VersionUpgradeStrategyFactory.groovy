package ch.netzwerg.gradle.release

import org.gradle.api.GradleException

@SuppressWarnings("GroovyAssignabilityCheck")
final class VersionUpgradeStrategyFactory {

    public static VersionInfo parseVersionInfo(String version) {
        def (majorPart, minorPart, patchPart) = version.tokenize('.')

        if (majorPart == null || minorPart == null || patchPart == null) {
            throw new GradleException("Invalid version '$version' (must follow 'major.minor.patch' semantics)")
        }

        int major = parsePart(majorPart, version)
        int minor = parsePart(minorPart, version)
        int patch = parsePart(patchPart, version)

        new VersionInfo(major, minor, patch)
    }

    private static int parsePart(String part, String version) {
        try {
            return Integer.parseInt(part)
        } catch (NumberFormatException nfe) {
            throwParseException(version, nfe, 'major')
        }
    }

    private static void throwParseException(String version, NumberFormatException nfe, String part) {
        throw new GradleException("Invalid version '$version' (could not parse '$part' part as int)", nfe);
    }

    public static VersionUpgradeStrategy createMajorVersionUpgradeStrategy(String versionSuffix) {
        return new VersionUpgradeStrategy() {
            @Override
            String getVersion(String currentVersion) {
                VersionInfo info = parseVersionInfo(currentVersion - versionSuffix)
                (info.major + 1) + ".0.0"
            }
        }
    }

    public static VersionUpgradeStrategy createMinorVersionUpgradeStrategy(String versionSuffix) {
        return new VersionUpgradeStrategy() {
            @Override
            String getVersion(String currentVersion) {
                VersionInfo info = parseVersionInfo(currentVersion - versionSuffix)
                "$info.major." + (info.minor + 1) + ".0"
            }
        }
    }

    public static VersionUpgradeStrategy createPatchVersionUpgradeStrategy(String versionSuffix) {
        return new VersionUpgradeStrategy() {
            @Override
            String getVersion(String currentVersion) {
                currentVersion - versionSuffix
            }
        }
    }

    public static VersionUpgradeStrategy createSnapshotVersionUpgradeStrategy() {
        return new VersionUpgradeStrategy() {
            @Override
            String getVersion(String currentVersion) {
                currentVersion
            }
        }
    }

    public static final class VersionInfo {

        final int major
        final int minor
        final int patch

        private VersionInfo(major, minor, patch) {
            this.major = major
            this.minor = minor
            this.patch = patch
        }

    }

}