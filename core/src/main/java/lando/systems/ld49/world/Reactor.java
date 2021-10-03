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

    private final float left = 350;
    private final float xScale = 1.5f;
    private final float yScale = 1.5f;

    public Reactor() {
        Assets assets = Main.game.assets;;
        shellTexture = assets.atlas.findRegion("tower/frontwall/tower-frontwall");
        backTexture = assets.atlas.findRegion("tower/backwall/tower-backwall");
        poolTexture = assets.atlas.findRegion("tower/pool-glow/tower-pool");
        glowTexture = assets.atlas.findRegion("tower/pool-glow/tower-pool-glow");

        // height of image, since pixels are top left origin the coords we got from the image are y-flipped, need to re-orient to y-up
        float flipy = 360 * yScale;

        // exterior wall, left side
        segments.add(new Segment2D(left +   5 * xScale, flipy - 360 * yScale, left +   5 * xScale, flipy - 330 * yScale));
        segments.add(new Segment2D(left +   5 * xScale, flipy - 330 * yScale, left +  55 * xScale, flipy - 280 * yScale));
        segments.add(new Segment2D(left +  55 * xScale, flipy - 280 * yScale, left + 100 * xScale, flipy - 220 * yScale));
        segments.add(new Segment2D(left + 100 * xScale, flipy - 220 * yScale, left + 120 * xScale, flipy - 160 * yScale));
        segments.add(new Segment2D(left + 120 * xScale, flipy - 160 * yScale, left + 120 * xScale, flipy - 100 * yScale));
        segments.add(new Segment2D(left + 120 * xScale, flipy - 100 * yScale, left + 115 * xScale, flipy -  70 * yScale));
        segments.add(new Segment2D(left + 115 * xScale, flipy -  70 * yScale, left + 105 * xScale, flipy -  40 * yScale));
        segments.add(new Segment2D(left + 105 * xScale, flipy -  40 * yScale, left + 130 * xScale, flipy -  30 * yScale));
        segments.add(new Segment2D(left + 130 * xScale, flipy -  30 * yScale, left + 130 * xScale, flipy -  40 * yScale));

//        for (int i = 0; i <10 ; i++){
//            float dx = 280 / 11f;
//            pins.add(new Pin(offset + 510 +dx + dx*i, 200, Pin.Type.steel));
//            pins.add(new Pin(offset + 510 +dx + dx*i, 100, Pin.Type.steel));
//
//            if (i < 9){
//                pins.add(new Pin(offset + 510 +dx + dx/2f + i*dx, 150, Pin.Type.bumper));
//            }
//        }
    }

    public void update(float dt) {
        for (Pin p : pins) {
            p.update(dt);
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion tex = backTexture;
        batch.draw(tex, left, 0, xScale * tex.getRegionWidth(), yScale * tex.getRegionHeight());
        tex = poolTexture;
        batch.draw(tex, left, 0, xScale * tex.getRegionWidth(), yScale * tex.getRegionHeight());
        tex = glowTexture;
        batch.draw(tex, left, 0, xScale * tex.getRegionWidth(), yScale * tex.getRegionHeight());
        tex = shellTexture;
        batch.draw(tex, left, 0, xScale * tex.getRegionWidth(), yScale * tex.getRegionHeight());

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
