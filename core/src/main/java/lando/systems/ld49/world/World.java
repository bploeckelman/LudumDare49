package lando.systems.ld49.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld49.Assets;
import lando.systems.ld49.Audio;
import lando.systems.ld49.Config;
import lando.systems.ld49.Main;
import lando.systems.ld49.collision.CollisionManager;
import lando.systems.ld49.particles.Particles;
import lando.systems.ld49.screens.GameOverScreen;
import lando.systems.ld49.screens.GameScreen;

import java.util.Collections;
import java.util.Comparator;

public class World {

    public final GameScreen gameScreen;
    public final Assets assets;
    public final Rectangle bounds;

    Array<Cloud> clouds;

    private float animIdleState = 0f;
    private float animRunState = 0f;
    private Catapult catapult;
    public Array<Shot> shots = new Array<>();
    public Reactor reactor;
    private CollisionManager collisionManager;
    private Presidente presidente;
    private Array<Banana> bananas = new Array<>();
    private float animState1 = 0;
    private float animState2 = MathUtils.random(1, 2);
    private float animState3 = MathUtils.random(3, 10);
    private float ambianceSoundTime;
    private boolean isRiotInProgress = false;
    public enum BananaStatus {NORMAL, PREP_RIOT, RIOT, END_RIOT}
    public BananaStatus bananasStatus = BananaStatus.NORMAL;

    private float gameOverTimer =0;

    private final float bananaHammockLeft = 280;
    private final float bananaHammockBottom = 120;
    private final float bananaPopulation = 15;

    private float ciaTimer;
    private static boolean playFinalExplosion;
    private static boolean playFastMusic;
    private Flame backgroundFlame1;
    private Flame backgroundFlame2;

    public World(GameScreen screen) {
        this.gameScreen = screen;
        this.assets = screen.assets;
        this.bounds = new Rectangle(0, 0, 1024, 1024);
        catapult = new Catapult(this, bananaHammockLeft, bananaHammockBottom);
        reactor = new Reactor(this);
        collisionManager = new CollisionManager(this);
        for (int i = 0; i < bananaPopulation; i++) {
            bananas.add(new Banana(assets, MathUtils.random(30f, Config.viewport_width - 50f), MathUtils.random(30f, 150f), this));
        }
        ambianceSoundTime = MathUtils.random(5f, 10f);
        presidente = new Presidente(assets, bananaHammockLeft - 150f, bananaHammockBottom - 20f, gameScreen);
        presidente.scale = 2.5f;
        presidente.enableEmote = true;
        ciaTimer = 30;
        this.playFinalExplosion = true;
        this.playFastMusic = true;

        clouds =  new Array<Cloud>();
        for (int i = 0; i < 8; i ++){
            clouds.add(new Cloud(true));
        }
        backgroundFlame1 = new Flame(new Rectangle(295, 328, 75, 40), new Color(1f, 1f, .8f, .7f), new Color ( .6f, .3f, .3f, .3f));
        backgroundFlame2 = new Flame(new Rectangle(454, 380, 85, 45), new Color(1f, 1f, .8f, .7f), new Color ( .6f, .3f, .3f, .3f));
    }

    private float smokeAccum = 0;
    public void update(float dt, boolean pause) {
        ambianceSoundTime-= dt;
        if (ambianceSoundTime <= 0){
            gameScreen.game.audio.playSound(Audio.Sounds.steamHiss, 0.1f);
            ambianceSoundTime = MathUtils.random(4f, 10f);
        }
        animIdleState += dt;
        animRunState += dt;
        animState1 += dt;
        animState2 += dt;
        animState3 += dt;

        // update clouds on pause
        updateClouds(dt);
        backgroundFlame1.update(dt);
        backgroundFlame2.update(dt);

        smokeAccum += dt;
//        if (smokeAccum > 0.1f) {
//            smokeAccum = 0;
            gameScreen.particles.addSmokeStackSmoke(325, 390);
            gameScreen.particles.addSmokeStackSmoke(505, 450);
//        }

        // Things that shouldn't run when paused should be here
        if (pause) return;

        Stats.secondsRan += dt;

        if (!gameScreen.ui.commsOpen) {
            ciaTimer -= dt;
        }
        if (ciaTimer <= 0){
            ciaTimer = MathUtils.random(45f, 60f);
            gameScreen.ui.toggleComms();
            makeBananasPrepRiot();
        }
        reactor.update(dt);

        if (reactor.getStructurePercent() >= 0.85f || reactor.getTemperaturePercent() >= 0.85f) {
            if (World.playFastMusic) {
                World.playFastMusic = false;
                gameScreen.game.audio.stopMusic();
                gameScreen.game.audio.playMusic(Audio.Musics.fastMusic);
            }
        }

        if (reactor.getStructurePercent() >= 1.0f || reactor.getTemperaturePercent() >= 1.0f) {
            gameScreen.particles.addSmoke(MathUtils.random(590f, 996f), MathUtils.random(130f, 480f));
            gameScreen.shaker.addDamage(100);

            if (MathUtils.random(1f) > .9f){
                gameScreen.particles.addLargeSmoke(MathUtils.random(590f, 996f), MathUtils.random(130f, 480f));
                gameScreen.game.audio.playSound(Audio.Sounds.alarm, 0.3F);
                gameScreen.game.audio.playSound(Audio.Sounds.fire, 0.5F);

                if (World.playFinalExplosion) {
                    World.playFinalExplosion = false;
                    gameScreen.game.audio.playSound(Audio.Sounds.explosions, 1.5F);
                }
            }
            gameOverTimer += dt;
            if (gameOverTimer > 10f) {
                if (!gameScreen.exitingScreen) {
                    gameScreen.exitingScreen = true;
                    gameScreen.game.setScreen(new GameOverScreen(gameScreen.game), Main.game.assets.heartShader, 3f);
                }
            }
            return;
        }
        catapult.update(dt, gameScreen);
        collisionManager.solve(dt);

        for (int i = shots.size-1; i >=0; i--){
            Shot shot = shots.get(i);
            shot.update(dt);
            if (shot.remove) {
                shots.removeIndex(i);
            }
        }
        for (int i = bananas.size-1; i>=0; i--) {
            Banana banana = bananas.get(i);
            banana.update(dt);
        }
        bananas.sort(Collections.reverseOrder(Comparator.comparing(banana -> banana.pos.y)));
        presidente.update(dt);
    }

    private void updateClouds(float dt) {
        for (int i = clouds.size -1; i >= 0; i--){
            clouds.get(i).update(dt);
            if (clouds.get(i).offScreen){
                clouds.removeIndex(i);
                clouds.add(new Cloud(false));
            }
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(assets.backgrounds.empty, bounds.x - 300, bounds.y - 200, bounds.width + 600, bounds.height);

        // dirt
        float groundLevel = 350;
        float bottom = bounds.y - 200;
        batch.setColor(198 / 255f, 156 / 255f, 108 / 255f, 1);
        batch.draw(assets.pixelRegion, bounds.x - 300, bottom, bounds.width + 600, groundLevel);
        batch.setColor(Color.WHITE);

        // background nuclear plant
        float scale = 2f;
        float width  = scale * assets.backgrounds.nuclearPlant.getRegionWidth();
        float height = scale * assets.backgrounds.nuclearPlant.getRegionHeight();
        batch.draw(assets.backgrounds.nuclearPlant,
                bounds.x + bounds.width  / 2f - width  / 2f + 10,
                bounds.y + bounds.height / 2f - height,
                width, height);

        backgroundFlame1.render(batch);
        backgroundFlame2.render(batch);

        // background trees
        float horizon = bottom + groundLevel;
        batch.draw(assets.treesIdle.getKeyFrame(animState1), bounds.x + 20, horizon);
        batch.draw(assets.treesIdle.getKeyFrame(animState2), bounds.x + bounds.width - 100, horizon);

        // litterburg clouds
        for (Cloud cloud: clouds){
            cloud.render(batch);
        }

        // trees with catapult attached
        Animation<TextureRegion> catapultTreesAnim = catapult.isHeld() ? assets.treesActive : assets.treesIdle;
        batch.draw(catapultTreesAnim.getKeyFrame(animState3), bananaHammockLeft - 135, bananaHammockBottom - 20);

        // other random plant trash
        batch.draw(assets.grassA.getKeyFrame(animState1), bounds.x + 20, horizon);
        batch.draw(assets.grassB.getKeyFrame(animState2), bounds.x + 450, horizon);
        batch.draw(assets.grassC.getKeyFrame(animState2), bananaHammockLeft - 20, bananaHammockBottom - 20);
        batch.draw(assets.grassD.getKeyFrame(animState3), bananaHammockLeft + 380, bananaHammockBottom - 20);
        batch.draw(assets.bushA.getKeyFrame(animState1), bananaHammockLeft + 80, bananaHammockBottom - 20);
        batch.draw(assets.bushB.getKeyFrame(animState2), 350, horizon);

        // awesome sign
        batch.draw(assets.bananaHammockSign, bananaHammockLeft - 160, bananaHammockBottom);

        // foreground stuff
        for (Banana banana : bananas) {
            if (!banana.isRioting && !banana.isFinishingRiot) {
                banana.render(batch);
            }
        }

        gameScreen.particles.draw(batch, Particles.Layer.background);

        reactor.renderBackground(batch);

        gameScreen.particles.draw(batch, Particles.Layer.middle);
        reactor.render(batch);

        for (Banana banana : bananas) {
            if (banana.isRioting || banana.isFinishingRiot) {
                banana.render(batch);
            }
        }

        presidente.render(batch);
        catapult.render(batch);

        for (Shot shot: shots) {
            shot.render(batch);
        }

        reactor.renderDebug(batch);
    }

    public void addShot(Shot shot) {
        shots.add(shot);
    }

    public void makeBananasHappy() {
        for (Banana banana : bananas) {
            banana.beHappy(10f);
        }
        bananasStatus = BananaStatus.NORMAL;
    }

    public void makeBananasPrepRiot() {
        bananasStatus = BananaStatus.PREP_RIOT;
        for (Banana banana : bananas) {
            banana.setupPrepRiot(true, new Vector2(bananaHammockLeft + 50f, bananaHammockBottom - 20f));
        }
    }

    public void makeBananasRiot() {
        bananasStatus = BananaStatus.RIOT;
        gameScreen.game.audio.playSound(Audio.Sounds.bananaMob, 0.5f);

        for (Banana banana : bananas) {
            banana.startRiot(true, new Vector2(reactor.left, 0f), 600f, 10f);
        }
        isRiotInProgress = true;
//        Timer.schedule(new Timer.Task() {
//            @Override
//            public void run() {
//                //damage
//                reactor.damageStructure(Reactor.DamageAmount.large);
//
//            }
//        }, 10f);
    }

    public void riotFinished() {
        if (isRiotInProgress) {
            reactor.damageStructure(Reactor.DamageAmount.large);
            gameScreen.particles.addLargeSmoke(reactor.left + 300f, 300f);
            isRiotInProgress = false;
        }
        bananasStatus = BananaStatus.END_RIOT;
        for (Banana banana : bananas) {
            banana.setupFinishRiot(true, new Vector2(reactor.left, 0f), new Vector2(reactor.left + 600f, 0f));
        }
    }



}
