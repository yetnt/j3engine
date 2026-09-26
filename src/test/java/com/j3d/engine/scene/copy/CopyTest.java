package com.j3d.engine.scene.copy;

import com.j3d.StaticRefs;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.scene.nodes.geometry.*;
import com.yetnt.utils.tuple.Pair;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import testframework.J3DTest;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CopyTest {

    J3DTest test;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        test = new J3DTest(testInfo);
    }

    @AfterEach
    void purge(TestInfo testInfo) {
        test.finished(testInfo);
    }

    Pair<ArrayList<GObject> , GTri> makeOriginal() {
        GPoint point = new GPoint(Vector3.ZERO);
        GPoint point2 = new GPoint(Vector3.acrossX(10));
        GPoint point3 = new GPoint(Vector3.acrossY(-10));

        GCurve curve = new GCurve(point, point2, point3);
        GTri tri = new GTri(Color.ORANGE, point, point2, point3);

        // Ordered to preserve dependency on points
        ArrayList<GObject> original = new ArrayList<>(List.of(point, point2, point3));
        original.addAll(tri.getLegStream().collect(Collectors.toCollection(ArrayList::new)));
        original.add(tri);
        original.add(curve);

        return new Pair<>(original, tri);
    }

    @Test
    void copyTest() {
        // This test all the objects exist
        // Primarily to see if duplicate copies are made
        ArrayList<GObject> original = makeOriginal().getFirst();
        CopyProperties copyProperties = CopyProperties.builder(original)
                .softDependencies(true).build();

        original.forEach(o -> o.copy(copyProperties));

        original.forEach(o -> {
            // A copy of this object must exist
            String identifier = o.getId() + "["+o.getClass().getName()+"]";
            Assertions.assertTrue(
                    copyProperties.exists(o.getId()),
                    "No copy of " + identifier + " exists."
            );
            GObject object = copyProperties.get(o.getId());
            Assertions.assertNotNull(object, "Copy of " + identifier + " was null.");

            Assertions.assertEquals(
                    object.getClass(), o.getClass(),
                    "Copy of " + identifier + " was not the same class as the original."
            );

            // properties should ideally match 1:1

            Assertions.assertEquals(
                    object.getPivot(), o.getPivot(),
                    "Copy of " + identifier + "'s pivot was not the same as the original."
            );
            Assertions.assertEquals(
                    object.getColour(), o.getColour(),
                    "Copy of " + identifier + "'s colour was not the same as the original."
            );
        });
    }

    @Test
    void copyTestSoftDependencies() {
        // Removing the first 3 points.
        ArrayList<GObject> original = makeOriginal().getFirst();
        ArrayList<GObject> withoutPoints = original.stream()
                .filter(o -> !(o instanceof GPoint))
                .collect(Collectors.toCollection(ArrayList::new));
        CopyProperties copyProperties = CopyProperties.builder(withoutPoints)
                .softDependencies(true).build();

        original.forEach(o -> o.copy(copyProperties));

        original.forEach(o -> {
            // A copy of this object, regardless of it not being in the original list, must exist
            String identifier = o.getId() + "["+o.getClass().getName()+"]";
            Assertions.assertTrue(
                    copyProperties.exists(o.getId()),
                    "No copy of " + identifier + " exists."
            );
        });
    }


    @Test
    void copyTestMissingDependencies() {
        // Removing the first 3 points.
        Pair<ArrayList<GObject> , GTri> originalFromMethod = makeOriginal();
        ArrayList<GObject> original = originalFromMethod.getFirst();
        ArrayList<GObject> withoutPoints = original.stream()
                .filter(o -> !(o instanceof GPoint))
                .collect(Collectors.toCollection(ArrayList::new));
        CopyProperties copyProperties = CopyProperties.builder(withoutPoints)
                .softDependencies(false).build();

        Assertions.assertThrows(InvalidCopyException.class, () -> originalFromMethod.getSecond()
                .copy(copyProperties));
    }
}