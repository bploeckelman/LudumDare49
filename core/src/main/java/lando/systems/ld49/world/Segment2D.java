package lando.systems.ld49.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld49.Audio;
import lando.systems.ld49.Main;
import lando.systems.ld49.collision.Collidable;

public class Segment2D implements Collidable {
    public Vector2 start;
    public Vector2 end;
    public Vector2 delta;
    public Vector2 normal;


    public Segment2D (Vector2 start, Vector2 end){
        this(start.x, start.y, end.x, end.y);
    }

    public Segment2D (float x1, float y1, float x2, float y2){
        this.start = new Vector2(x1, y1);
        this.end = new Vector2(x2, y2);
        this.delta = new Vector2(end).sub(start);
        this.normal = new Vector2(end).sub(start).nor().rotate90(1);
    }

    public float getRotation(){
        return delta.angle();
    }

    public void debugRender(SpriteBatch batch) {
        float width = 1f;
        batch.setColor(Color.BLACK);
        batch.draw(Main.game.assets.pixelRegion, start.x, start.y - width/2f, 0, width/2f, delta.len(), width, 1, 1, getRotation());
        batch.setColor(Color.MAGENTA);
        batch.draw(Main.game.assets.pixelRegion, (start.x + end.x)/2f, (start.y  + end.y )/2f, 0, width/2f, 10, .5f, 1, 1, normal.angleDeg());
        batch.setColor(Color.WHITE);

    }

    @Override
    public float getElastisity() {
        return .8f;
    }

    @Override
    public void hit() {
        // Todo: play a sound or something
        Main.game.audio.playSound(Audio.Sounds.rodHit, 0.07f);
    }
}
