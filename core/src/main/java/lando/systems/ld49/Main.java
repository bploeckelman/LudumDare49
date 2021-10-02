package lando.systems.ld49;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld49.particles.Particles;
import lando.systems.ld49.utils.InputPrompts;
import lando.systems.ld49.utils.Time;
import lando.systems.ld49.utils.accessors.*;
import lando.systems.ld49.world.World;

public class Main extends ApplicationAdapter {

    Assets assets;
    TweenManager tween;
    SpriteBatch batch;

    OrthographicCamera worldCamera;
    OrthographicCamera windowCamera;

    World world;
    TextureRegion texture;
    Particles particles;
    InputPrompts inputPrompts;

    Vector2 cameraPos = new Vector2();
    Vector3 mousePos = new Vector3();
    float accum = 0;

    @Override
    public void create() {
        Time.init();

        assets = new Assets();
        batch = assets.batch;

        tween = new TweenManager();
        Tween.setWaypointsLimit(4);
        Tween.setCombinedAttributesLimit(4);
        Tween.registerAccessor(Color.class, new ColorAccessor());
        Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
        Tween.registerAccessor(Vector2.class, new Vector2Accessor());
        Tween.registerAccessor(Vector3.class, new Vector3Accessor());
        Tween.registerAccessor(OrthographicCamera.class, new CameraAccessor());

        worldCamera = new OrthographicCamera();
        worldCamera.setToOrtho(false, Config.viewport_width, Config.viewport_height);
        worldCamera.update();

        windowCamera = new OrthographicCamera();
        windowCamera.setToOrtho(false, Config.window_width, Config.window_height);
        windowCamera.update();

        texture = assets.atlas.findRegion("lando");
        world = new World(assets);
        particles = new Particles(assets);
        inputPrompts = new InputPrompts(assets);

        cameraPos = new Vector2(world.bounds.width / 2, world.bounds.height / 2);
        worldCamera.position.set(cameraPos, 0);
    }

    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        // update global timer
        Time.delta = Gdx.graphics.getDeltaTime();

        // update code that always runs (regardless of pause)
        // ...

        // handle a pause
        if (Time.pause_timer > 0) {
            Time.pause_timer -= Time.delta;
            if (Time.pause_timer <= -0.0001f) {
                Time.delta = -Time.pause_timer;
            } else {
                // skip updates if we're paused
                return;
            }
        }
        Time.millis += Time.delta;
        Time.previous_elapsed = Time.elapsed_millis();

        // update systems
        tween.update(Time.delta);
        particles.update(Time.delta);

        world.update(Time.delta);

        float speed = 250;
        if      (Gdx.input.isKeyPressed(Input.Keys.LEFT))  cameraPos.add(-speed * Time.delta, 0);
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) cameraPos.add( speed * Time.delta, 0);
        if      (Gdx.input.isKeyPressed(Input.Keys.UP))    cameraPos.add( 0,  speed * Time.delta);
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  cameraPos.add( 0, -speed * Time.delta);

        if      (cameraPos.x < world.bounds.x + worldCamera.viewportWidth / 2f)                        cameraPos.x = world.bounds.x + worldCamera.viewportWidth / 2f;
        else if (cameraPos.x > world.bounds.x - worldCamera.viewportWidth / 2f + world.bounds.width)   cameraPos.x = world.bounds.x - worldCamera.viewportWidth / 2f + world.bounds.width;
        if      (cameraPos.y < world.bounds.y + worldCamera.viewportHeight / 2f)                       cameraPos.y = world.bounds.y + worldCamera.viewportHeight / 2f;
        else if (cameraPos.y > world.bounds.y - worldCamera.viewportHeight / 2f + world.bounds.height) cameraPos.y = world.bounds.y - worldCamera.viewportHeight / 2f + world.bounds.height;
        worldCamera.position.set(cameraPos, 0);

        worldCamera.update();
        windowCamera.update();

        // draw some sparkle for nice
        accum += Time.delta;
        if (accum > 0.025f) {
            accum -= 0.025f;
            worldCamera.unproject(mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            particles.sparkle(mousePos.x, mousePos.y);
        }
    }

    @Override
    public void render() {
        update();

        ScreenUtils.clear(Color.DARK_GRAY);

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
            batch.draw(assets.pixel, 10, 10, 3 * size, 2 * size);
            batch.setColor(Color.SKY);
            assets.debugNinePatch.draw(batch, 10, 10, 3 * size, 2 * size);
            batch.setColor(Color.WHITE);

            batch.draw(inputPrompts.get(InputPrompts.Type.key_light_arrow_left), margin, margin, size, size);
            batch.draw(inputPrompts.get(InputPrompts.Type.key_light_arrow_down), margin + size, margin, size, size);
            batch.draw(inputPrompts.get(InputPrompts.Type.key_light_arrow_right), margin + 2 * size, margin, size, size);
            batch.draw(inputPrompts.get(InputPrompts.Type.key_light_arrow_up), margin + size, margin + size, size, size);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        assets.dispose();
    }

}
