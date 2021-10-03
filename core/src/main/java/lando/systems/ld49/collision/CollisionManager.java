package lando.systems.ld49.collision;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld49.world.*;

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
                        collisions.add(new Collision(t, nearest2.add(normal), segment.normal, segment));
                    }
                }

                // check pins
                for (Pin p : world.reactor.pins) {
                    tempStart1.set(s.pos.x, s.pos.y);
                    frameVel1.set(s.velocity.x * s.dtLeft, s.velocity.y * s.dtLeft);
                    tempEnd1.set(s.pos.x + frameVel1.x, s.pos.y + frameVel1.y);
                    tempStart2.set(p.position.x, p.position.y);
                    frameVel2.set(0,0);

                    Float time = intersectCircleCircle(tempStart1, tempStart2, frameVel1, frameVel2, s.radius, p.radius);
                    if (time != null){
                        if (time == 0.0f) {
                            // ball was inside pin
                            Gdx.app.log("collision", "ball was already inside a pin");
                            float overlapDist = tempStart1.dst(tempStart2) - (s.radius + p.radius);
                            overlapDist += 1.5f;
                            normal.set(tempStart2).sub(tempStart1).nor();
                            tempEnd1.set(tempStart1.x + (overlapDist) * normal.x, tempStart1.y + (overlapDist) * normal.y);
                            s.pos.x = tempEnd1.x;
                            s.pos.y = tempEnd1.y;
                            continue collisionLoop;
                        } else if (time < 1) {
                            frameEndPos.set(tempStart1.x + frameVel1.x * (time * .99f), tempStart1.y + frameVel1.y * (time * .99f));
                            normal.set(frameEndPos).sub(tempStart2).nor();
                            collisions.add(new Collision(time, frameEndPos, normal, p));
                        }
                    }
                }
                tempStart1.set(s.pos.x, s.pos.y);
                frameVel1.set(s.velocity.x * s.dtLeft, s.velocity.y * s.dtLeft);
                tempEnd1.set(s.pos.x + frameVel1.x, s.pos.y + frameVel1.y);
                for (Piston piston : world.reactor.pistons) {
                    if (piston.bounds.contains(tempEnd1.x, tempEnd1.y)) {
                        collisions.add(new Collision(-1f, tempEnd1, Vector2.Y, piston));
                    }
                }

                if (collisions.size > 0) {
                    collisionHappened = true;
                    collisions.sort();
                    Collision c = collisions.get(0);
                    s.velocity.set(reflectVector(incomingVector.set(s.velocity), c.normal));
                    s.velocity.scl(c.collidable.getElastisity());
                    s.dtLeft -= c.t*dt;
                    s.pos.set(c.pos);
                    c.collidable.hit();
                    if (c.collidable instanceof Piston) {
                       s.remove = true;
                       s.dtLeft = 0;
                    }

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

    static Vector2 s = new Vector2();
    static Vector2 v = new Vector2();
    public static Float intersectCircleCircle(Vector2 pos1, Vector2 pos2, Vector2 vel1, Vector2 vel2, float rad1, float rad2) {
        return intersectCircleCircle(pos1.x, pos1.y, pos2.x, pos2.y, vel1.x, vel1.y, vel2.x, vel2.y, rad1, rad2);
    }

    public static Float intersectCircleCircle(float pos1x, float pos1y, float pos2x, float pos2y,
                                              float vel1x, float vel1y, float vel2x, float vel2y,
                                              float rad1, float rad2) {
        float t;
        s.set(pos2x, pos2y).sub(pos1x, pos1y);
        v.set(vel2x, vel2y).sub(vel1x, vel1y);
        float r = rad1 + rad2;
        float c = s.dot(s) - r * r;
        if (c < 0){
            // Already overlap early out
            t = 0f;
            return t;
        }
        float a = v.dot(v);
        if (a < .1f) return null; // circles not moving relative to each other
        float b = v.dot(s);
        if (b >= 0f) return null; // circles moving away from each other
        float d = b * b - a * c;
        if (d < 0) return null; // No intersections
        t = (float)(-b - Math.sqrt(d)) / a;

        return t;
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

    public static float intersectParabolaSegment(Segment2D segment, Vector2 point1, Vector2 point2, Vector2 point3) {
        if (segment.start.x == segment.end.x) {
            // Line is vertical check where that line would intersect
        } else {
            // convert the parameterized parabola to rectangular

            float m = (segment.end.y - segment.start.y) / (segment.end.x - segment.start.x);
            float d = segment.start.y - (m * segment.start.x);

        }
        return 0;
    }
}
