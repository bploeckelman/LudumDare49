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

public class TitleScreen extends BaseScreen {

    Vector2 placeholder;
    Vector2 vel;
    int size = 200;

    public TitleScreen(Main game){
        super(game);
        placeholder = new Vector2(MathUtils.random(Config.window_width - size), MathUtils.random(Config.window_height - size));
        vel = new Vector2(MathUtils.random(-1f, 1), MathUtils.random(-1f, 1f)).nor().scl(100);
        game.audio.playMusic(Audio.Musics.example);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            game.setScreen(new GameScreen(game), assets.cubeShader, 1f);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)){
            game.setScreen(new EndScreen(game), assets.cubeShader, 1f);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        //batch.setProjectionMatrix(shaker.getCombinedMatrix());
        batch.setColor(Color.WHITE);
        batch.draw(assets.backgrounds.titleImage, 0,0, Config.window_width, Config.window_height);
        batch.end();
    }
}
