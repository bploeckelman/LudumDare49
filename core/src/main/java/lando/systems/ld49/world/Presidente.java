package lando.systems.ld49.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.compression.lzma.Base;
import lando.systems.ld49.Assets;
import lando.systems.ld49.screens.BaseScreen;

public class Presidente {
    private float animationTimer;
    public Animation<TextureRegion> animation;
    public Vector2 pos = new Vector2();
    private TextureRegion textureRegion;
    public float scale = 1f;
    public boolean isDroppingCoins = false;
    private BaseScreen screen;
    private float emoteTimer = 0f;
    private float emoteCooldown = 0f;
    private float width = 48;
    private float height = 48;
    public boolean isEmoting = false;
    public boolean enableEmote = false;
    private TextureRegion emoteTexture;

    public Presidente(Assets assets, float x, float y, BaseScreen screen) {
        this.animation = assets.presidenteIdleAnim;
        this.emoteTexture = assets.emotes.cash;
        this.animationTimer = 0f;
        this.screen = screen;
        //this.textureRegion = animation.getKeyFrame(0f);
        pos.set(x, y);
    }

    public void emote(float dt) {
        emoteCooldown -= dt;
        if (emoteCooldown < 0) {
            isEmoting = true;
            emoteCooldown = MathUtils.random(4f, 7f);
            emoteTimer = MathUtils.random (1f, 3f);
        }
        if (isEmoting && emoteTimer > 0) {
            emoteTimer -= dt;
        }
        if (emoteTimer < 0) {
            isEmoting = false;
        }
    }


    public void update(float dt) {
        animationTimer += dt;
        textureRegion = animation.getKeyFrame(animationTimer);
        if (isDroppingCoins) {
            screen.particles.sparkle(pos.x, pos.y);
        }
        emote(dt);
    }

    public void render(SpriteBatch batch) {
        batch.draw(textureRegion, pos.x, pos.y, textureRegion.getRegionWidth() * scale, textureRegion.getRegionHeight() * scale);
        if (isEmoting) {
            batch.setColor(1f, 1f, 1f, 0.8f);
            batch.draw(emoteTexture, pos.x + width * scale / 2 - emoteTexture.getRegionWidth() / 2, pos.y + height * scale + 5f);
        }
    }
}
