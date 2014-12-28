package ch.netzwerg.gradle.release

import org.gradle.api.NamedDomainObjectFactory
import org.slf4j.LoggerFactory

class PublicationFactory implements NamedDomainObjectFactory<Publication> {

    private static final LOGGER = LoggerFactory.getLogger(PublicationFactory.class)

    PublicationFactory delegate

    @Override
    Publication create(String name) {
        if (delegate == null) {
            LOGGER.debug("Creating default publication")
            return new Publication(name)
        } else {
            LOGGER.debug("Creating publication via delegate factory")
            return delegate.create(name)
        }
    }

}
