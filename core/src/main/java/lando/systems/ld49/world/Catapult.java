package lando.systems.ld49.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld49.Assets;
import lando.systems.ld49.Audio;
import lando.systems.ld49.screens.GameScreen;
import lando.systems.ld49.utils.Utils;

public class Catapult {
    private Assets assets;
    private World world;

    private Vector2 pos = new Vector2();
    private Rectangle bounds = new Rectangle();
    private boolean held;
    private Vector2 launchAngle = new Vector2();
    private float strength;
    private float strengthMultiplier = 15;
    private float accum;

    private TextureRegion bananaHammock;
    private Animation<TextureRegion> bananaHammockShineAnim;
    private float shineStateTime = 0;
    private float width = 108;
    private float height = 36;
    private float shineCountdownMax;
    private float shineCountdown = 0;

    public Catapult(World world, float x, float y) {
        this.world = world;
        this.assets = world.assets;
        this.pos.set(x, y);
        this.bounds.set(x - width/2f, y - height/2f, width, height);
        this.accum = 0;
        this.bananaHammock = assets.atlas.findRegion("catapult/bhc-thong");
        this.bananaHammockShineAnim = new Animation<>(0.1f, assets.atlas.findRegions("catapult/bhc-thong-shine"), Animation.PlayMode.NORMAL);
        accum = 0;
    }

    public void update(float dt, GameScreen screen) {
        Vector3 mousePos = screen.mousePos;
        accum += dt;
        shineStateTime += dt;
        shineCountdown -= dt;
        if (shineCountdown <= 0) {
            shineCountdownMax = MathUtils.random(1, 4);
            shineCountdown = shineCountdownMax;
            shineStateTime = 0;
        }

        //TODO: If no ore, exit here

        if (!held){
            if (Gdx.input.justTouched() && bounds.contains(mousePos.x, mousePos.y)){
                held = true;
                screen.game.audio.playSound(Audio.Sounds.slingshotPull);

            }
        } else {
            if (!Gdx.input.isTouched()) {
                held = false;
                // TODO: Launch something
                screen.game.audio.playSound(Audio.Sounds.slingshotRelease, 0.2f);
                screen.world.addShot(new Shot(pos, new Vector2(launchAngle.x * strength * strengthMultiplier, launchAngle.y * strength * strengthMultiplier)));
            } else {
                launchAngle.set(pos.x - mousePos.x, pos.y - mousePos.y).nor();
                strength = MathUtils.clamp(pos.dst(mousePos.x, mousePos.y), 0, 60f);
            }
        }
    }

    public void render(SpriteBatch batch) {
        batch.setColor(held ? Color.LIGHT_GRAY : Color.WHITE);
        batch.draw(bananaHammock, pos.x - width/2, pos.y - height/2, width, height);
        batch.setColor(Color.WHITE);

        if (shineCountdown > 0 && !held) {
            TextureRegion shineFrame = bananaHammockShineAnim.getKeyFrame(shineStateTime);
            batch.draw(shineFrame, pos.x - width / 2, pos.y - height / 2, width, height);
        }

        if (held){
            batch.setColor(Color.YELLOW);
            batch.draw(bananaHammock, pos.x - launchAngle.x * strength - 10, pos.y - launchAngle.y * strength - 10, 20, 20);
            drawPath(batch);
        }

        batch.setColor(Color.WHITE);
    }

    Vector2 tempVec2 = new Vector2();
    Vector2 temp2Vec2 = new Vector2();
    Vector2 tempVel = new Vector2();
    Color pathColor = new Color();
    private void drawPath(SpriteBatch batch) {
        float hatch = .02f;
        for (int i = 0; i < 1000; i+=3) {
            tempVec2 = pathFunction(i * hatch, tempVec2);
            temp2Vec2 = pathFunction((i+1)* hatch, temp2Vec2);
            batch.setColor(Utils.hsvToRgb(accum - i *hatch, 1f, 1f, pathColor));

            batch.draw(assets.pixelRegion, tempVec2.x, tempVec2.y - 2, 0, 2, tempVec2.dst(temp2Vec2), 4, 1, 1, tempVel.set(temp2Vec2).sub(tempVec2).angleDeg());

            // TODO Break out when it is within the reactor
            for (Segment2D segment : world.reactor.segments) {

            }
        }
        batch.setColor(Color.WHITE);
    }

    private Vector2 pathFunction(float t, Vector2 point) {
        tempVel.set(launchAngle.x * strength * strengthMultiplier, launchAngle.y * strength * strengthMultiplier);
        point.set(pos.x + tempVel.x * t, pos.y + tempVel.y *t + -250*t*t);
        return point;
    }
}
