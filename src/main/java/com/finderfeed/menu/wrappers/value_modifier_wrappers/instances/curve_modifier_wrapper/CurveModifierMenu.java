package com.finderfeed.menu.wrappers.value_modifier_wrappers.instances.curve_modifier_wrapper;

import com.finderfeed.Main;
import com.finderfeed.menu.MainMenu;
import com.finderfeed.menu.Menu;
import com.finderfeed.menu.NoiseLayerRedactorMenu;
import com.finderfeed.noise_combiner.value_modifier.instances.curve_modifier.CurveModifier;
import com.finderfeed.noise_combiner.value_modifier.instances.curve_modifier.NoiseCurve;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class CurveModifierMenu extends Menu {

    private final CurveModifier curveModifier;

    private final List<Runnable> onChangeListeners = new ArrayList<>();

    private int selectedPoint = 0;
    private int draggingPoint = -1;

    public CurveModifierMenu(String title, CurveModifier curveModifier) {
        super(title, new ImVec2(700, 500));
        this.curveModifier = curveModifier;

        if (this.curveModifier.noiseCurve == null) {
            this.curveModifier.noiseCurve = new NoiseCurve();
        }
    }

    @Override
    public void renderMenuContents() {

        NoiseCurve curve = curveModifier.noiseCurve;

        float canvasW = 620;
        float canvasH = 320;

        ImVec2 start = ImGui.getCursorScreenPos();

        ImDrawList draw = ImGui.getWindowDrawList();

        draw.addRect(
                start.x,
                start.y,
                start.x + canvasW,
                start.y + canvasH,
                0xFFFFFFFF
        );

        // CENTER AXES
        float centerX = start.x + canvasW / 2f;
        float centerY = start.y + canvasH / 2f;

        draw.addLine(
                start.x,
                centerY,
                start.x + canvasW,
                centerY,
                0x66FFFFFF,
                1f
        );

        draw.addLine(
                centerX,
                start.y,
                centerX,
                start.y + canvasH,
                0x66FFFFFF,
                1f
        );

        // LABELS
        draw.addText(start.x + 4, centerY + 4, 0xFFFFFFFF, "-1");
        draw.addText(start.x + canvasW - 18, centerY + 4, 0xFFFFFFFF, "1");
        draw.addText(centerX + 4, start.y + 2, 0xFFFFFFFF, "1");
        draw.addText(centerX + 4, start.y + canvasH - 18, 0xFFFFFFFF, "-1");

        ImGui.invisibleButton("curve_canvas", canvasW, canvasH);

        float mouseX = ImGui.getIO().getMousePosX();
        float mouseY = ImGui.getIO().getMousePosY();

        // START DRAGGING
        if (ImGui.isItemHovered() && ImGui.isMouseClicked(0)) {

            draggingPoint = -1;

            for (int i = 0; i < curve.getPointCount(); i++) {

                Vector2f p = curve.getPointPos(i);

                float px = graphToScreenX(start.x, canvasW, p.x);
                float py = graphToScreenY(start.y, canvasH, p.y);

                float dx = mouseX - px;
                float dy = mouseY - py;

                if (dx * dx + dy * dy <= 100) {
                    draggingPoint = i;
                    selectedPoint = i;
                    break;
                }
            }
        }

        // MOVE POINT WHILE DRAGGING
        if (draggingPoint != -1 && ImGui.isMouseDown(0)) {

            float gx = screenToGraphX(start.x, canvasW, mouseX);
            float gy = screenToGraphY(start.y, canvasH, mouseY);

            curve.setPointPos(draggingPoint, new Vector2f(gx, gy));
        }

        // FINISH DRAGGING -> UPDATE ONCE
        if (draggingPoint != -1 && ImGui.isMouseReleased(0)) {
            draggingPoint = -1;
            triggerChange();
        }

        // CURVE LINES
        for (int i = 0; i < curve.getPointCount() - 1; i++) {

            Vector2f p1 = curve.getPointPos(i);
            Vector2f p2 = curve.getPointPos(i + 1);

            draw.addLine(
                    graphToScreenX(start.x, canvasW, p1.x),
                    graphToScreenY(start.y, canvasH, p1.y),
                    graphToScreenX(start.x, canvasW, p2.x),
                    graphToScreenY(start.y, canvasH, p2.y),
                    0xFF00FFFF,
                    2f
            );
        }

        // POINTS
        for (int i = 0; i < curve.getPointCount(); i++) {

            Vector2f p = curve.getPointPos(i);

            int color = (i == selectedPoint)
                    ? 0xFFFF0000
                    : 0xFFFFFFFF;

            draw.addCircleFilled(
                    graphToScreenX(start.x, canvasW, p.x),
                    graphToScreenY(start.y, canvasH, p.y),
                    6,
                    color
            );
        }

        ImGui.spacing();
        ImGui.text("Selected Point: " + selectedPoint);

        if (ImGui.button("Add Point")) {
            curve.addPoint(selectedPoint);
            triggerChange();
        }

        ImGui.sameLine();

        if (ImGui.button("Remove Point")) {

            curve.removePoint(selectedPoint);

            if (selectedPoint >= curve.getPointCount()) {
                selectedPoint = curve.getPointCount() - 1;
            }

            triggerChange();
        }

        if (ImGui.button("Open Point Noise Layer")) {

            var layer = curve.getLayerAtPoint(selectedPoint);

            int id = MainMenu.takeNextFreeMenuId();
            var redactor = new NoiseLayerRedactorMenu(
                    "Point Layer " + selectedPoint + "##" + id,
                    layer
            );

            redactor.addOnChangeListener(this::triggerChange);

            Main.window.getMainMenu().openMenu(redactor);
        }
    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onClose() {

    }

    private float graphToScreenX(float startX, float width, float x) {
        return startX + ((x + 1f) / 2f) * width;
    }

    private float graphToScreenY(float startY, float height, float y) {
        return startY + (1f - ((y + 1f) / 2f)) * height;
    }

    private float screenToGraphX(float startX, float width, float x) {
        return ((x - startX) / width) * 2f - 1f;
    }

    private float screenToGraphY(float startY, float height, float y) {
        return -((((y - startY) / height) * 2f) - 1f);
    }

    public void addOnChangeListener(Runnable r) {
        this.onChangeListeners.add(r);
    }

    private void triggerChange() {
        for (Runnable r : onChangeListeners) {
            r.run();
        }
    }
}
