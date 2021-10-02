package lando.systems.ld49.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld49.Assets;

public class UIElements {

    private final Assets assets;
    private final NinePatch panel;
    private final NinePatch buttonSmallPressed;
    private final TextureRegion markerRight;

    public UIElements(Assets assets) {
        this.assets = assets;
        this.panel = new NinePatch(assets.atlas.findRegion("ui/grey_panel"), 8, 8, 8, 8);
        this.buttonSmallPressed = new NinePatch(assets.atlas.findRegion("ui/grey_button11"), 2, 2, 2, 2);
        this.markerRight = assets.atlas.findRegion("ui/grey_sliderRight");
    }

    public void drawPanel(SpriteBatch batch, Rectangle bounds) {
        panel.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void drawVerticalMeter(SpriteBatch batch, Rectangle bounds, TextureRegion fillTexture, float fillPercent) {
        // can't really use ninepatches for something that is much more stretched on one axis than the other

        batch.setColor(1f, 1f, 1f, 0.5f);
        batch.draw(assets.pixel, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(Color.LIGHT_GRAY);
        assets.debugNinePatch.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(Color.WHITE);

        float margin = 4;
        batch.draw(fillTexture,
                bounds.x + margin,
                bounds.y + margin,
                bounds.width - 2 * margin,
                bounds.height - 2 * margin);

        float offset = markerRight.getRegionWidth() - 2 * margin;
        batch.draw(markerRight,
                bounds.x - offset,
                bounds.y + margin + (bounds.height - (2 * margin)) * fillPercent - markerRight.getRegionHeight() / 2f);
    }

}
