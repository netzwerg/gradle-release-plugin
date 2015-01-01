package ch.netzwerg.gradle.release.pub;

import org.gradle.api.NamedDomainObjectFactory;
import org.junit.Test;

import static org.junit.Assert.*;

public class PubChannelFactoryTest {

    @Test
    public void createDefault() {
        PubChannelFactory factory = new PubChannelFactory();
        PubChannel publication = factory.create("default");
        assertNotNull(publication);
    }

    @Test
    public void createDelegated() {
        PubChannelFactory factory = new PubChannelFactory();
        final PubChannel publicationExtension = new PubChannel("extension");
        NamedDomainObjectFactory<PubChannel> delegateFactory = new NamedDomainObjectFactory<PubChannel>() {
            @Override
            public PubChannel create(String name) {
                return publicationExtension;
            }
        };
        factory.registerDelegateFactory("foo", delegateFactory);
        assertSame(publicationExtension, factory.create("foo"));
        assertSame(publicationExtension, factory.create("fooBarBaz"));
        assertNotSame(publicationExtension, factory.create("bar"));
    }

}