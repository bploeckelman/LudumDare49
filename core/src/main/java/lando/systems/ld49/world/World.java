package lando.systems.ld49.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld49.Assets;
import lando.systems.ld49.Audio;
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

    public World(GameScreen screen) {
        this.gameScreen = screen;
        this.assets = screen.assets;
        this.center = new Vector2();
        this.bounds = new Rectangle(0, 0, 1024, 1024);
        this.bounds.getCenter(center);
        catapult = new Catapult(assets, 330, bounds.height / 2f - 99);
        screen.game.audio.stopMusic();

        groundLevel = bounds.height / 2f - 99;
        reactor = new Reactor(groundLevel);
        collisionManager = new CollisionManager(this);
        // Build the collidable areas


    }

    public Vector2 getCenter() {
        return bounds.getCenter(center);
    }

    public void update(float dt) {
        animIdleState += dt;
        animRunState += dt;
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
    }

    public void draw(SpriteBatch batch) {
        batch.draw(assets.backgrounds.castles, bounds.x, bounds.y, bounds.width, bounds.height);

        float groundLevel = bounds.height / 2f - 99;
        batch.setColor(198 / 255f, 156 / 255f, 108 / 255f, 1);
        batch.draw(assets.pixel, bounds.x, bounds.y, bounds.width, groundLevel);
        batch.setColor(Color.WHITE);

        float scale = 1.5f;
        float width  = scale * assets.backgrounds.nuclearPlant.getRegionWidth();
        float height = scale * assets.backgrounds.nuclearPlant.getRegionHeight();
        batch.draw(assets.backgrounds.nuclearPlant,
                bounds.x + bounds.width  / 2f - width  / 2f,
                bounds.y + bounds.height / 2f - height / 2f,
                width, height);

        reactor.render(batch);


        TextureRegion idleKeyframe = assets.ripelyIdleAnim.getKeyFrame(animIdleState);
        TextureRegion runKeyframe = assets.ripelyRunAnim.getKeyFrame(animRunState);
        batch.draw(idleKeyframe, 200, groundLevel);
        batch.draw(runKeyframe, 300, groundLevel);
        catapult.render(batch);
        for (Shot shot: shots) {
            shot.render(batch);
        }

        reactor.renderDebug(batch);
    }

    public void addShot(Shot shot) {
        shots.add(shot);
    }


}
