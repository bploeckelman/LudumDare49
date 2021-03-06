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

    private final Reactor reactor;
    private final boolean isExterior;


    public Segment2D (Reactor reactor, boolean isExterior, Vector2 start, Vector2 end){
        this(reactor, isExterior, start.x, start.y, end.x, end.y);
    }

    public Segment2D (Reactor reactor, boolean isExterior, float x1, float y1, float x2, float y2){
        this.reactor = reactor;
        this.isExterior = isExterior;
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
        batch.setColor(Color.BLUE);
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
    public void hit(Shot shot) {
        Main.game.audio.playSound(Audio.Sounds.wallHit, 0.7f);
        reactor.damageStructure(isExterior ? Reactor.DamageAmount.medium : Reactor.DamageAmount.small);
    }
}
