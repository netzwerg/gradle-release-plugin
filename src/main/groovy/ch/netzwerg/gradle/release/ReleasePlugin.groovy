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

import static ch.netzwerg.gradle.release.ReleaseExtension.*

class ReleasePlugin implements Plugin<Project> {

    public static final String RELEASE_TASK_NAME = 'release'
    public static final String RELEASE_EXTENSION_NAME = 'release'

    private static final LOGGER = LoggerFactory.getLogger(ReleasePlugin.class)

    @Override
    void apply(Project project) {

        project.evaluationDependsOnChildren()

        LOGGER.debug("Registering extension '$RELEASE_EXTENSION_NAME'")
        def releaseExtension = project.extensions.create(RELEASE_EXTENSION_NAME, ReleaseExtension, project)

        LOGGER.debug("Registering task '$RELEASE_TASK_NAME'")
        def releaseTask = project.tasks.create(RELEASE_TASK_NAME, ReleaseTask.class)
        releaseTask.dependsOn({ releaseExtension.dependsOn })

        LOGGER.debug("Initializing project.version from $releaseExtension.versionFile")
        project.version = releaseExtension.versionFile.text.trim()

        project.afterEvaluate {
            if (project.gradle.startParameter.taskNames.contains(RELEASE_TASK_NAME)) {
                project.version -= releaseExtension.versionSuffix
                LOGGER.debug("Set project.version to $project.version")
                if (!project.gradle.startParameter.dryRun) {
                    LOGGER.debug("Setting '$releaseExtension.versionFile' contents to $project.version")
                    releaseExtension.versionFile.text = project.version
                }
                if (isUsingUndefinedDefaultDependsOn(project, releaseExtension)) {
                    LOGGER.debug("Creating artificial '$DEFAULT_DEPENDS_ON_TASK_NAME' default task")
                    def subDefaultTasks = project.subprojects.collect {
                        it.tasks.findByPath(DEFAULT_DEPENDS_ON_TASK_NAME)
                    }
                    project.tasks.create(DEFAULT_DEPENDS_ON_TASK_NAME).dependsOn(subDefaultTasks);
                }
            }
        }
    }

    private static boolean isUsingUndefinedDefaultDependsOn(Project project, ReleaseExtension releaseExtension) {
        boolean untouchedDefaultDependsOn = DEFAULT_DEPENDS_ON == releaseExtension.dependsOn
        untouchedDefaultDependsOn && isDefaultDependsOnUndefined(project)
    }

    private static boolean isDefaultDependsOnUndefined(Project project) {
        project.tasks.findByName(DEFAULT_DEPENDS_ON_TASK_NAME) == null
    }

}