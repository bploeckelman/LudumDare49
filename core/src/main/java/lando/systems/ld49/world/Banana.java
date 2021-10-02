package lando.systems.ld49.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld49.Assets;

public class Banana {
    private Assets assets;

    private Vector2 pos = new Vector2();
    private Status status;
    private Animation<TextureRegion> animation;
    private float animationTimer = 0f;
    private float actionTimer;
    private float actionDuration;
    private final float VELOCITY = 1f;
    private World world;
    private TextureRegion textureRegion;

    public enum Status { IDLE_LEFT, IDLE_RIGHT, WALK_LEFT, WALK_RIGHT }

    public Banana(Assets assets, float x, float y, World world) {
        this.assets = assets;
        this.animation = assets.ripelyIdleAnim;
        this.status = Status.IDLE_RIGHT;
        this.world = world;
        this.actionTimer = 0f;
        this.actionDuration = MathUtils.random(3f, 10f);
        this.textureRegion = animation.getKeyFrame(0f);
        pos.set(x, y);
    }

    public void wanderAround(float dt) {
        actionTimer+=dt;
        animationTimer+=dt;
        //set new status if action timer has exceeded its duration
        if (actionTimer > actionDuration) {
            actionTimer = 0f;
            actionDuration = MathUtils.random(3f, 10f);
            if (status == Status.IDLE_LEFT || status == Status.IDLE_RIGHT) {
                switch (MathUtils.random(1)) {
                    case 0:
                        status = Status.WALK_RIGHT;
                        break;
                    case 1:
                        status = Status.WALK_LEFT;
                        break;
                }
            }
            else if (status == Status.WALK_LEFT) {
                status = Status.IDLE_LEFT;
            }
            else if (status == Status.WALK_RIGHT) {
                status = Status.IDLE_RIGHT;
            }
        }
        if (0 > pos.x && status == Status.WALK_LEFT) {
            //force walk to right
            status = Status.WALK_RIGHT;
        } else if (pos.x > world.bounds.width - textureRegion.getRegionWidth() && status == Status.WALK_RIGHT) {
            //walk to left
            status = Status.WALK_LEFT;
        }
        switch (status) {
            case WALK_LEFT:
                pos.x -= VELOCITY;
                textureRegion = assets.ripelyRunAnim.getKeyFrame(animationTimer);
                break;
            case WALK_RIGHT:
                pos.x += VELOCITY;
                textureRegion = assets.ripelyRunAnim.getKeyFrame(animationTimer);
                break;
            case IDLE_LEFT:
            case IDLE_RIGHT:
                textureRegion = assets.ripelyIdleAnim.getKeyFrame(animationTimer);
                break;
        }

    }

    public void update(float dt) {
        wanderAround(dt);

    }

    public void render(SpriteBatch batch) {
        switch (status) {
            case WALK_LEFT:
            case IDLE_LEFT:
                batch.draw(textureRegion, pos.x + textureRegion.getRegionWidth(), pos.y, -1 * textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
                break;
            case WALK_RIGHT:
            case IDLE_RIGHT:
                batch.draw(textureRegion, pos.x, pos.y);
                break;
        }
        batch.setColor(Color.WHITE);
    }
}
