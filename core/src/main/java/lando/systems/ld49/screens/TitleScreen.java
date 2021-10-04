package lando.systems.ld49.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld49.Audio;
import lando.systems.ld49.Config;
import lando.systems.ld49.Main;
import lando.systems.ld49.utils.Utils;
import lando.systems.ld49.world.Flame;

public class TitleScreen extends BaseScreen {

    Vector2 placeholder;
    Vector2 vel;
    int size = 200;
    float accum = 0;
    Array<Flame> flames = new Array<>();

    public TitleScreen(Main game){
        super(game);
        placeholder = new Vector2(MathUtils.random(Config.window_width - size), MathUtils.random(Config.window_height - size));
        vel = new Vector2(MathUtils.random(-1f, 1), MathUtils.random(-1f, 1f)).nor().scl(100);
        // TODO: not sure if we want to keep the fire on this screen
        for (int i = 0; i < 10; i ++){
            flames.add(new Flame(
                    -50 + i * 150, 0,
                    250, 200,
                    new Color(.2f, .6f, .1f, 0.55f),
                    new Color(.2f, .8f, .9f, 1)
            ));
        }
        game.audio.fadeMusic(Audio.Musics.introMusic);
//        game.audio.playMusic(Audio.Musics.outroMusic);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        accum += dt;
        if (!exitingScreen && (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ENTER))){
            exitingScreen = true;
            game.audio.stopMusic();
            game.setScreen(new StoryScreen(game), assets.doorwayShader, 3f);
        }
        // TODO: remove these shortcuts
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)){
            game.setScreen(new EndScreen(game), assets.cubeShader, 3f);
            game.audio.stopMusic();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.O)){
            game.setScreen(new GameOverScreen(game), assets.cubeShader, 3f);
            game.audio.stopMusic();
        }

        for (Flame flame : flames) {
            flame.update(dt*.5f);
        }
    }

    private Color textColor = new Color();
    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        batch.setProjectionMatrix(shaker.getCombinedMatrix());
        batch.setColor(Color.WHITE);
        batch.draw(assets.backgrounds.titleImage, 0,0, Config.window_width, Config.window_height);

        textColor = Utils.hsvToRgb(accum/2f, .5f, 1.0f, textColor);
        assets.pixelFont16.getData().setScale(1f + MathUtils.sin(accum) * .1f);
        assets.pixelFont16.setColor(Color.BLACK);
        assets.pixelFont16.draw(batch, "Click to Start!", 151, 329, 400, Align.center, false);
        assets.pixelFont16.setColor(textColor);
        assets.pixelFont16.draw(batch, "Click to Start!", 150, 330, 400, Align.center, false);
        assets.pixelFont16.setColor(Color.WHITE);
        assets.pixelFont16.getData().setScale(1f);
        batch.end();

        for (Flame flame : flames) {
            flame.render(batch);
        }

    }
}
