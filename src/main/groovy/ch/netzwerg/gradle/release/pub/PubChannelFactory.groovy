package ch.netzwerg.gradle.release.pub

import org.gradle.api.NamedDomainObjectFactory
import org.slf4j.LoggerFactory

class PubChannelFactory implements NamedDomainObjectFactory<PubChannel> {

    private static final LOGGER = LoggerFactory.getLogger(PubChannelFactory.class)

    final Map<String, NamedDomainObjectFactory<PubChannel>> delegateFactories = new HashMap<>();

    @Override
    PubChannel create(String name) {
        String delegateKey = delegateFactories.keySet().find { name.startsWith(it) }
        def delegateFactory = delegateFactories.get(delegateKey)
        if (delegateFactory == null) {
            LOGGER.debug("Creating default publication channel for name $name")
            return new PubChannel(name)
        } else {
            LOGGER.debug("Creating publication channel via delegate factory for name $name")
            return delegateFactory.create(name)
        }
    }

    def registerDelegateFactory(String namePrefix, NamedDomainObjectFactory<PubChannel> factory) {
        delegateFactories.put(namePrefix, factory)
    }

}