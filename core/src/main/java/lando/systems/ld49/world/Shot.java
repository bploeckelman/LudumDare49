package lando.systems.ld49.world;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld49.Main;

public class Shot {

    public static final float gravity = -500;

    public Vector2 pos = new Vector2();
    public Vector2 velocity = new Vector2();
    public Animation<TextureRegion> anim;
    public float animTime;
    public float rotation;
    public float radius = 16;
    public float dtLeft;
    public boolean remove;

    // TODO: add a little size expansion pulse on hit

    public Shot(Vector2 pos, Vector2 velocity) {
        this.pos.set(pos);
        this.velocity.set(velocity);
        this.remove = false;

        boolean whichProjectile = MathUtils.randomBoolean();
        this.anim = (whichProjectile)
                  ? Main.game.assets.projectiles.skull
                  : Main.game.assets.projectiles.coconut;
        this.animTime = 0;
        this.rotation = 0;
    }

    public void update(float dt) {
        velocity.add(0, gravity*dt);

        animTime += dt;

        float rotSpeed = 100;
        rotation -= rotSpeed * dt;
    }

    public void render(SpriteBatch batch) {
        TextureRegion keyframe = anim.getKeyFrame(animTime);
        batch.draw(keyframe,
                pos.x - radius,
                pos.y - radius,
                radius, radius,
                radius*2, radius*2,
                1f, 1f,
                rotation
        );
    }
}
