package lando.systems.ld49.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld49.Main;

public class Reactor {

    public Array<Segment2D> segments = new Array<>();
    public Array<Pin> pins = new Array<>();

    public Reactor() {
        segments.add(new Segment2D(500, 0, 510, 40));
        segments.add(new Segment2D(510, 40, 510, 140));
        segments.add(new Segment2D(510, 140, 480, 220));
        segments.add(new Segment2D(480, 220, 481, 220));
        segments.add(new Segment2D(481, 220, 511, 140));
        segments.add(new Segment2D(511, 140, 511, 40));
        segments.add(new Segment2D(511, 40, 501, 0));
        segments.add(new Segment2D(501, 0, 800, 0));
        segments.add(new Segment2D(800, 0, 790, 40));
        segments.add(new Segment2D(790, 40, 790, 140));
        segments.add(new Segment2D(790, 140, 820, 220));

        for (int i = 0; i <10 ; i++){
            float dx = 280 / 11f;
            pins.add(new Pin(510 +dx + dx*i, 200, Pin.Type.steel));
            pins.add(new Pin(510 +dx + dx*i, 100, Pin.Type.steel));

            if (i < 9){
                pins.add(new Pin(510 +dx + dx/2f + i*dx, 150, Pin.Type.bumper));
            }
        }

    }

    public void update(float dt) {
        for (Pin p : pins) {
            p.update(dt);
        }
    }

    public void render(SpriteBatch batch) {
        float scale = 1.5f;
        TextureRegion tower = Main.game.assets.tower;
        batch.draw(tower, 700, 0, scale * tower.getRegionWidth(), scale * tower.getRegionHeight());
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
