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
        if (o instanceof Collision) {
            Collision other = (Collision)o;
            return Float.compare(this.t, other.t);
        }
        return 0;
    }
}
