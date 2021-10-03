package lando.systems.ld49.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld49.Assets;
import lando.systems.ld49.collision.CollisionManager;
import lando.systems.ld49.screens.GameScreen;

public class World {

    private final GameScreen gameScreen;
    private final Assets assets;
    private final Vector2 center;

    public final Rectangle bounds;
    public static float cameraMargin = 30;

    private float animIdleState = 0f;
    private float animRunState = 0f;
    private Catapult catapult;
    public Array<Shot> shots = new Array<>();
    public Reactor reactor;
    private float groundLevel;
    private CollisionManager collisionManager;
    private Array<Banana> bananas = new Array<>();
    private float animState1 = 0;
    private float animState2 = 0;
    private float animState3 = 0;

    public World(GameScreen screen) {
        this.gameScreen = screen;
        this.assets = screen.assets;
        this.center = new Vector2();
        this.bounds = new Rectangle(0, 0, 1024, 1024);
        this.bounds.getCenter(center);
        catapult = new Catapult(assets, 250, 80);
        reactor = new Reactor();
        collisionManager = new CollisionManager(this);
        // Build the collidable areas
        bananas.add(new Banana(assets, 340f, 0, this));
        bananas.add(new Banana(assets, 520f, 0, this));
    }

    public Vector2 getCenter() {
        return bounds.getCenter(center);
    }

    public void update(float dt) {
        animIdleState += dt;
        animRunState += dt;
        animState1 += dt;
        animState2 += dt;
        animState3 += dt;
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
        batch.draw(assets.backgrounds.castles, bounds.x, bounds.y - 200, bounds.width + 300, bounds.height);

        // dirt
        float groundLevel = 350;
        float bottom = bounds.y - 200;
        batch.setColor(198 / 255f, 156 / 255f, 108 / 255f, 1);
        batch.draw(assets.pixel, bounds.x, bottom, bounds.width + 300, groundLevel);
        batch.setColor(Color.WHITE);

        // background nuclear plant
        float scale = 2f;
        float width  = scale * assets.backgrounds.nuclearPlant.getRegionWidth();
        float height = scale * assets.backgrounds.nuclearPlant.getRegionHeight();
        batch.draw(assets.backgrounds.nuclearPlant,
                bounds.x + bounds.width  / 2f - width  / 2f,
                bounds.y + bounds.height / 2f - height,
                width, height);

        // plants
        float horizon = bottom + groundLevel;
        batch.draw(assets.treesActive.getKeyFrame(animState1), bounds.x + 60, horizon);
        batch.draw(assets.treesActive.getKeyFrame(animState2), bounds.x + 400, horizon);
        batch.draw(assets.treesIdle.getKeyFrame(animState3), 120, 60);
        batch.draw(assets.grassA.getKeyFrame(animState1), bounds.x + 20, horizon);
        batch.draw(assets.grassB.getKeyFrame(animState2), bounds.x + 450, horizon);
        batch.draw(assets.grassC.getKeyFrame(animState2), 100, 60);
        batch.draw(assets.grassD.getKeyFrame(animState3), 400, 60);
        batch.draw(assets.bushA.getKeyFrame(animState1), 150, 60);
        batch.draw(assets.bushB.getKeyFrame(animState2), 350, horizon);

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


}
