package ch.netzwerg.gradle.release

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.regex.Matcher

class ReleasePlugin implements Plugin<Project> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReleasePlugin.class)
    private static final String VERSION_FILE_NAME = "version.txt"

    private Project project

    @Override
    void apply(Project project) {

        this.project = project

        project.ext.versionFile = project.file(VERSION_FILE_NAME)
        project.version = project.ext.versionFile.text.trim()

        if (project.gradle.startParameter.taskNames.contains('release')) {
            project.version -= '-SNAPSHOT'
            project.ext.versionFile.text = project.version
        }

        def clean = project.tasks.getByName("clean")
        def build = project.tasks.getByName("build")

        Task release = project.tasks.create("release")
        release.description = 'Creates non-SNAPSHOT distribution, tags release in VCS, and prepares version for next release cycle.'
        release.dependsOn(clean, build)
        release.doLast{
            commitVersionFile("Release v$project.version")
            createReleaseTag()
            String nextVersion = getNextVersion()
            project.ext.versionFile.text = nextVersion
            commitVersionFile("Prepare next release v$nextVersion")
            pushChanges()
        }
    }

    def commitVersionFile(String msg) {
        LOGGER.debug("Committing version file: $msg")
        git 'commit', '-m', msg, VERSION_FILE_NAME
    }

    def createReleaseTag() {
        String tagName = "v$project.version"
        LOGGER.debug("Creating release tag: $tagName")
        git 'tag', '-a', tagName, "-m Release $tagName"
    }

    def getNextVersion() {
        String pattern = /(\d+)([^\d]*$)/
        Matcher matcher = project.version =~ pattern
        String nextVersion = matcher.replaceAll("${(matcher[0][1] as int) + 1}${matcher[0][2]}") + "-SNAPSHOT"
        return nextVersion
    }

    def pushChanges() {
        LOGGER.debug('Pushing changes to repository')
        git 'push', '--tags'
        git 'push', 'origin', 'master'
    }

    def git(Object[] arguments) {
        LOGGER.debug("git $arguments")
        def output = new ByteArrayOutputStream()
        project.exec {
            executable 'git'
            args arguments
            standardOutput output
            ignoreExitValue = true
        }
        String gitOutput = output.toString().trim()
        if (!gitOutput.isEmpty()) {
            LOGGER.debug(gitOutput)
        }
    }

}