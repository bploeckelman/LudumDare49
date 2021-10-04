package lando.systems.ld49.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    public Presidente(Assets assets, float x, float y, BaseScreen screen) {
        this.animation = assets.presidenteRunAnim;
        this.animationTimer = 0f;
        this.screen = screen;
        //this.textureRegion = animation.getKeyFrame(0f);
        pos.set(x, y);
    }


    public void update(float dt) {
        animationTimer += dt;
        textureRegion = animation.getKeyFrame(animationTimer);
        if (isDroppingCoins) {
            screen.particles.sparkle(pos.x, pos.y);
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(textureRegion, pos.x, pos.y, textureRegion.getRegionWidth() * scale, textureRegion.getRegionHeight() * scale);
    }
}
