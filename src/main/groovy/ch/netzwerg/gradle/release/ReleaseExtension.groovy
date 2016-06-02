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

import ch.netzwerg.gradle.release.pub.PubChannelContainer
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil

class ReleaseExtension {

    private static final DEFAULT_VERSION_FILE = 'version.txt'
    private static final DEFAULT_DEPENDS_ON = Collections.singletonList('build')
    private static final DEFAULT_PUSH = false
    private static final DEFAULT_TAG_PREFIX = 'v'
    private static final DEFAULT_VERSION_SUFFIX = '-SNAPSHOT'

    private final Project project
    private final PubChannelContainer channelContainer
    private final File versionFile

    List<Object> dependsOn = DEFAULT_DEPENDS_ON
    boolean push = DEFAULT_PUSH
    String tagPrefix = DEFAULT_TAG_PREFIX
    String versionSuffix = DEFAULT_VERSION_SUFFIX

    ReleaseExtension(Project project) {
        this.project = project
        this.channelContainer = new PubChannelContainer()
        this.versionFile = project.file(DEFAULT_VERSION_FILE)
    }

    def dependsOn(Object... paths) {
        this.dependsOn = Arrays.asList(paths)
    }

    public PubChannelContainer publish(Closure closure) {
        ConfigureUtil.configure(closure, channelContainer)
        return channelContainer
    }

    public PubChannelContainer getPubChannels() {
        return channelContainer;
    }

    public String getTagName() {
        return tagPrefix + "$project.version" - versionSuffix
    }

    public File getVersionFile() {
        return versionFile
    }

}