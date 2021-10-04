package lando.systems.ld49.screens;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import lando.systems.ld49.Audio;
import lando.systems.ld49.Config;
import lando.systems.ld49.Main;
import lando.systems.ld49.particles.Particles;
import lando.systems.ld49.ui.UIGameOver;
import lando.systems.ld49.utils.Utils;
import lando.systems.ld49.utils.accessors.Vector2Accessor;
import lando.systems.ld49.utils.typinglabel.TypingLabel;
import lando.systems.ld49.world.Presidente;
import lando.systems.ld49.world.Stats;

public class GameOverScreen extends GameScreen {

    private final Animation<TextureRegion> idleAnimation;
    private TypingLabel signatureTypingLabel;
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
    private TypingLabel statsTypingLabel;
    private boolean isTalkingDone = false;
    private TextureRegion talkingTextureRegion;
    private Animation<TextureRegion> talkingAnimation;
    private float animationTimer = 0f;
//    private UIGameOver ui;
//    private boolean triggerComm = true;

    public GameOverScreen (Main game) {
        super(game);

        String moneyEarned = Utils.intToDollarString(Stats.moneyEarned);
        String moneySpent = Utils.intToDollarString(Stats.moneySpent);
        String netMoney = Utils.intToDollarString(Stats.moneyEarned - Stats.moneySpent);
        float secondsRan = Stats.secondsRan;
        talkingAnimation = new Animation<>(0.2f, assets.atlas.findRegions("people/dole-presidente/dole-presidente-talk/dole-presidente-talk"), Animation.PlayMode.LOOP);
        idleAnimation = new Animation<>(0.2f, assets.atlas.findRegions("people/dole-presidente/dole-presidente-idle/dole-presidente-idle"), Animation.PlayMode.LOOP);
        talkingTextureRegion = talkingAnimation.getKeyFrame(0);
        String stats = "Money Earned:\n" + moneyEarned + "\n\nMoney Spent:\n" + moneySpent + "\n\nMoney grifted: \n" + netMoney + "\n\nSurvived for: \n" + secondsRan + "s\n\n\n\n\n\n\n\n\n\n\n{RAINBOW}Worth It.{ENDRAINBOW}";
        String clickNext = "{COLOR=red}{JUMP=.2}Next Page{ENDJUMP}";
        String page1 = "{COLOR=black}People of Potassia,\n"+
                "I thank you for your generosity \n"+
                "during Cavendish’s time on your island.\n\n"+

                "Without your hard work and sacrifice,\n"+
                "I wouldn’t have been able to afford \n"+
                "that second solid {GRADIENT=gold;black}gold bidet{ENDGRADIENT},\n"+
                "which I will use to rinse out my {JUMP=.2}{COLOR=brown}banananus{COLOR=black}{ENDJUMP} :).\n\n"+

                "While we would love to continue\n"+
                "to work with you fine people,\n"+
                "I regret to inform you that,\n"+
                "circumstances no longer\n"+
                "make that a possibility :)";
        String page2 = "\n{COLOR=black}Specifically, the circumstances\n"+
                "that we completely {GRADIENT=purple;forest}{SICK}ratfucked{ENDSICK}{ENDGRADIENT}{COLOR=black} your \n"+
                "entire island through negligence\n"+
                "and raw greed, and {GRADIENT=red;orange}{WAVE=0.2}plundered {ENDWAVE}{ENDGRADIENT}{COLOR=black}\n"+
                "every morsel of natural abundance\n"+
                "until it was no longer profitable\n"+
                "thus rendering the land unlivable :(\n\n"+
                "We hope you have enjoyed\n"+
                "your partnership with Cavendish\n"+
                "as much as we have. \n";
        String page3 = "\n{COLOR=black}Also you probably shouldn’t drink\n"+
                "any of the {COLOR=blue}water {Color=black}here for a while. \n"+
                "A {SICK}looooong{ENDSICK} while.\n"+
                "Like, centuries probably.\n"+
                "{GRADIENT=forest;green}Radiation{ENDGRADIENT}, and whatever.\n\n"+
                "Anyway, thanks for the memories,\n"+
                "Spread {GRADIENT=blue;red}{SICK}#goals{ENDSICK}{ENDGRADIENT}{COLOR=black} and {GRADIENT=red;blue}{SICK}#blessed {ENDSICK}{ENDGRADIENT}\n"+
                "Ttyl, fellow banana peeps :)\n\n\n";
        String signature = "{COLOR=black}Yours Truly, \nDole Presidente";

        clickNextTypingLabel = new TypingLabel(game.assets.deutschFont, clickNext, Config.window_width / 2 - 200f, 120f);
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

        page3TypingLabel = new TypingLabel(game.assets.papyrusFont, page3, 100f, Config.window_height - 80f);
        page3TypingLabel.setWidth(Config.window_width / 2 - 120f);
        page3TypingLabel.setFontScale(.8f);
        page3TypingLabel.setLineAlign(Align.left);

        signatureTypingLabel = new TypingLabel(game.assets.deutschFont, signature, 110f, 180f);
        signatureTypingLabel.setWidth(Config.window_width / 2 - 120f);
        signatureTypingLabel.setFontScale(.8f);
        signatureTypingLabel.setLineAlign(Align.left);

        statsTypingLabel = new TypingLabel(game.assets.pixelFont16, stats, Config.window_width / 3 + 160f, Config.window_height - 30f);
        statsTypingLabel.setWidth(Config.window_width / 3);
        statsTypingLabel.setFontScale(.8f);
        statsTypingLabel.setLineAlign(Align.center);

        currentTypingLabel = page1TypingLabel;

        presidente = new Presidente(game.assets, Config.window_width / 2, 100f, this);
        presidente.animation = game.assets.presidenteIdleAnim;
        game.audio.fadeMusic(Audio.Musics.outroMusic);
//        ui = new UIGameOver(this, uiElements);

    }

    public void presidenteShitTalks(float dt) {
//        if (triggerComm) {
//            ui.toggleComms();
//            triggerComm = false;
//        }
        presidente.scale = presidenteScale;
        presidente.animation = assets.presidenteIdleAnim;
        animationTimer+=dt;
        if (!currentTypingLabel.hasEnded()) {
            talkingTextureRegion = talkingAnimation.getKeyFrame(animationTimer);
        } else {
            talkingTextureRegion = idleAnimation.getKeyFrame(animationTimer);
        }

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
                    showNext = false;
                    break;
            }
        }
        if (currentPage == 3 && currentTypingLabel.hasEnded()) {
            signatureTypingLabel.update(dt);
        }
        if (Gdx.input.justTouched() && showNext == false){
            if (signatureTypingLabel.hasEnded() && currentPage > 2) {
                isTalkingDone = true;
            }
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
            presidenteScale -= 1.4 * dt;
        }
        if (presidente.pos.y < 200f) {
            presidente.pos.set(presidente.pos.x, presidente.pos.y + 1f);
        }
        if (presidente.pos.x < camera.viewportWidth - 200f) {
            presidente.pos.set(presidente.pos.x + 2f, presidente.pos.y);
        } else {
            presidente.animation = assets.presidenteIdleAnim;
            if (!once) {
                once = true;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        presidente.animation = assets.presidenteRunAnim;
                        presidente.forceEmote = true;
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
        presidenteShitTalks(dt);
        if (isTalkingDone) {
            presidente.enableEmote = false;
            presidenteEscapes(dt);
        } else {
            presidente.enableEmote = true;
        }
        particles.update(dt);
        presidente.update(dt);
        statsTypingLabel.update(dt);
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
            if (currentPage == 3 && currentTypingLabel.hasEnded()) {
                signatureTypingLabel.render(batch);
            }
            batch.draw(talkingTextureRegion, Config.window_width / 2 - 30f, 130f, -200f, 200f);
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
            statsTypingLabel.render(batch);
            batch.setColor(Color.WHITE);
        }
        batch.end();
    }

}
