package lando.systems.ld49.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld49.Assets;

public class UI {

    private final UIElements uiElements;

    private final Rectangle panelBounds       = new Rectangle();
    private final Rectangle tempMeterBounds   = new Rectangle();
    private final Rectangle structMeterBounds = new Rectangle();
    private final Rectangle tempIconBounds    = new Rectangle();
    private final Rectangle structIconBounds  = new Rectangle();

    private final TextureRegion heatMeterInterior;
    private final TextureRegion structMeterInterior;

    private float structFillPercent = 1;
    private float heatFillPercent = 0;
    private float accum = 0;

    public UI(Assets assets, UIElements uiElements) {
        this.uiElements = uiElements;
        this.heatMeterInterior = assets.atlas.findRegion("ui/meter-fill-heat");
        this.structMeterInterior = assets.atlas.findRegion("ui/meter-fill-struct");

        // TODO: don't hardcode this stuff, use Config.window_* instead
        this.panelBounds.set(15, 159, 200, 546);

        float rightMargin = 50;
        float topMargin = 50;
        float bottomMargin = 20;
        float width = 20;
        float pad = 10;
        this.tempMeterBounds.set(
                panelBounds.x + panelBounds.width - width - rightMargin,
                panelBounds.y + bottomMargin,
                width, panelBounds.height - bottomMargin - topMargin);

        this.structMeterBounds.set(
                panelBounds.x + panelBounds.width - 2 * width - rightMargin - pad,
                panelBounds.y + bottomMargin,
                width, panelBounds.height - bottomMargin - topMargin);

        float iconW = 32;
        float iconH = 38;
        float iconMargin = 4;
        this.tempIconBounds.set(tempMeterBounds.x + tempMeterBounds.width / 2f - iconW / 2f, tempMeterBounds.y + tempMeterBounds.height + iconMargin, iconW, iconH);
        this.structIconBounds.set(structMeterBounds.x + structMeterBounds.width / 2f - iconW / 2f, structMeterBounds.y + structMeterBounds.height + iconMargin, iconW, iconH);
    }

    public void update(float dt) {
        float fillSpeed = 100;
        accum += fillSpeed * dt;
        heatFillPercent = (MathUtils.sinDeg(accum) + 1f) / 2f;
    }

    public void draw(SpriteBatch batch) {
        uiElements.drawPanel(batch, panelBounds);
        uiElements.drawVerticalMeter(batch, tempMeterBounds, heatMeterInterior, heatFillPercent, UIElements.ElementDir.left);
        uiElements.drawVerticalMeter(batch, structMeterBounds, structMeterInterior, structFillPercent, UIElements.ElementDir.right);
        uiElements.drawIcon(batch, UIElements.Icon.structure, structIconBounds);
        uiElements.drawIcon(batch, UIElements.Icon.temperature, tempIconBounds);
    }

}
