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

    public static final String RELEASE_TASK_GROUP_NAME = 'Release'
    public static final String RELEASE_TASK_NAME = 'release'
    public static final String RELEASE_MAJOR_VERSION_TASK_NAME = 'releaseMajorVersion'
    public static final String RELEASE_MINOR_VERSION_TASK_NAME = 'releaseMinorVersion'

    public static Map<String, String> RELEASE_TASKS = [
            (RELEASE_TASK_NAME)              : "Creates a tagged non-SNAPSHOT release.",
            (RELEASE_MAJOR_VERSION_TASK_NAME): 'Upgrades to next major version & creates a tagged non-SNAPSHOT release.',
            (RELEASE_MINOR_VERSION_TASK_NAME): 'Upgrades to next minor version & creates a tagged non-SNAPSHOT release.'
    ]

    public static final String RELEASE_EXTENSION_NAME = 'release'

    private static final LOGGER = LoggerFactory.getLogger(ReleasePlugin.class)

    @Override
    void apply(Project project) {
        LOGGER.debug("Registering extension '$RELEASE_EXTENSION_NAME'")
        def releaseExtension = project.extensions.create(RELEASE_EXTENSION_NAME, ReleaseExtension, project)

        RELEASE_TASKS.each {
            String name = it.key
            String description = it.value
            LOGGER.debug("Registering task '$name'")
            def releaseTask = project.tasks.create(name, ReleaseTask.class)
            releaseTask.description = description
            releaseTask.group = RELEASE_TASK_GROUP_NAME
            releaseTask.dependsOn({ releaseExtension.dependsOn })
        }

        LOGGER.debug("Initializing project.version from $releaseExtension.versionFile")
        project.version = releaseExtension.versionFile.text.trim()
        LOGGER.debug("Set project.version to $project.version")

        project.afterEvaluate {
            def taskNames = project.gradle.startParameter.taskNames
            if (isAnyReleaseTaskCalled(taskNames)) {
                VersionUpgradeStrategy upgradeStrategy = resolveVersionUpgradeStrategy(taskNames)
                project.version = upgradeStrategy.getReleaseVersion((project.version - releaseExtension.versionSuffix) as String)
                LOGGER.debug("Set project.version to $project.version")
                if (!project.gradle.startParameter.dryRun) {
                    LOGGER.debug("Setting '$releaseExtension.versionFile' contents to $project.version")
                    releaseExtension.versionFile.text = project.version
                }
            }
        }
    }

    private static VersionUpgradeStrategy resolveVersionUpgradeStrategy(List<String> taskNames) {
        if (taskNames.contains(RELEASE_MAJOR_VERSION_TASK_NAME)) {
            return VersionUpgradeStrategyFactory.createMajorVersionUpgradeStrategy()
        } else if (taskNames.contains(RELEASE_MINOR_VERSION_TASK_NAME)) {
            return VersionUpgradeStrategyFactory.createMinorVersionUpgradeStrategy()
        } else {
            VersionUpgradeStrategyFactory.createCurrentVersionUpgradeStrategy()
        }
    }

    private static boolean isAnyReleaseTaskCalled(List<String> taskNames) {
        taskNames.contains(RELEASE_TASK_NAME) ||
                taskNames.contains(RELEASE_MINOR_VERSION_TASK_NAME) ||
                taskNames.contains(RELEASE_MAJOR_VERSION_TASK_NAME)
    }

}