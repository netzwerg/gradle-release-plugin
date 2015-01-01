package ch.netzwerg.gradle.release

import ch.netzwerg.gradle.release.pub.PubChannel
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals

public class ReleaseExtensionTest {

    private ReleaseExtension extension

    @Before
    public void before() {
        Project project = ProjectBuilder.builder().build();
        project.version = '1.2.3.DEV'
        extension = new ReleaseExtension(project);
        extension.setSuffix('.DEV')
        extension.pubChannels.registerPubChannelFactory('foo', new NamedDomainObjectFactory<PubChannel>() {
            @Override
            PubChannel create(String name) {
                return new FooPubChannel(name)
            }
        })
        extension.pubChannels.registerPubChannelFactory('bar', new NamedDomainObjectFactory<PubChannel>() {
            @Override
            PubChannel create(String name) {
                return new BarPubChannel(name)
            }
        })
    }

    @Test
    public void publish() {
        extension.publish {
            foo {} // default name
            bar('xxx') {}
            foo('one') // without closure
            bar {}
            foo('two') {}
        }
        def fooChannels = extension.pubChannels.byChannelType('foo')
        assertEquals(3, fooChannels.size())
        def fooChannelIt = fooChannels.iterator()
        assertEquals('default', fooChannelIt.next().name)
        assertEquals('one', fooChannelIt.next().name)
        assertEquals('two', fooChannelIt.next().name)

        def barChannels = extension.pubChannels.byChannelType('bar')
        def barChannelIt = barChannels.iterator()
        assertEquals(2, barChannels.size())
        assertEquals('xxx', barChannelIt.next().name)
        assertEquals('default', barChannelIt.next().name)
    }

    @Test(expected = IllegalArgumentException.class)
    public void publish_failOnDuplicate() {
        extension.publish {
            foo('x') {}
            foo('x')
        }
    }

    @Test
    public void getTagName() {
        assertEquals('v1.2.3', extension.tagName)
    }

    private class FooPubChannel extends PubChannel {
        FooPubChannel(String name) {
            super(name)
        }
    }

    private class BarPubChannel extends PubChannel {
        BarPubChannel(String name) {
            super(name)
        }
    }

}