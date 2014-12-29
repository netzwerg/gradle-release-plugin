package ch.netzwerg.gradle.release.pub;

import org.gradle.api.NamedDomainObjectFactory;
import org.junit.Test;

import static org.junit.Assert.*;

public class PublicationFactoryTest {

    @Test
    public void createDefault() {
        PublicationFactory factory = new PublicationFactory();
        Publication publication = factory.create("default");
        assertNotNull(publication);
    }

    @Test
    public void createDelegated() {
        PublicationFactory factory = new PublicationFactory();
        final Publication publicationExtension = new Publication("extension");
        NamedDomainObjectFactory<Publication> delegateFactory = new NamedDomainObjectFactory<Publication>() {
            @Override
            public Publication create(String name) {
                return publicationExtension;
            }
        };
        factory.registerDelegateFactory("foo", delegateFactory);
        assertSame(publicationExtension, factory.create("foo"));
        assertSame(publicationExtension, factory.create("fooBarBaz"));
        assertNotSame(publicationExtension, factory.create("bar"));
    }

}