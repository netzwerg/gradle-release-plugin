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

import ch.netzwerg.gradle.release.pub.Publication
import ch.netzwerg.gradle.release.pub.PublicationFactory
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.LoggerFactory

class ReleasePlugin implements Plugin<Project> {

    private static final LOGGER = LoggerFactory.getLogger(ReleasePlugin.class)

    static final VERSION_FILE_NAME = 'version.txt'
    static final RELEASE_TASK_NAME = 'release'
    static final RELEASE_EXTENSION_NAME = 'release'

    @Override
    void apply(Project project) {
        def factory = new PublicationFactory()
        def publications = project.container(Publication, factory)
        def releaseExtension = project.extensions.create(RELEASE_EXTENSION_NAME, ReleaseExtension, publications, factory)
        LOGGER.debug("Registered extension '$RELEASE_EXTENSION_NAME'")

        def releaseTask = project.tasks.create(RELEASE_TASK_NAME, ReleaseTask.class)
        LOGGER.debug("Registered task '$RELEASE_TASK_NAME'")
        releaseTask.dependsOn({ releaseExtension.dependsOn })

        project.ext.versionFile = project.file(VERSION_FILE_NAME)
        project.version = project.ext.versionFile.text.trim()

        project.afterEvaluate {
            if (project.gradle.startParameter.taskNames.contains(RELEASE_TASK_NAME)) {
                project.version -= releaseExtension.suffix
                LOGGER.debug("Set project.version to $project.version")
                project.ext.versionFile.text = project.version
            }
        }
    }

}