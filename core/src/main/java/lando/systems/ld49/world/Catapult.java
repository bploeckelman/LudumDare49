package lando.systems.ld49.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld49.Assets;
import lando.systems.ld49.Audio;
import lando.systems.ld49.screens.GameScreen;

public class Catapult {
    private Assets assets;

    private Vector2 pos = new Vector2();
    private Rectangle bounds = new Rectangle();
    private boolean held;
    private Vector2 launchAngle = new Vector2();
    private float strength;

    private float width = 50;


    public Catapult(Assets assets, float x, float y) {
        this.assets = assets;
        pos.set(x, y);
        bounds.set(x - width/2f, y - width/2f, width, width);
    }

    public void update(float dt, GameScreen screen) {
        Vector3 mousePos = screen.mousePos;
        //TODO: If no ore, exit here
        if (!held){
            if (Gdx.input.justTouched() && bounds.contains(mousePos.x, mousePos.y)){
                held = true;
                screen.game.audio.playSound(Audio.Sounds.slingshotPull);

            }
        } else {
            if (!Gdx.input.isTouched()) {
                held = false;
                // TODO: Launch something
                screen.game.audio.playSound(Audio.Sounds.slingshotRelease, 0.2f);
                screen.world.addShot(new Shot(pos, new Vector2(launchAngle.x * strength * 10, launchAngle.y * strength * 10)));
            } else {
                launchAngle.set(pos.x - mousePos.x, pos.y - mousePos.y).nor();
                strength = MathUtils.clamp(pos.dst(mousePos.x, mousePos.y), 0, 60f);
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (held){
            batch.setColor(Color.GREEN);
        } else {
            batch.setColor(Color.RED);
        }
        batch.draw(assets.pixel, pos.x - width/2, pos.y - width/2, width, width);

        if (held){
            batch.setColor(Color.YELLOW);
            batch.draw(assets.pixel, pos.x - launchAngle.x * strength - 10, pos.y - launchAngle.y * strength - 10, 20, 20);
        }

        batch.setColor(Color.WHITE);
    }
}
