package lando.systems.ld49.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld49.Assets;

public class World {

    private final Assets assets;

    public final Rectangle bounds;

    public World(Assets assets) {
        this.assets = assets;
        this.bounds = new Rectangle(0, 0, 1024, 1024);
    }

    public void update(float dt) {

    }

    public void draw(SpriteBatch batch) {
        batch.draw(assets.backgrounds.castles, bounds.x, bounds.y, bounds.width, bounds.height);
    }

}
