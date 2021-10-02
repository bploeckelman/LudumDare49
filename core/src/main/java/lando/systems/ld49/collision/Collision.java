package lando.systems.ld49.collision;

import com.badlogic.gdx.math.Vector2;

public class Collision implements Comparable {

    public float t;
    public Vector2 pos = new Vector2();
    public Vector2 normal = new Vector2();

    public Collision (float t, Vector2 pos, Vector2 normal) {
        this.t = t;
        this.pos.set(pos);
        this.normal.set(normal);
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
