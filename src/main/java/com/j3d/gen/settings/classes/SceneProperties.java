package com.j3d.gen.settings.classes;

import com.j3d.engine.draw.PureSortMethod;
import com.j3d.engine.draw.Renderer;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo2d.CartesianPoint;
import com.j3d.gen.settings.SettingsChild;
import com.j3d.gen.settings.SettingsParent;
import com.j3d.gen.settings.types.BooleanSetting;
import com.j3d.gen.settings.types.DoubleSetting;
import com.j3d.gen.settings.types.EnumSetting;
import com.j3d.ui.settings.SettingsParentPanel;

import java.util.ArrayList;

public class SceneProperties implements SettingsParent {
    /**
     * Factor to scale the {@link CartesianPoint} vs {@link ScreenPoint} units.
     * <p>
     * This is such that the screen space is not used as the default grid. Where (0, 1) and (0, 0) are but a pixel apart.
     * The Scale factor helps by making it such that (if SCALE is set to 10), inputting (0, 1) as a {@link CartesianPoint}, when converted to {@link ScreenPoint} it is multiplied by 10 units.
     */
    public DoubleSetting scale = new DoubleSetting(
            "Axis Scale Factor",
            43.0,
            "Factor to scale each 3d point's projected 2d axis.",
            1.0,
            100.0
    ).setValues(
            Double::intValue,
            i -> i * 1.0,
            1
    );
    public EnumSetting<PureSortMethod> triangleSortMethod = new EnumSetting<>(
            "Triangle Sort Method",
            PureSortMethod.CAMDISTSORT,
            "The method the sceneManager should make use of to sort triangles.",
            PureSortMethod.values()
    ).onSetValue((PureSortMethod l) -> {
        Renderer.setSortMethod(l);
        return null;
    });
    public BooleanSetting useBackFaceCulling = new BooleanSetting(
            "Use Back Face Culling",
            true,
            "Draw method should use back face culling optimization"
    );

    public SceneProperties() {

    }

    @Override
    public String getDescription() {
        return "Scene properties";
    }

    @Override
    public String getName() {
        return "Scene Properties";
    }

    @Override
    public ArrayList<SettingsChild> getAllChildren() {
        return new ArrayList<>() {{
            add(useBackFaceCulling);
            add(scale);
            add(triangleSortMethod);
        }};
    }

    @Override
    public SettingsParentPanel panel() {
        return new SettingsParentPanel(this).addChildren(getAllChildren());
    }
}
