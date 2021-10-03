package lando.systems.ld49.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld49.Assets;
import lando.systems.ld49.Main;

public class Reactor {

    private static final boolean DRAW_SEGMENTS = false;

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
        segments.add(new Segment2D(left +   8 * xScale, flipy - 360 * yScale, left +   8 * xScale, flipy - 330 * yScale));
        segments.add(new Segment2D(left +   8 * xScale, flipy - 330 * yScale, left +  60 * xScale, flipy - 280 * yScale));
        segments.add(new Segment2D(left +  60 * xScale, flipy - 280 * yScale, left + 105 * xScale, flipy - 220 * yScale));
        segments.add(new Segment2D(left + 105 * xScale, flipy - 220 * yScale, left + 125 * xScale, flipy - 160 * yScale));
        segments.add(new Segment2D(left + 125 * xScale, flipy - 160 * yScale, left + 125 * xScale, flipy - 100 * yScale));
        segments.add(new Segment2D(left + 125 * xScale, flipy - 100 * yScale, left + 115 * xScale, flipy -  70 * yScale));
        segments.add(new Segment2D(left + 115 * xScale, flipy -  70 * yScale, left + 105 * xScale, flipy -  40 * yScale));
        segments.add(new Segment2D(left + 105 * xScale, flipy -  40 * yScale, left + 130 * xScale, flipy -  30 * yScale));
        segments.add(new Segment2D(left + 130 * xScale, flipy -  30 * yScale, left + 130 * xScale, flipy -  40 * yScale));

        // interior wall, left side
        segments.add(new Segment2D(left + 130 * xScale, flipy -  30 * yScale, left + 125 * xScale, flipy -  65 * yScale));
        segments.add(new Segment2D(left + 125 * xScale, flipy -  65 * yScale, left + 135 * xScale, flipy - 100 * yScale));
        segments.add(new Segment2D(left + 135 * xScale, flipy - 100 * yScale, left + 155 * xScale, flipy - 130 * yScale));
        segments.add(new Segment2D(left + 155 * xScale, flipy - 130 * yScale, left + 170 * xScale, flipy - 160 * yScale));
        segments.add(new Segment2D(left + 170 * xScale, flipy - 160 * yScale, left + 165 * xScale, flipy - 200 * yScale));
        segments.add(new Segment2D(left + 165 * xScale, flipy - 200 * yScale, left + 150 * xScale, flipy - 235 * yScale));
        segments.add(new Segment2D(left + 150 * xScale, flipy - 235 * yScale, left + 135 * xScale, flipy - 270 * yScale));
        segments.add(new Segment2D(left + 135 * xScale, flipy - 270 * yScale, left + 125 * xScale, flipy - 300 * yScale));
        segments.add(new Segment2D(left + 125 * xScale, flipy - 300 * yScale, left + 120 * xScale, flipy - 330 * yScale));
        segments.add(new Segment2D(left + 120 * xScale, flipy - 330 * yScale, left + 140 * xScale, flipy - 335 * yScale));
        segments.add(new Segment2D(left + 140 * xScale, flipy - 335 * yScale, left + 165 * xScale, flipy - 360 * yScale));
        segments.add(new Segment2D(left + 165 * xScale, flipy - 360 * yScale, left + 165 * xScale, flipy - 360 * yScale));

        // floor
        segments.add(new Segment2D(left + 165 * xScale, flipy - 360 * yScale, left + 305 * xScale, flipy - 360 * yScale));

        // interior wall, right side
        segments.add(new Segment2D(left + 305 * xScale, flipy - 360 * yScale, left + 340 * xScale, flipy - 335 * yScale));
        segments.add(new Segment2D(left + 340 * xScale, flipy - 335 * yScale, left + 360 * xScale, flipy - 325 * yScale));
        segments.add(new Segment2D(left + 360 * xScale, flipy - 325 * yScale, left + 360 * xScale, flipy - 300 * yScale));
        segments.add(new Segment2D(left + 360 * xScale, flipy - 300 * yScale, left + 340 * xScale, flipy - 265 * yScale));
        segments.add(new Segment2D(left + 340 * xScale, flipy - 265 * yScale, left + 315 * xScale, flipy - 200 * yScale));
        segments.add(new Segment2D(left + 315 * xScale, flipy - 200 * yScale, left + 315 * xScale, flipy - 155 * yScale));
        segments.add(new Segment2D(left + 315 * xScale, flipy - 155 * yScale, left + 335 * xScale, flipy - 115 * yScale));
        segments.add(new Segment2D(left + 335 * xScale, flipy - 115 * yScale, left + 350 * xScale, flipy -  80 * yScale));
        segments.add(new Segment2D(left + 350 * xScale, flipy -  80 * yScale, left + 365 * xScale, flipy -  50 * yScale));
        segments.add(new Segment2D(left + 365 * xScale, flipy -  50 * yScale, left + 360 * xScale, flipy -  30 * yScale));
        segments.add(new Segment2D(left + 360 * xScale, flipy -  30 * yScale, left + 360 * xScale, flipy -  30 * yScale));

        // exterior wall, right side
        segments.add(new Segment2D(left + 360 * xScale, flipy -  30 * yScale, left + 370 * xScale, flipy -  45 * yScale));
        segments.add(new Segment2D(left + 370 * xScale, flipy -  45 * yScale, left + 360 * xScale, flipy -  75 * yScale));
        segments.add(new Segment2D(left + 360 * xScale, flipy -  75 * yScale, left + 360 * xScale, flipy - 100 * yScale));
        segments.add(new Segment2D(left + 360 * xScale, flipy - 100 * yScale, left + 360 * xScale, flipy - 160 * yScale));
        segments.add(new Segment2D(left + 360 * xScale, flipy - 160 * yScale, left + 370 * xScale, flipy - 200 * yScale));
        segments.add(new Segment2D(left + 370 * xScale, flipy - 200 * yScale, left + 395 * xScale, flipy - 250 * yScale));
        segments.add(new Segment2D(left + 395 * xScale, flipy - 250 * yScale, left + 440 * xScale, flipy - 300 * yScale));
        segments.add(new Segment2D(left + 440 * xScale, flipy - 300 * yScale, left + 470 * xScale, flipy - 330 * yScale));
        segments.add(new Segment2D(left + 470 * xScale, flipy - 330 * yScale, left + 470 * xScale, flipy - 360 * yScale));
        segments.add(new Segment2D(left + 470 * xScale, flipy - 360 * yScale, left + 500 * xScale, flipy - 360 * yScale));

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
        if (!DRAW_SEGMENTS) return;
        for (Segment2D segment : segments) {
            segment.debugRender(batch);
        }
    }
}
