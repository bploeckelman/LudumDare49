package lando.systems.ld49.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld49.Audio;
import lando.systems.ld49.Config;
import lando.systems.ld49.Main;
import lando.systems.ld49.particles.Particles;
import lando.systems.ld49.ui.UI;
import lando.systems.ld49.utils.Time;
import lando.systems.ld49.world.Stats;
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
        ui = new UI(this, uiElements);
        tutorial = new Tutorial(this);

        cameraPos.set(Config.viewport_width / 2f, 270);
        worldCamera.position.set(cameraPos, 0);
        worldCamera.update();
        game.audio.fadeMusic(Audio.Musics.music1);
//        game.audio.playMusic(Audio.Musics.example, true);
        // TODO: add a mux if we need more input processors
        Gdx.input.setInputProcessor(ui);

        Time.millis_since_play_started = Time.elapsed_millis();

        Stats.moneySpent = 0;
        Stats.secondsRan = 0;
    }

    public void update(float dt) {
        super.update(dt);

        KeyState.left_pressed  = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        KeyState.right_pressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        KeyState.up_pressed    = Gdx.input.isKeyPressed(Input.Keys.UP);
        KeyState.down_pressed  = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        KeyState.space_pressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        worldCamera.unproject(mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0));

        ui.setTemperature(world.reactor.getTemperaturePercent());
        ui.setStructuralDmg(world.reactor.getStructurePercent());
        tutorial.update(dt);
        boolean pause = tutorial.isActive();
        // TODO: don't allow you to click on the buttons if the tutorial is active?
        ui.update(dt, pause);
        world.update(dt, pause);

        // draw some sparkle for nice
        accum += dt;

        if (Gdx.input.justTouched()){
            windowCamera.unproject(mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
                Gdx.app.log("Touch", "X: " + mousePos.x + " Y: " + mousePos.y);
            }
        }

    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

        // draw world
        batch.setProjectionMatrix(shaker.getCombinedMatrix());
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
            tutorial.render(batch);

        }
        batch.end();
    }



}
