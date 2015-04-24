package ch.netzwerg.gradle.release

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class ReleasePluginTest {

    private static final String TEST_VERSION_NUMBER = " 1.2.3-SNAPSHOT     " // with whitespace

    private File projectDir
    private Project project
    private ReleasePlugin plugin

    @Before
    public void before() {
        projectDir = File.createTempDir()
        def versionFile = new File(projectDir, 'version.txt')
        versionFile.text = TEST_VERSION_NUMBER

        project = ProjectBuilder.builder().withProjectDir(projectDir).build();

        plugin = new ReleasePlugin()
        plugin.apply(project)
    }

    @After
    public void after() {
        if (projectDir?.exists()) {
            projectDir.delete()
        }
    }

    @Test
    public void apply() {
        assertEquals(TEST_VERSION_NUMBER.trim(), project.version)
        assertNotNull(project.extensions.findByName(ReleasePlugin.RELEASE_EXTENSION_NAME))
        assertNotNull(project.tasks.findByName(ReleasePlugin.RELEASE_TASK_NAME))
        assertNotNull(project.tasks.findByName(ReleasePlugin.RELEASE_MAJOR_VERSION_TASK_NAME))
        assertNotNull(project.tasks.findByName(ReleasePlugin.RELEASE_MINOR_VERSION_TASK_NAME))
    }

}