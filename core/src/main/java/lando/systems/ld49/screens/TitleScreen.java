package lando.systems.ld49.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld49.Audio;
import lando.systems.ld49.Config;
import lando.systems.ld49.Main;
import lando.systems.ld49.world.Flame;

public class TitleScreen extends BaseScreen {

    Vector2 placeholder;
    Vector2 vel;
    int size = 200;
    float accum = 0;
    Flame flame;

    public TitleScreen(Main game){
        super(game);
        placeholder = new Vector2(MathUtils.random(Config.window_width - size), MathUtils.random(Config.window_height - size));
        vel = new Vector2(MathUtils.random(-1f, 1), MathUtils.random(-1f, 1f)).nor().scl(100);
        // TODO: not sure if we want to keep the fire on this screen
        flame = new Flame(
                -200, 0,
                Config.window_width + 400, 250,
                new Color(1.0f, .8f, .5f, 0.75f),
                new Color(.8f, .1f, .1f, 0.75f)
        );
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

        flame.update(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        batch.setProjectionMatrix(shaker.getCombinedMatrix());
        batch.setColor(Color.WHITE);
        batch.draw(assets.backgrounds.titleImage, 0,0, Config.window_width, Config.window_height);
        batch.end();

        flame.render(batch);

    }
}
