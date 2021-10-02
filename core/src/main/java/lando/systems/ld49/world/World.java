package lando.systems.ld49.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld49.Assets;

public class World {

    private final Assets assets;
    private final Vector2 center;

    public final Rectangle bounds;

    public World(Assets assets) {
        this.assets = assets;
        this.center = new Vector2();
        this.bounds = new Rectangle(0, 0, 1024, 1024);
        this.bounds.getCenter(center);
    }

    public Vector2 getCenter() {
        return bounds.getCenter(center);
    }

    public void update(float dt) {

    }

    public void draw(SpriteBatch batch) {
        batch.draw(assets.backgrounds.castles, bounds.x, bounds.y, bounds.width, bounds.height);

        batch.setColor(198 / 255f, 156 / 255f, 108 / 255f, 1);
        batch.draw(assets.pixel, bounds.x, bounds.y, bounds.width, bounds.height / 2f - 99);
        batch.setColor(Color.WHITE);

        float scale = 1.5f;
        float width  = scale * assets.backgrounds.nuclearPlant.getRegionWidth();
        float height = scale * assets.backgrounds.nuclearPlant.getRegionHeight();
        batch.draw(assets.backgrounds.nuclearPlant,
                bounds.x + bounds.width  / 2f - width  / 2f,
                bounds.y + bounds.height / 2f - height / 2f,
                width, height);
    }

}
