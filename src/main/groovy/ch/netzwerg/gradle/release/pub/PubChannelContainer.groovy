/**
 * Copyright 2015 Rahel Lüthy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.netzwerg.gradle.release.pub

import org.gradle.util.ConfigureUtil
import org.slf4j.LoggerFactory

class PubChannelContainer {

    private static final LOGGER = LoggerFactory.getLogger(PubChannelContainer.class)

    private final Map<String, PubChannelFactory<? extends PubChannel>> factories = new HashMap<>()
    private final Map<ChannelId, PubChannel> channels = [:]

    @SuppressWarnings("GroovyAssignabilityCheck")
    def methodMissing(String channelType, args) {
        LOGGER.debug("PubChannelContainer channelType: $channelType , args: $args")
        String channelName = 'default'
        Closure closure = null
        for (Object arg : args) {
            if (arg instanceof String) {
                channelName = arg
            } else if (arg instanceof Closure) {
                closure = arg
            }
        }

        ChannelId channelId = new ChannelId(channelType, channelName)
        if (channels.containsKey(channelId)) {
            throw new IllegalArgumentException("A configuration named '$channelName' already exists for type '$channelType'")
        }

        def factory = factories.get(channelType)
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported channel type '$channelType'")
        }
        PubChannel pubChannel = factory.create(channelName)
        channels.put(channelId, pubChannel)
        if (closure != null) {
            ConfigureUtil.configure(closure, pubChannel)
        }
        factory.onConfigurationComplete(pubChannel)
    }

    def registerPubChannelFactory(String channelType, PubChannelFactory<? extends PubChannel> factory) {
        factories.put(channelType, factory)
    }

    public Collection<PubChannel> byChannelType(String channelType) {
        channels.findAll {
            it.key.channelType.equals(channelType)
        }.values()
    }

    private static final class ChannelId {

        private final String channelType
        private final String channelName

        private ChannelId(String channelType, String channelName) {
            this.channelName = channelName
            this.channelType = channelType
        }

        boolean equals(o) {
            if (this.is(o)) return true
            if (getClass() != o.class) return false

            ChannelId channelId = (ChannelId) o

            if (channelName != channelId.channelName) return false
            if (channelType != channelId.channelType) return false

            return true
        }

        int hashCode() {
            int result
            result = channelType.hashCode()
            result = 31 * result + channelName.hashCode()
            return result
        }
    }

}