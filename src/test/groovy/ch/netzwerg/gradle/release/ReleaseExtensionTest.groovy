package ch.netzwerg.gradle.release

import ch.netzwerg.gradle.release.pub.PubChannel
import ch.netzwerg.gradle.release.pub.PubChannelFactory
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull;

public class ReleaseExtensionTest {

    private ReleaseExtension extension

    @Before
    public void before() {
        Project project = ProjectBuilder.builder().build();
        project.version = '1.2.3.DEV'
        PubChannelFactory factory = new PubChannelFactory();
        NamedDomainObjectContainer<PubChannel> publications = project.container(PubChannel.class, factory);
        extension = new ReleaseExtension(project, publications, factory);
        extension.setSuffix('.DEV')
    }

    @Test
    public void getPublicationFactory() {
        assertNotNull(extension.getPubChannelFactory())
    }

    @Test
    public void publications() {
        extension.publish {
            foo
            githubOne
            githubTwo
            bar
        }
        assertEquals(1, extension.getPubChannelsByNamePrefix('bar').size())
        assertEquals(2, extension.getPubChannelsByNamePrefix('github').size())
        assertEquals(0, extension.getPubChannelsByNamePrefix('baz').size())
    }

    @Test
    public void getTagName() {
        assertEquals('v1.2.3', extension.tagName)
    }

}