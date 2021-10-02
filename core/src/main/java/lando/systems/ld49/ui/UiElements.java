package lando.systems.ld49.ui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld49.Assets;

public class UiElements {

    private final Assets assets;
    private final NinePatch panel;

    public UiElements(Assets assets) {
        this.assets = assets;
        this.panel = new NinePatch(assets.atlas.findRegion("ui/grey_panel"), 8, 8, 8, 8);
    }

    public void drawPanel(SpriteBatch batch, Rectangle bounds) {
        panel.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
    }

}
