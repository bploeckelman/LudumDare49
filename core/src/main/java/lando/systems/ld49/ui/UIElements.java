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
    private final NinePatch buttonSmallReleased;
    private final TextureRegion markerLeft;
    private final TextureRegion markerRight;
    private final TextureRegion markerUp;
    private final TextureRegion markerDown;
    private final TextureRegion markerCircle;
    private final TextureRegion iconStruct;
    private final TextureRegion iconTemp;
    private final TextureRegion progressBarStart;
    private final TextureRegion progressBarCenter;
    private final TextureRegion progressBarEnd;

    public enum ElementDir { left, right, up, down }
    public enum Icon { structure, temperature }

    public UIElements(Assets assets) {
        this.assets = assets;
        this.panel = new NinePatch(assets.atlas.findRegion("ui/grey_panel"), 8, 8, 8, 8);
        this.buttonSmallPressed  = new NinePatch(assets.atlas.findRegion("ui/grey_button11"), 5, 5, 5, 5);
        this.buttonSmallReleased = new NinePatch(assets.atlas.findRegion("ui/grey_button12"), 5, 5, 5, 5);
        this.markerRight  = assets.atlas.findRegion("ui/grey_sliderRight");
        this.markerLeft   = assets.atlas.findRegion("ui/grey_sliderLeft");
        this.markerUp     = assets.atlas.findRegion("ui/grey_sliderUp");
        this.markerDown   = assets.atlas.findRegion("ui/grey_sliderDown");
        this.markerCircle = assets.atlas.findRegion("ui/grey_circle");
        this.iconStruct = assets.atlas.findRegion("icons/emote-wrench");
        this.iconTemp   = assets.atlas.findRegion("icons/emote-temp");
        this.progressBarStart  = assets.atlas.findRegion("ui/grey_sliderHorizStart");
        this.progressBarCenter = assets.atlas.findRegion("ui/grey_sliderHorizontal");
        this.progressBarEnd    = assets.atlas.findRegion("ui/grey_sliderHorizEnd");
    }

    public void drawPanel(SpriteBatch batch, Rectangle bounds) {
        panel.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void drawButton(SpriteBatch batch, Rectangle bounds, Color tint, boolean pressed) {
        NinePatch button = pressed ? buttonSmallPressed : buttonSmallReleased;

        batch.setColor(tint);
        button.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(Color.WHITE);
    }

    public void drawIcon(SpriteBatch batch, Icon icon, Rectangle bounds) {
        TextureRegion texture = null;
        switch (icon) {
            case structure:   texture = iconStruct; break;
            case temperature: texture = iconTemp;   break;
        }
        if (texture != null) {
            batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    public void drawHorizontalProgressBar(SpriteBatch batch, Rectangle bounds, Color fillColor, float fillPercent) {
        batch.setColor(Color.WHITE);

        // draw background
        batch.draw(progressBarCenter, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.draw(progressBarStart,  bounds.x, bounds.y, progressBarStart.getRegionWidth(), bounds.height);
        batch.draw(progressBarEnd,    bounds.x + bounds.width - progressBarEnd.getRegionWidth(), bounds.y, progressBarEnd.getRegionWidth(), bounds.height);

        // draw fill bar
        float cap = 4;
        batch.setColor(fillColor);
        batch.draw(assets.pixelRegion, bounds.x + cap, bounds.y/* + bounds.height / 2f - bounds.height / 4f*/, (bounds.width - 2 * cap) * fillPercent, bounds.height / 2f);
        batch.setColor(Color.WHITE);
    }

    public void drawVerticalMeter(SpriteBatch batch, Rectangle bounds, TextureRegion fillTexture, float fillPercent, ElementDir markerDir) {
        // can't really use ninepatches for something that is much more stretched on one axis than the other

        batch.setColor(1f, 1f, 1f, 0.5f);
        batch.draw(assets.pixelRegion, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(Color.LIGHT_GRAY);
        assets.debugNinePatch.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(Color.WHITE);

        float margin = 4;
        batch.draw(fillTexture,
                bounds.x + margin,
                bounds.y + margin,
                bounds.width - 2 * margin,
                bounds.height - 2 * margin);

        float offset = 0;
        TextureRegion marker;
        switch (markerDir) {
            case left:  marker = markerLeft;  offset = bounds.width; break;
            case right: marker = markerRight; offset = -marker.getRegionWidth(); break;
            default:    marker = markerCircle;
        }
        batch.draw(marker,
                bounds.x + offset,
                bounds.y + margin + (bounds.height - (2 * margin)) * fillPercent - marker.getRegionHeight() / 2f);
    }

}
