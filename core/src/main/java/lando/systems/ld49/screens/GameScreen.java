package lando.systems.ld49.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld49.Audio;
import lando.systems.ld49.Config;
import lando.systems.ld49.Main;
import lando.systems.ld49.particles.Particles;
import lando.systems.ld49.ui.UI;
import lando.systems.ld49.world.World;

public class GameScreen extends BaseScreen {

    public World world;

    public final Vector3 mousePos = new Vector3();
    final Vector2 cameraPos = new Vector2();
    float accum = 0;
    float targetZoom;

    final UI ui;

    static class KeyState {
        static boolean left_pressed = false;
        static boolean right_pressed = false;
        static boolean up_pressed = false;
        static boolean down_pressed = false;
        static boolean space_pressed = false;
    }

    public GameScreen(Main game) {
        super(game);
        world = new World(this);
        ui = new UI(game, uiElements);

        cameraPos.set(Config.viewport_width / 2f, 270);
        worldCamera.position.set(cameraPos, 0);
        worldCamera.update();
        game.audio.playMusic(Audio.Musics.music1, true);
        // TODO: add a mux if we need more input processors
        Gdx.input.setInputProcessor(ui);
    }

    public void update(float dt) {
        KeyState.left_pressed  = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        KeyState.right_pressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        KeyState.up_pressed    = Gdx.input.isKeyPressed(Input.Keys.UP);
        KeyState.down_pressed  = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        KeyState.space_pressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        worldCamera.unproject(mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0));

        ui.shots = world.shots.size;
        ui.update(dt);
        world.update(dt);

        // draw some sparkle for nice
        accum += dt;
        if (accum > 0.025f) {
            accum -= 0.025f;
            particles.sparkle(mousePos.x, mousePos.y);
        }

        super.update(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        // draw world
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        {
            world.draw(batch);
            particles.draw(batch, Particles.Layer.foreground);
        }
        batch.end();

        // draw overlay
        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
//            float margin = 10;
//            float size = 3 * 16;
//
//            batch.setColor(0.2f, 0.2f, 0.2f, 0.5f);
//            batch.draw(assets.pixel, 10, 10, 3 * size, 3 * size);
//            batch.setColor(Color.SKY);
//            assets.debugNinePatch.draw(batch, 10, 10, 3 * size, 3 * size);
//            batch.setColor(Color.WHITE);
//
//            batch.setColor(KeyState.left_pressed ? Color.LIME : Color.WHITE);
//            batch.draw(inputPrompts.get(InputPrompts.Type.key_light_arrow_left), margin, margin + size, size, size);
//
//            batch.setColor(KeyState.down_pressed ? Color.LIME : Color.WHITE);
//            batch.draw(inputPrompts.get(InputPrompts.Type.key_light_arrow_down), margin + size, margin + size, size, size);
//
//            batch.setColor(KeyState.right_pressed ? Color.LIME : Color.WHITE);
//            batch.draw(inputPrompts.get(InputPrompts.Type.key_light_arrow_right), margin + 2 * size, margin + size, size, size);
//
//            batch.setColor(KeyState.up_pressed ? Color.LIME : Color.WHITE);
//            batch.draw(inputPrompts.get(InputPrompts.Type.key_light_arrow_up), margin + size, margin + 2 * size, size, size);
//
//            batch.setColor(KeyState.space_pressed ? Color.LIME : Color.WHITE);
//            batch.draw(inputPrompts.get(InputPrompts.Type.key_light_spacebar_1), margin, margin, size, size);
//            batch.draw(inputPrompts.get(InputPrompts.Type.key_light_spacebar_2), margin + size, margin, size, size);
//            batch.draw(inputPrompts.get(InputPrompts.Type.key_light_spacebar_3), margin + 2 * size, margin, size, size);
//
//            batch.setColor(Color.WHITE);

            ui.draw(batch);
        }
        batch.end();
    }



}
