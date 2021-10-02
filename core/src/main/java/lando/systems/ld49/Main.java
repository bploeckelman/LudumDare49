package lando.systems.ld49;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld49.screens.BaseScreen;
import lando.systems.ld49.screens.GameScreen;
import lando.systems.ld49.screens.LaunchScreen;
import lando.systems.ld49.utils.Time;
import lando.systems.ld49.utils.accessors.*;

public class Main extends ApplicationAdapter {

    public static Main game;
    public Assets assets;
    public TweenManager tween;
    public SpriteBatch batch;
    public Audio audio;

    private BaseScreen currentScreen;
    private BaseScreen nextScreen;
    private MutableFloat transitionPercent;
    private FrameBuffer transitionFBO;
    private FrameBuffer originalFBO;
    Texture originalTexture;
    Texture transitionTexture;
    ShaderProgram transitionShader;
    boolean transitioning;

    @Override
    public void create() {
        game = this;
        Time.init();

        transitionPercent = new MutableFloat(0);
        transitionFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Config.window_width, Config.window_height, false);
        transitionTexture = transitionFBO.getColorBufferTexture();

        originalFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Config.window_width, Config.window_height, false);
        originalTexture = originalFBO.getColorBufferTexture();

        transitioning = false;

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

        audio = new Audio(this);

        if (Gdx.app.getType() == Application.ApplicationType.WebGL || Config.show_launch_screen) {
            setScreen(new LaunchScreen(this));
        } else {
            setScreen(new GameScreen(this));
        }
    }

    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        // update global timer
        Time.delta = Gdx.graphics.getDeltaTime();

        // update code that always runs (regardless of pause)
        currentScreen.alwaysUpdate(Time.delta);

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
        currentScreen.update(Time.delta);
    }

    @Override
    public void render() {
        update();
        ScreenUtils.clear(Color.DARK_GRAY);

        currentScreen.renderFrameBuffers(assets.batch);

        if (nextScreen != null) {
            nextScreen.update(Time.delta);
            nextScreen.renderFrameBuffers(assets.batch);
            transitionFBO.begin();
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            nextScreen.render(assets.batch);
            transitionFBO.end();

            originalFBO.begin();
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            currentScreen.render(assets.batch);
            originalFBO.end();

            assets.batch.setShader(transitionShader);
            assets.batch.begin();
            originalTexture.bind(1);
            transitionShader.setUniformi("u_texture1", 1);
            transitionTexture.bind(0);
            transitionShader.setUniformf("u_percent", transitionPercent.floatValue());
            assets.batch.setColor(Color.WHITE);
            assets.batch.draw(transitionTexture, 0,0, Config.window_width, Config.window_height);
            assets.batch.end();
            assets.batch.setShader(null);
        } else {
            currentScreen.render(assets.batch);
        }
    }

    public void setScreen(BaseScreen screen) {
        setScreen(screen, null, .5f);
    }

    public void setScreen(final BaseScreen newScreen, ShaderProgram transitionType, float transitionSpeed) {
        if (nextScreen != null) return;
        if (transitioning) return; // only want one transition
        if (currentScreen == null) {
            currentScreen = newScreen;
        } else {
            transitioning = true;
            if (transitionType == null) {
                transitionShader = assets.randomTransitions.get(MathUtils.random(assets.randomTransitions.size - 1));
            } else {
                transitionShader = transitionType;
            }
            transitionPercent.setValue(0);
            Timeline.createSequence()
                    .pushPause(.1f)
                    .push(Tween.call((i, baseTween) -> nextScreen = newScreen))
                    .push(Tween.to(transitionPercent, 1, transitionSpeed)
                            .target(1))
                    .push(Tween.call((i, baseTween) -> {
                        currentScreen = nextScreen;
                        nextScreen = null;
                        transitioning = false;
                    }))
                    .start(tween);
        }
    }

    public BaseScreen getScreen() {
        return currentScreen;
    }

    @Override
    public void dispose() {
        assets.dispose();
    }

}
