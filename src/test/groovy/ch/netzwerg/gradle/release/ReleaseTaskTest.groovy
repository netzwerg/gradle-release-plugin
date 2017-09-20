package ch.netzwerg.gradle.release

import org.junit.Test

import static org.junit.Assert.assertEquals

class ReleaseTaskTest {

    @Test
    public void getNextVersion() {
        assertEquals('1.2.4-SNAPSHOT', ReleaseTask.getNextVersion('1.2.3', '-SNAPSHOT'))
    }

}
