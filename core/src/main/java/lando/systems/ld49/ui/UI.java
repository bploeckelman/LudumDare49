package lando.systems.ld49.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld49.Assets;

public class UI {

    private final UIElements uiElements;

    private final Rectangle panelBounds = new Rectangle();
    private final Rectangle heatMeterBounds = new Rectangle();

    private final TextureRegion heatMeterInterior;

    private float heatFillPercent = 0;
    private float accum = 0;

    public UI(Assets assets, UIElements uiElements) {
        this.uiElements = uiElements;
        this.heatMeterInterior = assets.atlas.findRegion("ui/heat-meter-fill");

        // TODO: don't hardcode this stuff, use Config.window_* instead
        this.panelBounds.set(15, 159, 200, 546);

        float margin = 20;
        float width = 20;
        this.heatMeterBounds.set(
                panelBounds.x + panelBounds.width - width - margin,
                panelBounds.y + margin,
                width, panelBounds.height - 2 * margin);
    }

    public void update(float dt) {
        float fillSpeed = 50;
        accum += fillSpeed * dt;
        heatFillPercent = (MathUtils.sinDeg(accum) + 1f) / 2f;
    }

    public void draw(SpriteBatch batch) {
        uiElements.drawPanel(batch, panelBounds);
        uiElements.drawVerticalMeter(batch, heatMeterBounds, heatMeterInterior, heatFillPercent);
    }

}
