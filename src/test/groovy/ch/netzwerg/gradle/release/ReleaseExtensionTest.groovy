package ch.netzwerg.gradle.release

import ch.netzwerg.gradle.release.pub.PubChannel
import ch.netzwerg.gradle.release.pub.PubChannelFactory
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import java.util.concurrent.atomic.AtomicInteger

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

public class ReleaseExtensionTest {

    private ReleaseExtension extension
    private FooPubChannelFactory fooPubChannelFactory
    private BarPubChannelFactory barPubChannelFactory

    @Before
    public void before() {
        Project project = ProjectBuilder.builder().build();
        project.version = '1.2.3.DEV'
        extension = new ReleaseExtension(project);
        extension.setVersionSuffix('.DEV')

        fooPubChannelFactory = new FooPubChannelFactory()
        extension.pubChannels.registerPubChannelFactory('foo', fooPubChannelFactory)

        barPubChannelFactory = new BarPubChannelFactory()
        extension.pubChannels.registerPubChannelFactory('bar', barPubChannelFactory)
    }

    @Test
    public void publish() {
        fooPubChannelFactory.fooCompleteCalled.set(0)
        barPubChannelFactory.barCompleteCalled.set(0)

        extension.publish {
            foo {  // default name
                baz = '1'
            }
            bar() {}
            foo('one') {
                baz = '2'
            }
            bar('xxx')  // without closure should work as well
            foo('two') {
                baz = '3'
            }
        }

        assertEquals(3, fooPubChannelFactory.fooCompleteCalled.get())
        def fooChannels = extension.pubChannels.byChannelType('foo')
        assertEquals(3, fooChannels.size())
        def fooChannelIt = fooChannels.iterator()
        assertEquals('default', fooChannelIt.next().name)
        assertEquals('one', fooChannelIt.next().name)
        assertEquals('two', fooChannelIt.next().name)

        assertEquals(2, barPubChannelFactory.barCompleteCalled.get())
        def barChannels = extension.pubChannels.byChannelType('bar')
        def barChannelIt = barChannels.iterator()
        assertEquals(2, barChannels.size())
        assertEquals('default', barChannelIt.next().name)
        assertEquals('xxx', barChannelIt.next().name)
    }

    @Test(expected = IllegalArgumentException.class)
    public void publish_failOnDuplicate() {
        extension.publish {
            foo('x') {
                baz = '1'
            }
            foo('x') {
                baz = '2'
            }
        }
    }

    @Test
    public void getTagName() {
        assertEquals('v1.2.3', extension.tagName)
    }

    @Test
    public void getTagNameWithPrefix() {
        extension.setTagPrefix('myPrefix_')
        assertEquals('myPrefix_1.2.3', extension.tagName)
    }

    @Test
    public void versionFile() {
        assertNotNull(extension.versionFile)
        assertEquals('version.txt', extension.versionFile.getName())
    }

    private class FooPubChannel extends PubChannel {

        String baz

        FooPubChannel(String name) {
            super(name)
        }
    }

    private class FooPubChannelFactory implements PubChannelFactory<FooPubChannel> {

        AtomicInteger fooCompleteCalled = new AtomicInteger(0)

        @Override
        FooPubChannel create(String name) {
            return new FooPubChannel(name)
        }

        @Override
        void onConfigurationComplete(FooPubChannel pubChannel) {
            fooCompleteCalled.incrementAndGet()
            assertNotNull(pubChannel.baz)
        }
    }

    private class BarPubChannel extends PubChannel {
        BarPubChannel(String name) {
            super(name)
        }
    }

    private class BarPubChannelFactory implements PubChannelFactory<PubChannel> {

        AtomicInteger barCompleteCalled = new AtomicInteger(0)

        @Override
        PubChannel create(String name) {
            return new BarPubChannel(name)
        }

        @Override
        void onConfigurationComplete(PubChannel pubChannel) {
            barCompleteCalled.incrementAndGet()
        }

    }

}