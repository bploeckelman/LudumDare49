package lando.systems.ld49.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld49.Assets;
import lando.systems.ld49.Config;

public class Banana {
    private Assets assets;

    private Vector2 pos = new Vector2();
    private float width;
    private float height;
    private float scale;
    private Status status;
    private Animation<TextureRegion> animation;
    private float animationTimer = 0f;
    private float actionTimer;
    private float actionDuration;
    private final float VELOCITY = 1f;
    private final float YVELOCITY = .5f;
    private final float RIOT_VELOCITY = 2f;
    private World world;
    private TextureRegion textureRegion;
    private boolean isRioting = false;
    private Vector2 riotDestination = new Vector2();
    private float riotTimer = 0f;
    private float happyTimer = 0f;
    private float emoteTimer = 0f;
    private float emoteCooldown = 0f;
    private boolean isEmoting = false;
    private TextureRegion emoteTexture;
    private Feelings feeling;
    private int yDirection = 1;

    public enum Status { IDLE_LEFT, IDLE_RIGHT, WALK_LEFT, WALK_RIGHT, RIOT_LEFT, RIOT_RIGHT, RIOT_IDLE }
    public enum Feelings { POSITIVE, NEUTRAL, NEGATIVE, WARN_TEMP, WARN_WRENCH}

    public Banana(Assets assets, float x, float y, World world) {
        this.assets = assets;
        this.animation = assets.ripelyIdleAnim;
        this.status = Status.IDLE_RIGHT;
        this.world = world;
        this.actionTimer = 0f;
        this.actionDuration = MathUtils.random(3f, 10f);
        this.textureRegion = animation.getKeyFrame(0f);
        this.feeling = Feelings.NEUTRAL;
        this.width = 48f;
        this.height = 48f;
        this.scale = 1f;
        pos.set(x, y);
    }
    public void scalePerY() {
        scale = 2 - pos.y/100f;
    }

    public void emote(float dt) {
        if (isEmoting && emoteTimer > 0) {
            emoteTimer -= dt;
        }
        if (emoteTimer < 0) {
            isEmoting = false;
        }
    }

    public void setEmoteTexture(float dt) {
        emoteCooldown -= dt;
        if (emoteCooldown < 0) {
            isEmoting = true;
            emoteCooldown = MathUtils.random(5f, 10f);
            emoteTimer = MathUtils.random (2f, 4f);
            switch (feeling) {
                case NEUTRAL:
                    int random = MathUtils.random(assets.neutralEmotes.size - 1);
                    emoteTexture = assets.neutralEmotes.get(random);
                    break;
                case NEGATIVE:
                    emoteTexture = assets.negativeEmotes.get(MathUtils.random(assets.neutralEmotes.size - 1));
                    break;
                case POSITIVE:
                    emoteTexture = assets.positiveEmotes.get(MathUtils.random(assets.neutralEmotes.size - 1));
                    break;
                case WARN_TEMP:
                    emoteTexture = assets.emotes.temperature;
                    break;
                case WARN_WRENCH:
                    emoteTexture = assets.emotes.wrench;
                    break;
            }
        }
    }

    public void startRiot(boolean isRioting, Vector2 reactorLocation, float reactorWidth, float riotTimer) {
        this.isRioting = isRioting;
        this.riotDestination.set(reactorLocation.x + MathUtils.random(reactorWidth), reactorLocation.y);
        this.riotTimer = riotTimer;
        this.feeling = Feelings.NEGATIVE;
        this.emoteCooldown = 0f;
    }

    public void beHappy(float happyTimer) {
        this.feeling = Feelings.POSITIVE;
        this.happyTimer = happyTimer;
        this.riotTimer = 0;
        this.emoteCooldown = 0f;
    }


    private void wanderYAxis() {
        if (pos.x > world.reactor.left) {
            return;
        }
        if (pos.y > 150f) {
            yDirection = -1;
            pos.y -= MathUtils.random(YVELOCITY);
        } else if (pos.y < 50f) {
            yDirection = 1;
            pos.y += MathUtils.random(YVELOCITY);
        } else {
            pos.y += yDirection * MathUtils.random(YVELOCITY);
        }
    }

    public void riot(float dt) {
        animationTimer += dt;
        riotTimer -= dt;
        if (pos.x < riotDestination.x + 40f && pos.x > riotDestination.x - 40f) {
//            status = Status.RIOT_IDLE;
            world.gameScreen.particles.addSmoke(pos.x + width * scale / 2, pos.y + height * scale + 5f);

        }
        if (pos.x + width * scale / 2 < riotDestination.x) {
            status = Status.RIOT_RIGHT;
        }
        else if (pos.x > riotDestination.x) {
            status = Status.RIOT_LEFT;
        }

        switch (status) {
            case RIOT_LEFT:
                pos.x -= RIOT_VELOCITY;
                if (pos.y > 0) {
                    pos.y -= RIOT_VELOCITY;
                }
                textureRegion = assets.ripelyRunAnim.getKeyFrame(animationTimer);
                break;
            case RIOT_RIGHT:
                pos.x += RIOT_VELOCITY;
                if (pos.y > 0) {
                    pos.y -= RIOT_VELOCITY;
                }
                textureRegion = assets.ripelyRunAnim.getKeyFrame(animationTimer);
                break;
            case RIOT_IDLE:
                textureRegion = assets.ripelyIdleAnim.getKeyFrame(animationTimer);
                break;
        }


        if (riotTimer < 0) {
            isRioting = false;
            feeling = Feelings.NEUTRAL;
            status = Status.WALK_LEFT;
            emoteCooldown = 0f;
            world.riotFinished();
        }
    }

    public void wanderAround(float dt) {
        actionTimer+=dt;
        animationTimer+=dt;
        if (feeling == Feelings.POSITIVE) {
            happyTimer-=dt;
        }
        if (happyTimer < 0) {
            feeling = Feelings.NEUTRAL;
        }
        //set new status if action timer has exceeded its duration
        if (actionTimer > actionDuration) {
            actionTimer = 0f;
            actionDuration = MathUtils.random(3f, 10f);
            switch (MathUtils.random(1)) {
                case 0:
                    yDirection = 1;
                    break;
                case 1:
                    yDirection = -1;
                    break;
            }
            if (pos.x > Config.viewport_width - width) {
                status = Status.WALK_LEFT;
            }
            else if (status == Status.IDLE_LEFT || status == Status.IDLE_RIGHT) {
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
        } else if (pos.x > Config.viewport_width - width && status == Status.WALK_RIGHT) {
            //walk to left
            status = Status.WALK_LEFT;
        }
        switch (status) {
            case WALK_LEFT:
                pos.x -= VELOCITY;
                textureRegion = assets.ripelyRunAnim.getKeyFrame(animationTimer);
                wanderYAxis();
                break;
            case WALK_RIGHT:
                pos.x += VELOCITY;
                textureRegion = assets.ripelyRunAnim.getKeyFrame(animationTimer);
                wanderYAxis();
                break;
            case IDLE_LEFT:
            case IDLE_RIGHT:
                textureRegion = assets.ripelyIdleAnim.getKeyFrame(animationTimer);
                break;
        }

    }

    public void update(float dt) {
        scalePerY();
        if (isRioting) {
            riot(dt);
            world.gameScreen.particles.addSmoke(pos.x + width * scale / 2, pos.y);
        }
        else {
            wanderAround(dt);
        }
        setEmoteTexture(dt);
        emote(dt);
    }

    public void render(SpriteBatch batch) {
        switch (status) {
            case RIOT_IDLE:
            case RIOT_LEFT:
            case RIOT_RIGHT:
                batch.setColor(new Color(1f, .5f, .5f, 1f));
                break;
            default:
                batch.setColor(Color.WHITE);
                break;
        }
        switch (status) {
            case WALK_LEFT:
            case IDLE_LEFT:
            case RIOT_LEFT:
                batch.draw(textureRegion, pos.x + width * scale, pos.y, -1 * width * scale, height * scale);
                break;
            case WALK_RIGHT:
            case IDLE_RIGHT:
            case RIOT_RIGHT:
            case RIOT_IDLE:
                batch.draw(textureRegion, pos.x, pos.y, width * scale, height * scale);
                break;
        }
        if (isEmoting) {
            batch.setColor(1f, 1f, 1f, 0.8f);
            batch.draw(emoteTexture, pos.x + width * scale / 2 - emoteTexture.getRegionWidth() / 2, pos.y + height * scale + 5f);
        }
        batch.setColor(Color.WHITE);
    }
}
