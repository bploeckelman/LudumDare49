package lando.systems.ld49.screens;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld49.Assets;
import lando.systems.ld49.Config;
import lando.systems.ld49.Main;
import lando.systems.ld49.particles.Particles;

public abstract class BaseScreen extends InputAdapter {
    public final Main game;
    public final Assets assets;
    public final TweenManager tween;
    public final SpriteBatch batch;
    public final Particles particles;

    public OrthographicCamera worldCamera;
    public OrthographicCamera hudCamera;
//    public ScreenShakeCameraController shaker;
//
//    public final PlayerInput playerInput = new PlayerInput();

    public BaseScreen(Main game) {
        this.game = game;
        this.assets = game.assets;
        this.tween = game.tween;
        this.batch = assets.batch;
        this.particles = new Particles(assets);

        this.worldCamera = new OrthographicCamera();
        this.worldCamera.setToOrtho(false, Config.viewport_width, Config.viewport_height);
        this.worldCamera.update();

        this.hudCamera = new OrthographicCamera();
        this.hudCamera.setToOrtho(false, Config.window_width, Config.window_height);
        this.hudCamera.update();
//        this.shaker = new ScreenShakeCameraController(worldCamera);


        Controllers.clearListeners();
//        Controllers.addListener(playerInput);
    }

    public void update(float dt) {
//        shaker.update(dt);
//        particles.update(dt);
//        playerInput.update(dt);
    }


    public void renderFrameBuffers(SpriteBatch batch )  {
    }

    public abstract void render(SpriteBatch batch);

//    public long playSound(Audio.Sounds sound) {
//        return this.game.audio.playSound(sound);
//    }
//
//    public Music playMusic(Audio.Musics music) {
//        return this.game.audio.playMusic(music, true, true);
//    }

}