package lando.systems.ld49.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld49.Main;

public class Shot {
    public float radius = 6;
    public Vector2 pos = new Vector2();
    public float dtLeft;
    public boolean remove;
    public Vector2 velocity = new Vector2();


    public Shot(Vector2 pos, Vector2 velocity) {
        this.pos.set(pos);
        this.velocity.set(velocity);
        remove = false;
    }

    public void update(float dt) {
        velocity.add(0, -500*dt);
//        dtLeft = dt;
//        pos.add(velocity.x * dt, velocity.y * dt);
    }

    public void render(SpriteBatch batch) {
        batch.setColor(Color.PURPLE);
        batch.draw(Main.game.assets.particles.circle, pos.x - radius, pos.y - radius, radius*2, radius*2);
        batch.setColor(Color.WHITE);
    }
}
