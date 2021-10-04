package lando.systems.ld49.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import lando.systems.ld49.Assets;
import lando.systems.ld49.Audio;
import lando.systems.ld49.collision.CollisionManager;
import lando.systems.ld49.screens.GameScreen;

public class World {

    public final GameScreen gameScreen;
    public final Assets assets;
    public final Rectangle bounds;

    private float animIdleState = 0f;
    private float animRunState = 0f;
    private Catapult catapult;
    public Array<Shot> shots = new Array<>();
    public Reactor reactor;
    private CollisionManager collisionManager;
    private Array<Banana> bananas = new Array<>();
    private float animState1 = 0;
    private float animState2 = MathUtils.random(1, 2);
    private float animState3 = MathUtils.random(3, 10);
    private float ambianceSoundTime;

    private final float bananaHammockLeft = 280;
    private final float bananaHammockBottom = 120;
    private final float bananaPopulation = 10;

    public World(GameScreen screen) {
        this.gameScreen = screen;
        this.assets = screen.assets;
        this.bounds = new Rectangle(0, 0, 1024, 1024);
        catapult = new Catapult(this, bananaHammockLeft, bananaHammockBottom);
        reactor = new Reactor(this);
        collisionManager = new CollisionManager(this);
        for (int i = 0; i < bananaPopulation; i++) {
            bananas.add(new Banana(assets, MathUtils.random(30f, 450f), MathUtils.random(30f, 150f), this));
        }
        ambianceSoundTime = MathUtils.random(5f, 10f);
    }

    public void update(float dt, boolean pause) {
        ambianceSoundTime-= dt;
        if (ambianceSoundTime <= 0){
            // TODO: Pete play sound here
            gameScreen.game.audio.playSound(Audio.Sounds.steamHiss, 0.1f);
            ambianceSoundTime = MathUtils.random(4f, 10f);
        }
        animIdleState += dt;
        animRunState += dt;
        animState1 += dt;
        animState2 += dt;
        animState3 += dt;

        // Things that shouldn't run when paused should be here
        if (pause) return;

        reactor.update(dt);
        catapult.update(dt, gameScreen);
        collisionManager.solve(dt);

        for (int i = shots.size-1; i >=0; i--){
            Shot shot = shots.get(i);
            shot.update(dt);
            // TODO: remove if done
            if (shot.remove) {
                shots.removeIndex(i);
            }
        }
        for (Banana banana : bananas) {
            banana.update(dt);
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(assets.backgrounds.empty, bounds.x, bounds.y - 200, bounds.width + 300, bounds.height);

        // dirt
        float groundLevel = 350;
        float bottom = bounds.y - 200;
        batch.setColor(198 / 255f, 156 / 255f, 108 / 255f, 1);
        batch.draw(assets.pixelRegion, bounds.x, bottom, bounds.width + 300, groundLevel);
        batch.setColor(Color.WHITE);

        // background nuclear plant
        float scale = 2f;
        float width  = scale * assets.backgrounds.nuclearPlant.getRegionWidth();
        float height = scale * assets.backgrounds.nuclearPlant.getRegionHeight();
        batch.draw(assets.backgrounds.nuclearPlant,
                bounds.x + bounds.width  / 2f - width  / 2f,
                bounds.y + bounds.height / 2f - height,
                width, height);

        // background trees
        float horizon = bottom + groundLevel;
        batch.draw(assets.treesIdle.getKeyFrame(animState1), bounds.x + 20, horizon);
        batch.draw(assets.treesIdle.getKeyFrame(animState2), bounds.x + bounds.width - 100, horizon);

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
        batch.draw(assets.bananaHammockSign, bananaHammockLeft - 150, bananaHammockBottom);

        // foreground stuff
        reactor.render(batch);
        catapult.render(batch);
        for (Shot shot: shots) {
            shot.render(batch);
        }
        for (Banana banana : bananas) {
            banana.render(batch);
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
    }

    public void makeBananasRiot() {
        for (Banana banana : bananas) {
            banana.startRiot(true, new Vector2(reactor.left, 0f), 600f, 10f);
        }
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                //damage
                reactor.damageStructure(Reactor.DamageAmount.large);

            }
        }, 10f);
    }


}
