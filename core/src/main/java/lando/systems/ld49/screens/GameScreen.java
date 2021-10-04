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
import lando.systems.ld49.world.Tutorial;
import lando.systems.ld49.world.World;

public class GameScreen extends BaseScreen {

    public World world;

    public final Vector3 mousePos = new Vector3();
    final Vector2 cameraPos = new Vector2();
    float accum = 0;
    float targetZoom;

    public final UI ui;
    public final Tutorial tutorial;

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
        tutorial = new Tutorial();

        cameraPos.set(Config.viewport_width / 2f, 270);
        worldCamera.position.set(cameraPos, 0);
        worldCamera.update();
        game.audio.playMusic(Audio.Musics.music1, true);
//        game.audio.playMusic(Audio.Musics.example, true);
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
        ui.setTemperature(world.reactor.getTemperaturePercent());
        ui.setStructuralDmg(world.reactor.getStructurePercent());
        ui.update(dt);
        tutorial.update(dt);
        boolean pause = tutorial.isActive();
        world.update(dt, pause);

        // draw some sparkle for nice
        accum += dt;

        if (Gdx.input.justTouched()){
            windowCamera.unproject(mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            Gdx.app.log("Touch", "X: " + mousePos.x + " Y: " + mousePos.y);
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
            ui.draw(batch);
            tutorial.render(batch, windowCamera);

        }
        batch.end();
    }



}
