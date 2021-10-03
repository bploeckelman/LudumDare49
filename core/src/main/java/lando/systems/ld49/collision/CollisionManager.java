package lando.systems.ld49.collision;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld49.world.Segment2D;
import lando.systems.ld49.world.Shot;
import lando.systems.ld49.world.World;

public class CollisionManager {
    World world;

    public CollisionManager(World world) {
        this.world = world;
    }

    Vector2 tempStart1 = new Vector2();
    Vector2 tempEnd1 = new Vector2();
    Vector2 frameEndPos = new Vector2();
    Vector2 tempStart2 = new Vector2();
    Vector2 tempEnd2 = new Vector2();
    Vector2 frameVel1 = new Vector2();
    Vector2 frameVel2 = new Vector2();
    Vector2 nearest1 = new Vector2();
    Vector2 nearest2 = new Vector2();
    Vector2 normal = new Vector2();
    Vector2 incomingVector = new Vector2();
    Array<Collision> collisions = new Array<>();
    public void solve(float dt) {

        for (Shot s : world.shots){
            s.dtLeft = dt;
        }

        boolean collisionHappened = true;
        int fuckInfiniteLoops = 0;

        collisionLoop:
        while (collisionHappened) {
            fuckInfiniteLoops++;
            collisionHappened = false;
            for (Shot s : world.shots){
                if (s.dtLeft <= 0) continue;
                collisions.clear();
                boolean collided = false;

                frameVel1.set(s.velocity.x * s.dtLeft, s.velocity.y * s.dtLeft);
                tempStart1.set(s.pos);
                tempEnd1.set(s.pos.x + frameVel1.x, s.pos.y + frameVel1.y);
                frameEndPos.set(tempEnd1);

                // check boundary
                for (Segment2D segment : world.reactor.segments){
                    float t = checkSegmentCollision(tempStart1, tempEnd1, segment.start, segment.end, nearest1, nearest2);
                    if (t != Float.MAX_VALUE && nearest1.dst(nearest2) < s.radius) {
                        normal.set(tempStart1).sub(nearest2).nor().scl(s.radius + 1.5f);
                        collisions.add(new Collision(t, nearest2.add(normal), segment.normal));
                    }
                }

                if (collisions.size > 0) {
                    collisionHappened = true;
                    collisions.sort();
                    Collision c = collisions.get(0);
                    s.velocity.set(reflectVector(incomingVector.set(s.velocity), c.normal));
                    s.velocity.scl(.8f);
                    s.dtLeft -= c.t*dt;
                    s.pos.set(c.pos);

                } else {
                    s.dtLeft = 0;
                    s.pos.set(frameEndPos);
                }
            }
            if (fuckInfiniteLoops > 10000) {
                Gdx.app.log("Collisions", "Collision had an infinite loop. =(");
                break;
            }
        }
    }


    Vector2 d1 = new Vector2();
    Vector2 d2 = new Vector2();
    Vector2 r = new Vector2();
    private float checkSegmentCollision(Vector2 seg1Start, Vector2 seg1End, Vector2 seg2Start, Vector2 seg2End, Vector2 nearestSeg1, Vector2 nearestSeg2){
        d1.set(seg1End).sub(seg1Start);
        d2.set(seg2End).sub(seg2Start);
        r.set(seg1Start).sub(seg2Start);

        float a = d1.dot(d1);
        float e = d2.dot(d2);
        float f = d2.dot(r);

        float b = d1.dot(d2);
        float c = d1.dot(r);

        float s = 0;
        float t = 0;

        float denom = a*e-b*b;
        if (denom != 0){
            s = MathUtils.clamp((b*f - c*e)/denom, 0f, 1f);
        } else {
            // Parallel
            return Float.MAX_VALUE;
        }

        t = (b*s + f) /e;
        if (t < 0) {
            t = 0;
            s = MathUtils.clamp(-c /a, 0, 1);
        } else if (t > 1) {
            t = 1;
            s = MathUtils.clamp((b-c)/a, 0, 1);
        }

        nearestSeg1.set(seg1Start).add(d1.scl(s));
        nearestSeg2.set(seg2Start).add(d2.scl(t));
        return s;
    }

    public static Vector2 reflectVector(Vector2 incoming, Vector2 normal) {
        float initalSize = incoming.len();
        normal.nor();
        incoming.nor();
        float iDotN = incoming.dot(normal);
        incoming.set(incoming.x - 2f * normal.x * iDotN,
                incoming.y - 2f * normal.y * iDotN)
                .nor().scl(initalSize);
        return incoming;
    }
}
