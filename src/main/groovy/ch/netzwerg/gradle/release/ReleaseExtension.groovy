/**
 * Copyright 2014 Rahel Lüthy
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
package ch.netzwerg.gradle.release

import ch.netzwerg.gradle.release.pub.PubChannel
import ch.netzwerg.gradle.release.pub.PubChannelFactory
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

class ReleaseExtension {

    private static final DEFAULT_DEPENDS_ON = Collections.singletonList('build')
    private static final DEFAULT_PUSH = false
    private static final DEFAULT_SUFFIX = '-SNAPSHOT'

    private final NamedDomainObjectContainer<? extends PubChannel> pubChannels
    private final PubChannelFactory factory
    private final Project project

    List<Object> dependsOn = DEFAULT_DEPENDS_ON
    boolean push = DEFAULT_PUSH
    String suffix = DEFAULT_SUFFIX

    ReleaseExtension(Project project, pubChannels, factory) {
        this.project = project
        this.pubChannels = pubChannels
        this.factory = factory
    }

    def dependsOn(Object... paths) {
        this.dependsOn = Arrays.asList(paths)
    }

    def publish(Closure closure) {
        pubChannels.configure(closure)
    }

    def PubChannelFactory getPubChannelFactory() {
        return factory
    }

    def Collection<? extends PubChannel> getPubChannelsByNamePrefix(String namePrefix) {
        return pubChannels.findAll {
            it.name.startsWith(namePrefix)
        }
    }

    def getTagName() {
        return "v$project.version" - suffix
    }

}