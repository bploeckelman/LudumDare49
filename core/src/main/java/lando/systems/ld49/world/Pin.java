package lando.systems.ld49.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld49.Audio;
import lando.systems.ld49.Main;
import lando.systems.ld49.collision.Collidable;

public class Pin implements Collidable {

    public enum Type {
        steel, bumper;
        public Animation<TextureRegion> anim;
    }

    private static float PIN_DRIFT_DIST = 20;
    public Type type;
    public Vector2 position = new Vector2();
    private Vector2 startPosition = new Vector2();
    private float driftAngle;
    private float accum;
    private float positionPeriod;

    public float radius = 6;
    private float animTime = 0;

    public Pin(float x, float y, Type type){
        this.position.set(x, y);
        this.type = type;
        this.startPosition.set(x, y);
        driftAngle = MathUtils.random(360f);
        this.accum = 0;
        this.positionPeriod  = MathUtils.random(.1f, .5f);
    }


    public void update(float dt) {
        animTime += dt;
        accum += dt;
        position.set(startPosition.x + MathUtils.sin(accum * positionPeriod) * MathUtils.sinDeg(driftAngle) * PIN_DRIFT_DIST,
                startPosition.y + MathUtils.sin(accum * positionPeriod) * MathUtils.cosDeg(driftAngle) * PIN_DRIFT_DIST);
    }

    public void render(SpriteBatch batch) {
        batch.draw(type.anim.getKeyFrame(animTime), position.x - radius, position.y -radius, radius*2, radius*2);
    }

    @Override
    public float getElastisity() {
        switch (type){
            case steel: return .8f;
            case bumper: return 1.2f;
        }
        return 1.0f;
    }

    @Override
    public void hit(Shot shot) {
        shot.hitPulse();
        Main.game.audio.playSound(Audio.Sounds.rodHit, 0.4f);
    }
}
