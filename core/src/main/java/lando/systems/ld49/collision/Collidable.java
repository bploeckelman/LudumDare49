package lando.systems.ld49.collision;

import lando.systems.ld49.world.Shot;

public interface Collidable {
    float getElastisity();
    void hit(Shot shot);
}
