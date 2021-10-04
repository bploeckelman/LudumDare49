package lando.systems.ld49.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld49.Audio;
import lando.systems.ld49.Main;
import lando.systems.ld49.collision.Collidable;

public class Pin implements Collidable {

    enum Type {steel, bumper};
    public Type type;
    public Vector2 position = new Vector2();
    public float radius = 6;

    public Pin(float x, float y, Type type){
        this.position.set(x, y);
        this.type = type;
    }

    public Pin(Vector2 position, Type type) {
        this.position.set(position);
        this.type = type;
    }

    public void update(float dt) {

    }

    public void render(SpriteBatch batch) {
        batch.setColor(Color.RED);
        if (type == Type.bumper) {
            batch.setColor(Color.BLUE);
        }
        batch.draw(Main.game.assets.particles.circle, position.x - radius, position.y -radius, radius*2, radius*2);
        batch.setColor(Color.WHITE);
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
