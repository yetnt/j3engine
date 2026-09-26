package testframework;

import com.j3d.StaticRefs;
import com.j3d.engine.math.Dim;
import com.j3d.engine.scene.SceneManager;
import org.junit.jupiter.api.TestInfo;

public class J3DTest {

    public J3DTest(TestInfo testInfo) {
        basicStaticRefsAck(testInfo);
    }

    private void basicStaticRefsAck(TestInfo testInfo) {
        StaticRefs.none(true, testInfo.getDisplayName());
        StaticRefs.registerSceneManager(
                new SceneManager(new Dim(1, 2))
        );
    }

    public void finished(TestInfo testInfo) {
        StaticRefs.clear(false);
        StaticRefs.getLog().testPrintln("Passed: " + testInfo.getDisplayName());
        StaticRefs.clearLogger();
    }
}
