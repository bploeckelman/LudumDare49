package lando.systems.ld49.screens;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import lando.systems.ld49.Audio;
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
        presidente.animation = game.assets.presidenteRunAnim;
        game.audio.fadeMusic(Audio.Musics.outroMusic);



        String endText = "\n\n"+
                "People of Potassia,\n\n"+
                "I thank you for your generosity \n\n"+
                "during Cavendish’s time on your island.\n\n"+

                "Without your hard work and sacrifice,\n\n"+
                "I wouldn’t have been able to afford \n\n"+
                "that second solid gold bidet,\n\n"+
                "which I will use to rinse out my banananus.\n\n"+

                "While we would love to continue\n\n"+
                "to work with you fine people,\n\n"+
                "I regret to inform you that,\n\n"+
                "circumstances no longer\n\n"+
                "make that a possibility.\n\n"+
                "\n\n"+
                "Specifically, the circumstances\n\n"+
                "that we completely ratfucked your \n\n"+
                "entire island through negligence\n\n"+
                "and raw greed, and plundered\n\n"+
                "every morsel of natural abundance\n\n"+
                "until it was no longer profitable\n\n"+
                "thus rendering the land unlivable.\n\n"+
                "\n\n"+
                "We hope you have enjoyed\n\n"+
                "your partnership with Cavendish\n\n"+
                "as much as we have. \n\n"+
                "\n\n"+
                "Also you probably shouldn’t drink\n\n"+
                "any of the water here for a while. \n\n"+
                "A looooong while.\n\n"+
                "Like, centuries probably.\n\n"+
                "Radiation, and whatever.\n\n"+
                "\n\n"+
                "Anyway, thanks for the memories,\n\n"+
                "hashtag goals, hashtag blessed,\n\n"+
                "Ttyl, fellow banana peeps\n\n"+
                "\n\n"+
                "Dole Presidente";


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
