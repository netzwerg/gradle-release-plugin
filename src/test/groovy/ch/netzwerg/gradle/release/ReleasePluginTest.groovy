package ch.netzwerg.gradle.release

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertNotNull

class ReleasePluginTest {

    private Project project
    private ReleasePlugin plugin

    @Before
    public void before() {
        project = ProjectBuilder.builder().build()
        plugin = new ReleasePlugin()
        plugin.apply(project)
    }

    @Test
    public void apply() {
        assertNotNull(project.extensions.findByName(ReleasePlugin.RELEASE_EXTENSION_NAME))
        assertNotNull(project.tasks.findByName(ReleasePlugin.RELEASE_TASK_NAME))
    }

}