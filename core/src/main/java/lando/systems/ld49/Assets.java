package lando.systems.ld49;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Disposable;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Assets implements Disposable {

    public enum Load { ASYNC, SYNC }

    public boolean initialized;

    public SpriteBatch batch;
    public ShapeDrawer shapes;
    public BitmapFont font;
    public GlyphLayout layout;
    public AssetManager mgr;
    public TextureAtlas atlas;

    public Texture pixel;
    public NinePatch debugNinePatch;

    public Particles particles;
    public static class Particles {
        public TextureRegion circle;
        public TextureRegion sparkle;
        public TextureRegion smoke;
        public TextureRegion ring;
    }

    public Assets() {
        this(Load.SYNC);
    }

    public Assets(Load load) {
        initialized = false;

        batch = new SpriteBatch();
        shapes = new ShapeDrawer(batch);
        font = new BitmapFont();
        layout = new GlyphLayout();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        {
            pixmap.setColor(Color.WHITE);
            pixmap.drawPixel(0, 0);
            pixel = new Texture(pixmap);
        }
        pixmap.dispose();

        shapes.setTextureRegion(new TextureRegion(pixel));

        mgr = new AssetManager();
        {
            mgr.load(new AssetDescriptor<>("sprites/sprites.atlas", TextureAtlas.class));
        }

        if (load == Load.SYNC) {
            mgr.finishLoading();
            updateLoading();
        }
    }

    public float updateLoading() {
        if (!mgr.update()) return mgr.getProgress();
        if (initialized) return 1;

        atlas = mgr.get("sprites/sprites.atlas");

        particles = new Particles();
        particles.circle  = atlas.findRegion("particles/circle");
        particles.ring    = atlas.findRegion("particles/ring");
        particles.smoke   = atlas.findRegion("particles/smoke");
        particles.sparkle = atlas.findRegion("particles/sparkle");

        debugNinePatch = new NinePatch(atlas.findRegion("debug-patch"), 2, 2, 2, 2);

        initialized = true;
        return 1;
    }

    @Override
    public void dispose() {
        mgr.dispose();
        batch.dispose();
        pixel.dispose();
    }

}
