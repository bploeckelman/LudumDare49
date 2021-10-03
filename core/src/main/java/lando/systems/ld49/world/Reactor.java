package lando.systems.ld49.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld49.Assets;
import lando.systems.ld49.Main;

public class Reactor {

    public Array<Segment2D> segments = new Array<>();
    public Array<Pin> pins = new Array<>();

    private final TextureRegion shellTexture;
    private final TextureRegion backTexture;
    private final TextureRegion poolTexture;
    private final TextureRegion glowTexture;
    // TODO: pistons and sockets
    private final float left = 650;
    private final float scale = 1.25f;

    public Reactor() {
        Assets assets = Main.game.assets;;
        shellTexture = assets.atlas.findRegion("tower/frontwall/tower-frontwall");
        backTexture = assets.atlas.findRegion("tower/backwall/tower-backwall");
        poolTexture = assets.atlas.findRegion("tower/pool-glow/tower-pool");
        glowTexture = assets.atlas.findRegion("tower/pool-glow/tower-pool-glow");

        float offset = 300;
        segments.add(new Segment2D(offset + 500, 0,   offset + 510, 40));
        segments.add(new Segment2D(offset + 510, 40,  offset + 510, 140));
        segments.add(new Segment2D(offset + 510, 140, offset + 480, 220));
        segments.add(new Segment2D(offset + 480, 220, offset + 481, 220));
        segments.add(new Segment2D(offset + 481, 220, offset + 511, 140));
        segments.add(new Segment2D(offset + 511, 140, offset + 511, 40));
        segments.add(new Segment2D(offset + 511, 40,  offset + 501, 0));
        segments.add(new Segment2D(offset + 501, 0,   offset + 800, 0));
        segments.add(new Segment2D(offset + 800, 0,   offset + 790, 40));
        segments.add(new Segment2D(offset + 790, 40,  offset + 790, 140));
        segments.add(new Segment2D(offset + 790, 140, offset + 820, 220));

        for (int i = 0; i <10 ; i++){
            float dx = 280 / 11f;
            pins.add(new Pin(offset + 510 +dx + dx*i, 200, Pin.Type.steel));
            pins.add(new Pin(offset + 510 +dx + dx*i, 100, Pin.Type.steel));

            if (i < 9){
                pins.add(new Pin(offset + 510 +dx + dx/2f + i*dx, 150, Pin.Type.bumper));
            }
        }
    }

    public void update(float dt) {
        for (Pin p : pins) {
            p.update(dt);
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion tex = backTexture;
        batch.draw(tex, left, 0, scale * tex.getRegionWidth(), scale * tex.getRegionHeight());
        tex = poolTexture;
        batch.draw(tex, left, 0, scale * tex.getRegionWidth(), scale * tex.getRegionHeight());
        tex = glowTexture;
        batch.draw(tex, left, 0, scale * tex.getRegionWidth(), scale * tex.getRegionHeight());
        tex = shellTexture;
        batch.draw(tex, left, 0, scale * tex.getRegionWidth(), scale * tex.getRegionHeight());

        for (Pin p : pins) {
            p.render(batch);
        }
    }

    public void renderDebug(SpriteBatch batch) {
        for (Segment2D segment : segments) {
            segment.debugRender(batch);
        }
    }
}
