package lando.systems.ld49.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld49.Assets;
import lando.systems.ld49.Config;

public class Cloud {
    Vector2 pos;
    boolean headingRight;
    float speed;
    public boolean offScreen;
    TextureRegion texture;

    public Cloud(boolean onScreen){
        texture = Assets.cloudTextures.get(MathUtils.random(Assets.cloudTextures.size-1));
        headingRight = MathUtils.randomBoolean();
        speed = MathUtils.random(20f,50f);
        offScreen = false;
        if (onScreen){
            pos = new Vector2(MathUtils.random(Config.window_width), MathUtils.random(150) + 400);
        } else {
            pos = new Vector2(headingRight? -texture.getRegionWidth() : Config.window_width, MathUtils.random(150) + 400);
        }
    }

    public void update(float dt){
        if (headingRight){
            pos.x += speed * dt;
        } else {
            pos.x -= speed * dt;
        }

        if (pos.x < -texture.getRegionWidth() || pos.x > Config.window_width) offScreen = true;
    }

    public void render(SpriteBatch batch){
        batch.draw(texture, pos.x, pos.y);
    }
}
