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

class ReleasePlugin implements Plugin<Project> {

    static final VERSION_FILE_NAME = 'version.txt'
    static final RELEASE_TASK_NAME = 'release'

    @Override
    void apply(Project project) {
        project.ext.versionFile = project.file(VERSION_FILE_NAME)
        project.version = project.ext.versionFile.text.trim()

        if (project.gradle.startParameter.taskNames.contains(RELEASE_TASK_NAME)) {
            project.version -= '-SNAPSHOT'
            project.ext.versionFile.text = project.version
        }

        project.tasks.create(RELEASE_TASK_NAME, ReleaseTask.class)
    }

}