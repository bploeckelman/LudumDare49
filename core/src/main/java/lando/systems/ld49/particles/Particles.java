package lando.systems.ld49.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import lando.systems.ld49.Assets;

public class Particles implements Disposable {

    public enum Layer { background, middle, foreground }

    private static final int MAX_PARTICLES = 4000;

    private final Assets assets;
    private final ObjectMap<Layer, Array<Particle>> activeParticles;
    private final Pool<Particle> particlePool = Pools.get(Particle.class, MAX_PARTICLES);

    public Particles(Assets assets) {
        this.assets = assets;
        this.activeParticles = new ObjectMap<>();
        int particlesPerLayer = MAX_PARTICLES / Layer.values().length;
        this.activeParticles.put(Layer.background, new Array<>(false, particlesPerLayer));
        this.activeParticles.put(Layer.middle,     new Array<>(false, particlesPerLayer));
        this.activeParticles.put(Layer.foreground, new Array<>(false, particlesPerLayer));
    }

    public void clear() {
        for (Layer layer : Layer.values()) {
            particlePool.freeAll(activeParticles.get(layer));
            activeParticles.get(layer).clear();
        }
    }

    public void update(float dt) {
        for (Layer layer : Layer.values()) {
            for (int i = activeParticles.get(layer).size - 1; i >= 0; --i) {
                Particle particle = activeParticles.get(layer).get(i);
                particle.update(dt);
                if (particle.isDead()) {
                    activeParticles.get(layer).removeIndex(i);
                    particlePool.free(particle);
                }
            }
        }
    }

    public void draw(SpriteBatch batch, Layer layer) {
        activeParticles.get(layer).forEach(particle -> particle.render(batch));
    }

    @Override
    public void dispose() {
        clear();
    }

    // ------------------------------------------------------------------------
    // Helper fields for particle spawner methods
    // ------------------------------------------------------------------------
    private final Color tempColor = new Color();
    private final Vector2 tempVec2 = new Vector2();
    // ------------------------------------------------------------------------
    // Spawners for different particle effects
    // ------------------------------------------------------------------------

    public void sparkle(float x, float y) {
        TextureRegion keyframe = assets.particles.sparkle;
        tempColor.set(Color.WHITE);
        int numParticles = 3;
        for (int i = 0; i < numParticles; ++i) {
            activeParticles.get(Layer.foreground).add(Particle.initializer(particlePool.obtain())
                    .keyframe(keyframe)
                    .startPos(x, y)
                    .velocityDirection(MathUtils.random(-5, 5) + 90, MathUtils.random(-200, -150))
                    .startSize(MathUtils.random(10, 16))
                    .endSize(MathUtils.random(2, 8))
                    .startAlpha(1f)
                    .endAlpha(0f)
                    .timeToLive(2f)
                    .startColor(tempColor)
                    .init());
        }
    }

}
