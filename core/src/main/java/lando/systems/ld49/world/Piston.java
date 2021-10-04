package lando.systems.ld49.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld49.Audio;
import lando.systems.ld49.Main;
import lando.systems.ld49.collision.Collidable;
import lando.systems.ld49.utils.Time;


public class Piston implements Collidable {

    private static float MAX_HEAT = 100;
    private TextureRegion piston_back;
    private TextureRegion piston;
    private TextureRegion piston_front;
    private TextureRegion piston_buttons;

    private float heat;
    private float heatupSpeed = 1;
    private float heatupSpeedMax = 5;

    public boolean broken;
    private Flame flameBackground;
    public Rectangle bounds = new Rectangle();
    private float pistonPosition;
    private float accum;

    public Piston(float x, float y, float width, float height){
        bounds.set(x, y, width, height);
        broken = false;
        flameBackground = new Flame(bounds.x, bounds.y, bounds.width, bounds.height * 2f, new Color(1f, .8f, .3f, 0.0f), new Color(.8f, .2f, 0, 0f));
        pistonPosition = 0;
        piston_back = Main.game.assets.atlas.findRegion("tower/piston-socket/piston-socket-back");
        piston = Main.game.assets.atlas.findRegion("tower/piston-socket/piston");
        piston_front = Main.game.assets.atlas.findRegion("tower/piston-socket/piston-socket-front");
        piston_buttons = Main.game.assets.atlas.findRegion("tower/piston-socket/piston-socket-buttons");
        accum = 0;
    }


    public void update(float dt) {
        accum += dt;
        // ~5 minutes of playtime until max speed is reached
        float elapsedSecondsUntilMaxSpeed = 60 * 5;
        float percentOfMaxSpeed = accum / elapsedSecondsUntilMaxSpeed;

        heatupSpeed = MathUtils.clamp(percentOfMaxSpeed * heatupSpeedMax, 1, heatupSpeedMax);
//        Gdx.app.log("heat speed", Float.toString(heatupSpeed));

        if (!broken) {
            heat += heatupSpeed * dt;
            if (heat >= MAX_HEAT) {
                heat = MAX_HEAT;
                broken = true;
                Main.game.audio.playSound(Audio.Sounds.fire, 1.0f);
                Main.game.audio.playSound(Audio.Sounds.alarm, 0.075f);
                Main.game.getScreen().shaker.addDamage(.1f);
            }
        } else {

        }
        pistonPosition = MathUtils.lerp(pistonPosition, getPercentHeat(), .01f);
        flameBackground.insideColor.a = getPercentHeat();
        flameBackground.outsideColor.a = getPercentHeat();
        flameBackground.update(dt);
    }

    public void render(SpriteBatch batch) {

        batch.draw(piston_back, bounds.x, bounds.y, bounds.width, bounds.height);
        if (broken){
            batch.setColor(Color.GRAY);
        } else {
            batch.setColor(Color.WHITE);
        }
        batch.draw(piston, bounds.x, bounds.y - (bounds.height/3f) + (bounds.height/2f)*pistonPosition, bounds.width, bounds.height);
        batch.setColor(Color.WHITE);
        flameBackground.render(batch);
        if (broken){
            batch.setColor(Color.GRAY);
        } else {
            batch.setColor(Color.WHITE);
        }
        batch.draw(piston_front, bounds.x, bounds.y, bounds.width, bounds.height);

        batch.draw(piston_buttons, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(Color.WHITE);
    }

    public float getPercentHeat() {
        return MathUtils.clamp(heat/MAX_HEAT, 0f, 1f);
    }

    @Override
    public float getElastisity() {
        return 0;
    }

    @Override
    public void hit(Shot shot) {
        if (!broken) {
//            Gdx.app.log("some tag", "current heat  " + getPercentHeat());
            float vol = getPercentHeat();
            Main.game.audio.playSound(Audio.Sounds.pistonDown, vol);

            heat = 0;
        }
        if (broken) {
//            Gdx.app.log("some tag", "Broken Hit");
            Main.game.audio.playSound(Audio.Sounds.downHit, 0.24f);
        }

    }
}
