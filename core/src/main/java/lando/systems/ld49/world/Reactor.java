package lando.systems.ld49.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld49.Main;

public class Reactor {

    public Array<Segment2D> segments = new Array<>();
    public Array<Pin> pins = new Array<>();
    public float groundLevel;

    public Reactor(float groundLevel) {
        this.groundLevel = groundLevel;
        segments.add(new Segment2D(500, groundLevel, 510, groundLevel + 40));
        segments.add(new Segment2D(510, groundLevel + 40, 510, groundLevel + 140));
        segments.add(new Segment2D(510, groundLevel + 140, 480, groundLevel + 220));
        segments.add(new Segment2D(480, groundLevel + 220, 481, groundLevel + 220));
        segments.add(new Segment2D(481, groundLevel + 220, 511, groundLevel + 140));
        segments.add(new Segment2D(511, groundLevel+140, 511, groundLevel+40));
        segments.add(new Segment2D(511, groundLevel+40, 501, groundLevel));
        segments.add(new Segment2D(501, groundLevel, 800, groundLevel));
        segments.add(new Segment2D(800, groundLevel, 790, groundLevel+40));
        segments.add(new Segment2D(790, groundLevel+40, 790, groundLevel+140));
        segments.add(new Segment2D(790, groundLevel+140, 820, groundLevel+220));

        for (int i = 0; i <10 ; i++){
            float dx = 280 / 11f;
            pins.add(new Pin(510 +dx + dx*i, groundLevel+200, Pin.Type.steel));
            pins.add(new Pin(510 +dx + dx*i, groundLevel+100, Pin.Type.steel));

            if (i < 9){
                pins.add(new Pin(510 +dx + dx/2f + i*dx, groundLevel + 150, Pin.Type.bumper));
            }
        }

    }

    public void update(float dt) {
        for (Pin p : pins) {
            p.update(dt);
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(Main.game.assets.tower, 470, groundLevel, 360, 250);
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
