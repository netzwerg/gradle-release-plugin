package ch.netzwerg.gradle.release

import ch.netzwerg.gradle.release.pub.Publication
import ch.netzwerg.gradle.release.pub.PublicationFactory
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
        PublicationFactory factory = new PublicationFactory();
        NamedDomainObjectContainer<Publication> publications = project.container(Publication.class, factory);
        extension = new ReleaseExtension(publications, factory);
    }

    @Test
    public void getPublicationFactory() {
        assertNotNull(extension.getPublicationFactory())
    }

    @Test
    public void publications() {
        extension.publications {
            foo
            githubOne
            githubTwo
            bar
        }
        assertEquals(1, extension.getPublicationsByNamePrefix('bar').size())
        assertEquals(2, extension.getPublicationsByNamePrefix('github').size())
        assertEquals(0, extension.getPublicationsByNamePrefix('baz').size())
    }

}