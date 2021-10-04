package lando.systems.ld49.screens;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import lando.systems.ld49.Audio;
import lando.systems.ld49.Config;
import lando.systems.ld49.Main;
import lando.systems.ld49.particles.Particles;
import lando.systems.ld49.ui.UIGameOver;
import lando.systems.ld49.utils.accessors.Vector2Accessor;
import lando.systems.ld49.utils.typinglabel.TypingLabel;
import lando.systems.ld49.world.Presidente;

public class GameOverScreen extends GameScreen {

    private TypingLabel clickNextTypingLabel;
    private TypingLabel page1TypingLabel;
    private Presidente presidente;
    private float presidenteScale = 6f;
    private OrthographicCamera camera = windowCamera;
    private float colorTint = 1f;
    private boolean once = false;
    private boolean showNext = false;
    private TypingLabel currentTypingLabel;
    private int currentPage = 1;
    private TypingLabel page2TypingLabel;
    private TypingLabel page3TypingLabel;
    private TypingLabel nextTypingLabel;
    private boolean isTalkingDone = false;
    private TextureRegion talkingTextureRegion;
//    private UIGameOver ui;
//    private boolean triggerComm = true;

    public GameOverScreen (Main game) {
        super(game);
        String clickNext = "{COLOR=red}{JUMP=.2}Next Page{ENDJUMP}";
        String page1 = "{COLOR=black}People of Potassia,\n"+
                "I thank you for your generosity \n"+
                "during Cavendish’s time on your island.\n\n"+

                "Without your hard work and sacrifice,\n"+
                "I wouldn’t have been able to afford \n"+
                "that second solid {RAINBOW}gold bidet{ENDRAINBOW},\n"+
                "which I will use to rinse out my {JUMP=.2}banananus{ENDJUMP} :).\n\n"+

                "While we would love to continue\n"+
                "to work with you fine people,\n"+
                "I regret to inform you that,\n"+
                "circumstances no longer\n"+
                "make that a possibility.\n";
        String page2 = "\n{COLOR=black}Specifically, the circumstances\n"+
                "that we completely ratfucked your \n"+
                "entire island through negligence\n"+
                "and raw greed, and plundered\n"+
                "every morsel of natural abundance\n"+
                "until it was no longer profitable\n"+
                "thus rendering the land unlivable :(\n\n"+
                "We hope you have enjoyed\n"+
                "your partnership with Cavendish\n"+
                "as much as we have. \n";
        String page3 = "\n{COLOR=black}Also you probably shouldn’t drink\n"+
                "any of the water here for a while. \n"+
                "A looooong while.\n"+
                "Like, centuries probably.\n"+
                "Radiation, and whatever.\n\n"+
                "Anyway, thanks for the memories,\n"+
                "hashtag goals, hashtag blessed,\n"+
                "Ttyl, fellow banana peeps\n\n"+
                "Dole Presidente";

        clickNextTypingLabel = new TypingLabel(game.assets.papyrusFont, clickNext, Config.window_width / 2 - 180f, 120f);
        clickNextTypingLabel.setWidth(Config.window_width / 2 - 120f);
        clickNextTypingLabel.setFontScale(.8f);
        clickNextTypingLabel.setLineAlign(Align.left);

        page1TypingLabel = new TypingLabel(game.assets.papyrusFont, page1, 90f, Config.window_height - 80f);
        page1TypingLabel.setWidth(Config.window_width / 2 - 120f);
        page1TypingLabel.setFontScale(.8f);
        page1TypingLabel.setLineAlign(Align.left);

        page2TypingLabel = new TypingLabel(game.assets.papyrusFont, page2, 90f, Config.window_height - 80f);
        page2TypingLabel.setWidth(Config.window_width / 2 - 120f);
        page2TypingLabel.setFontScale(.8f);
        page2TypingLabel.setLineAlign(Align.left);

        page3TypingLabel = new TypingLabel(game.assets.papyrusFont, page3, 90f, Config.window_height - 80f);
        page3TypingLabel.setWidth(Config.window_width / 2 - 120f);
        page3TypingLabel.setFontScale(.8f);
        page3TypingLabel.setLineAlign(Align.left);

        currentTypingLabel = page1TypingLabel;

        presidente = new Presidente(game.assets, Config.window_width / 2, 100f, this);
        presidente.animation = game.assets.presidenteIdleAnim;
        game.audio.playMusic(Audio.Musics.outroMusic, true);
//        ui = new UIGameOver(this, uiElements);

    }

    public void presidenteShitTalks(float dt) {
//        if (triggerComm) {
//            ui.toggleComms();
//            triggerComm = false;
//        }
        presidente.scale = presidenteScale;
        presidente.animation = assets.presidenteIdleAnim;

        if (currentTypingLabel.hasEnded() && showNext == false) {
            //display next
            showNext = true;
            switch (currentPage) {
                case 1:
                    nextTypingLabel = page2TypingLabel;
                    break;
                case 2:
                    nextTypingLabel = page3TypingLabel;
                    break;
                case 3:
                    isTalkingDone = true;
                    showNext = false;
                    break;
            }
        }

        if (Gdx.input.justTouched() && showNext == false){
            currentTypingLabel.skipToTheEnd();
        }
        currentTypingLabel.update(dt);
        if (Gdx.input.justTouched() && showNext == true){
            currentTypingLabel = nextTypingLabel;
            currentPage++;
            showNext = false;
            currentTypingLabel.restart();
        }
    }

    public void presidenteEscapes(float dt) {
        presidente.scale = presidenteScale;
        presidente.animation = assets.presidenteRunAnim;
        if (presidenteScale > 1f) {
            presidenteScale -= 1.2* dt;
        }
        if (presidente.pos.y < 200f) {
            presidente.pos.set(presidente.pos.x, presidente.pos.y + 1f);
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
    }


    @Override
    public void update(float dt) {
        if (isTalkingDone) {
            presidenteEscapes(dt);
        } else {
            presidenteShitTalks(dt);
        }
        particles.update(dt);
        presidente.update(dt);
        if (showNext) {
            clickNextTypingLabel.update(dt);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(Color.BLACK);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
            particles.draw(batch, Particles.Layer.foreground);
            batch.draw(assets.parchment, 25f, 25f, camera.viewportWidth / 2, camera.viewportHeight - 50f);
            currentTypingLabel.render(batch);
            if (showNext) {
                clickNextTypingLabel.render(batch);
            }
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
