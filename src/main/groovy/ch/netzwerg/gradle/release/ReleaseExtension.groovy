package ch.netzwerg.gradle.release

import org.gradle.api.Project

class ReleaseExtension {

    private final ReleasePlugin plugin
    private final Project project

    boolean push = false
    List<Object> dependsOn = Collections.emptyList()

    public ReleaseExtension(ReleasePlugin plugin, Project project) {
        this.plugin = plugin
        this.project = project
    }

    public void dependsOn(Object... paths) {
        this.dependsOn = Arrays.asList(paths)
    }

}