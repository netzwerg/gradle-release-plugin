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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.LoggerFactory

class ReleasePlugin implements Plugin<Project> {

    public static final String RELEASE_TASK_NAME = 'release'
    public static final String RELEASE_EXTENSION_NAME = 'release'

    private static final LOGGER = LoggerFactory.getLogger(ReleasePlugin.class)

    @Override
    void apply(Project project) {
        LOGGER.debug("Registering extension '$RELEASE_EXTENSION_NAME'")
        def releaseExtension = project.extensions.create(RELEASE_EXTENSION_NAME, ReleaseExtension, project)

        LOGGER.debug("Registering task '$RELEASE_TASK_NAME'")
        def releaseTask = project.tasks.create(RELEASE_TASK_NAME, ReleaseTask.class)
        releaseTask.dependsOn({ releaseExtension.dependsOn })

        LOGGER.debug("Initializing project.version from $releaseExtension.versionFile")
        project.version = releaseExtension.versionFile.text.trim()
        LOGGER.debug("Set project.version to $project.version")

        project.afterEvaluate {
            if (project.gradle.startParameter.taskNames.contains(RELEASE_TASK_NAME)) {
                project.version -= releaseExtension.versionSuffix
                LOGGER.debug("Set project.version to $project.version")
                if (!project.gradle.startParameter.dryRun) {
                    LOGGER.debug("Setting '$releaseExtension.versionFile' contents to $project.version")
                    releaseExtension.versionFile.text = project.version
                }
            }
        }
    }

}