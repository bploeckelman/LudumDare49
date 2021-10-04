package lando.systems.ld49.world;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld49.Assets;
import lando.systems.ld49.Main;

public class Shot {

    public static final float gravity = -700;

    public Vector2 pos = new Vector2();
    public Vector2 velocity = new Vector2();
    public Animation<TextureRegion> anim;
    public float animTime;
    public float rotation;
    public float radius = 16;
    public float dtLeft;
    public boolean remove;
    public float pulse = 0;

    public Shot(Vector2 pos, Vector2 velocity) {
        this.pos.set(pos);
        this.velocity.set(velocity);
        this.remove = false;

        Assets assets = Main.game.assets;

        // figure out which projectile we are, weighted so that bananaMan is rare
        int which = MathUtils.random(0, 100);
        if      (which <= 45) this.anim = assets.projectiles.skull;
        else if (which <= 90) this.anim = assets.projectiles.coconut;
        else {
            this.anim = assets.projectiles.bananaMan;
            // TODO: Pete, play the wilhelm scream or whatever here
        }
        this.animTime = 0;
        this.rotation = 0;
    }

    public void hitPulse() {
        pulse = 4;
    }

    public void update(float dt) {
        velocity.add(0, gravity*dt);

        float pulseSpeed = 100;
        pulse -= pulseSpeed * dt;
        if (pulse < 0) pulse = 0;

        animTime += dt;

        float rotSpeed = 100;
        rotation -= rotSpeed * dt;
    }

    public void render(SpriteBatch batch) {
        TextureRegion keyframe = anim.getKeyFrame(animTime);
        batch.draw(keyframe,
                pos.x - (radius + pulse),
                pos.y - (radius + pulse),
                radius + pulse, radius + pulse,
                radius*2 + pulse*2, radius*2 + pulse*2,
                1f, 1f,
                rotation
        );
    }
}
