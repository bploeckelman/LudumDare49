package lando.systems.ld49.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld49.Main;

public class Shot {
    private Vector2 pos = new Vector2();
    private Vector2 velocity = new Vector2();

    public Shot(Vector2 pos, Vector2 velocity) {
        this.pos.set(pos);
        this.velocity.set(velocity);

    }

    public void update(float dt) {
        velocity.add(0, -500*dt);
        pos.add(velocity.x * dt, velocity.y * dt);
    }

    public void render(SpriteBatch batch) {
        batch.draw(Main.game.assets.pixel, pos.x - 10, pos.y - 10, 20, 20);
    }
}
