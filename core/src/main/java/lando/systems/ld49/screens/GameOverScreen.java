package lando.systems.ld49.screens;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import lando.systems.ld49.Config;
import lando.systems.ld49.Main;
import lando.systems.ld49.particles.Particles;
import lando.systems.ld49.utils.accessors.Vector2Accessor;
import lando.systems.ld49.world.Presidente;

public class GameOverScreen extends BaseScreen {

    private Presidente presidente;
    private float presidenteScale = 10f;
    private OrthographicCamera camera = windowCamera;
    private float colorTint = 1f;
    private boolean once = false;

    public GameOverScreen(Main game) {
        super(game);
        presidente = new Presidente(game.assets, 100f, 200f, this);
    }


    @Override
    public void update(float dt) {
        presidente.scale = presidenteScale;
        if (presidenteScale > 1f) {
            presidenteScale -= 1.2* dt;
        }
        if (presidente.pos.x < camera.viewportWidth - 200f) {
            presidente.pos.set(presidente.pos.x + 2f, presidente.pos.y);
        } else {
            if (!once) {
                once = true;
                presidente.animation = assets.ripelyIdleAnim;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        presidente.animation = assets.presidenteRunAnim;
                        Tween.to(presidente.pos, Vector2Accessor.X, 1)
                                .target(camera.viewportWidth)
                                .setCallback((type, source) ->
                                        game.setScreen(new EndScreen(game), assets.pizelizeShader, 3f)
                                )
                                .start(game.tween);
                    }
                }, 2.5f);
            }
        }
        if (colorTint > 0f) {
            colorTint -= 0.002f;
        }
        particles.update(dt);
        presidente.update(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(Color.BLACK);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
            particles.draw(batch, Particles.Layer.foreground);
            batch.setColor(Color.GREEN);
            batch.draw(assets.pixelRegion,camera.viewportWidth - 300f, camera.viewportHeight - 250f, 200f, 50f);
            batch.setColor(Color.WHITE);
            assets.font.getData().setScale(2f);
            assets.layout.setText(assets.font, "EXIT", Color.WHITE, 200f, Align.center, false);
            assets.font.draw(batch, assets.layout, camera.viewportWidth - 300f, camera.viewportHeight - 210f);
            assets.font.getData().setScale(1f);
            batch.draw(assets.pixelRegion, camera.viewportWidth - 300f, camera.viewportHeight - 600f, 200f, 300f);
            batch.setColor(colorTint, colorTint, colorTint, 1);
            presidente.render(batch);
            batch.setColor(Color.WHITE);
        }
        batch.end();
    }

}
