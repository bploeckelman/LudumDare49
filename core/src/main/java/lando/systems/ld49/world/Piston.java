package lando.systems.ld49.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld49.collision.Collidable;


public class Piston implements Collidable {

    private static float MAX_HEAT = 120;
    private float heat;
    public boolean broken;
    private Flame flameBackground;
    private Rectangle bounds = new Rectangle();

    public Piston(float x, float y, float width, float height){
        bounds.set(x, y, width, height);
        broken = false;
        flameBackground = new Flame(bounds, Color.CLEAR, Color.CLEAR);
    }


    public void update(float dt) {
        if (!broken)
        heat += dt;
        if (heat >= MAX_HEAT){
            heat = MAX_HEAT;
            broken = true;
        }
    }

    public void render(SpriteBatch batch) {

    }

    public float getPercentHeat() {
        return MathUtils.clamp(heat/MAX_HEAT, 0f, 1f);
    }

    @Override
    public float getElastisity() {
        return 0;
    }

    @Override
    public void hit() {
        heat = 0;
        // TODO: sounds?
    }
}
