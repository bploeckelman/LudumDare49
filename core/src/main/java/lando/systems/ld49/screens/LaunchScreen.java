package lando.systems.ld49.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld49.Main;

public class LaunchScreen extends BaseScreen {
    public LaunchScreen(Main game) {
        super(game);
    }

    public void update(float dt) {
        if (Gdx.input.justTouched()){
            game.setScreen(new GameScreen(game));
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(Color.RED);

    }
}
