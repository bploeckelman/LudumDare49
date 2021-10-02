package lando.systems.ld49.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld49.Audio;
import lando.systems.ld49.Main;
import lando.systems.ld49.particles.Particles;
import lando.systems.ld49.ui.InputPrompts;
import lando.systems.ld49.ui.UI;
import lando.systems.ld49.utils.accessors.Vector2Accessor;
import lando.systems.ld49.world.World;

public class GameScreen extends BaseScreen {

    public World world;

    public final Vector3 mousePos = new Vector3();
    final Vector2 cameraPos = new Vector2();
    final MutableFloat cameraZoom = new MutableFloat(1f);
    float accum = 0;
    boolean zoomedIn = false;
    boolean isZooming = false;

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
        ui = new UI(game.assets, uiElements);

        cameraPos.set(world.bounds.width / 2, world.bounds.height / 2);
        worldCamera.position.set(cameraPos, 0);
        worldCamera.update();
    }

    public void update(float dt) {
        KeyState.left_pressed  = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        KeyState.right_pressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        KeyState.up_pressed    = Gdx.input.isKeyPressed(Input.Keys.UP);
        KeyState.down_pressed  = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        KeyState.space_pressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        worldCamera.unproject(mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0));

        ui.update(dt);
        world.update(dt);
        particles.update(dt);

        float speed = 250;
        if      (KeyState.left_pressed)  cameraPos.add(-speed * dt, 0);
        else if (KeyState.right_pressed) cameraPos.add( speed * dt, 0);
        if      (KeyState.up_pressed)    cameraPos.add( 0,  speed * dt);
        else if (KeyState.down_pressed)  cameraPos.add( 0, -speed * dt);

        if      (cameraPos.x < world.bounds.x + worldCamera.viewportWidth / 2f)                        cameraPos.x = world.bounds.x + worldCamera.viewportWidth / 2f;
        else if (cameraPos.x > world.bounds.x - worldCamera.viewportWidth / 2f + world.bounds.width)   cameraPos.x = world.bounds.x - worldCamera.viewportWidth / 2f + world.bounds.width;
        if      (cameraPos.y < world.bounds.y + worldCamera.viewportHeight / 2f)                       cameraPos.y = world.bounds.y + worldCamera.viewportHeight / 2f;
        else if (cameraPos.y > world.bounds.y - worldCamera.viewportHeight / 2f + world.bounds.height) cameraPos.y = world.bounds.y - worldCamera.viewportHeight / 2f + world.bounds.height;
        worldCamera.position.set(cameraPos, 0);
        worldCamera.zoom = cameraZoom.floatValue();
        worldCamera.update();

        handleZoomInOut();


        // draw some sparkle for nice
        accum += dt;
        if (accum > 0.025f) {
            accum -= 0.025f;
            particles.sparkle(mousePos.x, mousePos.y);
        }
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
            batch.setColor(Color.MAGENTA);
            assets.debugNinePatch.draw(batch, 10, 10, windowCamera.viewportWidth - 20, windowCamera.viewportHeight - 20);
            batch.setColor(Color.WHITE);

            float margin = 10;
            float size = 3 * 16;

            batch.setColor(0.2f, 0.2f, 0.2f, 0.5f);
            batch.draw(assets.pixel, 10, 10, 3 * size, 3 * size);
            batch.setColor(Color.SKY);
            assets.debugNinePatch.draw(batch, 10, 10, 3 * size, 3 * size);
            batch.setColor(Color.WHITE);

            batch.setColor(KeyState.left_pressed ? Color.LIME : Color.WHITE);
            batch.draw(inputPrompts.get(InputPrompts.Type.key_light_arrow_left), margin, margin + size, size, size);

            batch.setColor(KeyState.down_pressed ? Color.LIME : Color.WHITE);
            batch.draw(inputPrompts.get(InputPrompts.Type.key_light_arrow_down), margin + size, margin + size, size, size);

            batch.setColor(KeyState.right_pressed ? Color.LIME : Color.WHITE);
            batch.draw(inputPrompts.get(InputPrompts.Type.key_light_arrow_right), margin + 2 * size, margin + size, size, size);

            batch.setColor(KeyState.up_pressed ? Color.LIME : Color.WHITE);
            batch.draw(inputPrompts.get(InputPrompts.Type.key_light_arrow_up), margin + size, margin + 2 * size, size, size);

            batch.setColor(KeyState.space_pressed ? Color.LIME : Color.WHITE);
            batch.draw(inputPrompts.get(InputPrompts.Type.key_light_spacebar_1), margin, margin, size, size);
            batch.draw(inputPrompts.get(InputPrompts.Type.key_light_spacebar_2), margin + size, margin, size, size);
            batch.draw(inputPrompts.get(InputPrompts.Type.key_light_spacebar_3), margin + 2 * size, margin, size, size);

            batch.setColor(Color.WHITE);

            ui.draw(batch);
        }
        batch.end();
    }

    private void handleZoomInOut() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !isZooming) {
            isZooming = true;
            game.audio.playSound(Audio.Sounds.example);
            if (zoomedIn) {
                Timeline.createParallel()
                        .push(
                                Tween.to(cameraZoom, -1, 1f).target(1f)
                        )
                        .push(
                                Tween.to(cameraPos, Vector2Accessor.XY, 1f)
                                        .target(world.bounds.width / 2f, world.bounds.height / 2f)
                        )
                        .setCallback((type, source) -> {
                            zoomedIn = false;
                            isZooming = false;
                        })
                        .start(tween);
            } else {
                Timeline.createParallel()
                        .push(
                                Tween.to(cameraZoom, -1, 1f).target(0.5f)
                        )
                        .push(
                                Tween.to(cameraPos, Vector2Accessor.XY, 1f)
                                        .target(world.bounds.width / 2f, world.bounds.height / 2f)
                        )
                        .setCallback((type, source) -> {
                            zoomedIn = true;
                            isZooming = false;
                        })
                        .start(tween);
            }
        }
    }

}
