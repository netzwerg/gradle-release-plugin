package ch.netzwerg.gradle.release

class ReleaseExtension {

    private static final DEFAULT_PUSH = false
    private static final DEFAULT_SUFFIX = '-SNAPSHOT'

    List<Object> dependsOn = Collections.emptyList()
    boolean push = DEFAULT_PUSH
    String suffix = DEFAULT_SUFFIX

    public void dependsOn(Object... paths) {
        this.dependsOn = Arrays.asList(paths)
    }

}