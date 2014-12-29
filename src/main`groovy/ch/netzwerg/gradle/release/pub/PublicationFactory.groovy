package ch.netzwerg.gradle.release.pub

import org.gradle.api.NamedDomainObjectFactory
import org.slf4j.LoggerFactory

class PublicationFactory implements NamedDomainObjectFactory<Publication> {

    private static final LOGGER = LoggerFactory.getLogger(PublicationFactory.class)

    final Map<String, NamedDomainObjectFactory<Publication>> delegateFactories = new HashMap<>();

    @Override
    Publication create(String name) {
        String delegateKey = delegateFactories.keySet().find { name.startsWith(it) }
        def delegateFactory = delegateFactories.get(delegateKey)
        if (delegateFactory == null) {
            LOGGER.debug("Creating default publication for name $name")
            return new Publication(name)
        } else {
            LOGGER.debug("Creating publication via delegate factory for name $name")
            return delegateFactory.create(name)
        }
    }

    def registerDelegateFactory(String namePrefix, NamedDomainObjectFactory<Publication> factory) {
        delegateFactories.put(namePrefix, factory)
    }

}