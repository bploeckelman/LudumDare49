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
import lando.systems.ld49.utils.Time;
import lando.systems.ld49.utils.accessors.*;

public class Main extends ApplicationAdapter {

    Assets assets;
    TweenManager tween;
    SpriteBatch batch;

    TextureRegion texture;

    @Override
    public void create() {
        Time.init();

        assets = new Assets();

        tween = new TweenManager();
        Tween.setWaypointsLimit(4);
        Tween.setCombinedAttributesLimit(4);
        Tween.registerAccessor(Color.class, new ColorAccessor());
        Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
        Tween.registerAccessor(Vector2.class, new Vector2Accessor());
        Tween.registerAccessor(Vector3.class, new Vector3Accessor());
        Tween.registerAccessor(OrthographicCamera.class, new CameraAccessor());

        batch = assets.batch;
        texture = assets.atlas.findRegion("lando");
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
        // ...
    }

    @Override
    public void render() {
        update();

        ScreenUtils.clear(Color.YELLOW);
        batch.begin();
        {
            batch.draw(texture, 0, 0, Config.window_width, Config.window_height);

            assets.debugNinePatch.draw(batch, Config.window_width / 2f - Config.viewport_width / 2f, Config.window_height - Config.viewport_height, Config.viewport_width, Config.viewport_height);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        assets.dispose();
    }

}
