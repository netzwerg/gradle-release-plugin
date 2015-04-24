package ch.netzwerg.gradle.release

import org.gradle.api.GradleException
import org.junit.Test

import static org.junit.Assert.assertEquals

class VersionUpgradeStrategyFactoryTest {

    @Test
    public void parseVersionInfo() {
        def info = VersionUpgradeStrategyFactory.parseVersionInfo("1.2.3")
        assertEquals(1, info.major)
        assertEquals(2, info.minor)
        assertEquals(3, info.patch)
    }

    @Test(expected = GradleException)
    public void parseVersionInfo_wrongPattern() {
        VersionUpgradeStrategyFactory.parseVersionInfo("42")
    }

    @Test
    public void createMajorVersionUpgradeStrategy() {
        def strategy = VersionUpgradeStrategyFactory.createMajorVersionUpgradeStrategy();
        assertEquals("2.0.0", strategy.getReleaseVersion("1.2.3"))
    }

    @Test
    public void createMinorVersionUpgradeStrategy() {
        def strategy = VersionUpgradeStrategyFactory.createMinorVersionUpgradeStrategy();
        assertEquals("1.3.0", strategy.getReleaseVersion("1.2.3"))
    }

    @Test
    public void createCurrentVersionUpgradeStrategy() {
        def strategy = VersionUpgradeStrategyFactory.createCurrentVersionUpgradeStrategy();
        assertEquals("1.2.3", strategy.getReleaseVersion("1.2.3"))
    }

}
