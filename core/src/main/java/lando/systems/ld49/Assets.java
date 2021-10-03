package lando.systems.ld49;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Assets implements Disposable {

    public enum Load { ASYNC, SYNC }

    public boolean initialized;

    public SpriteBatch batch;
    public ShapeDrawer shapes;
    public BitmapFont font;
    public BitmapFont pixelFont16;
    public GlyphLayout layout;
    public AssetManager mgr;
    public TextureAtlas atlas;

    public Texture pixel;
    public Texture noise;
    public TextureRegion pixelRegion;
    public NinePatch debugNinePatch;

    public Animation<TextureRegion> ripelyIdleAnim;
    public Animation<TextureRegion> ripelyRunAnim;
    public Animation<TextureRegion> presidenteRunAnim;
    public Animation<TextureRegion> cat;
    public Animation<TextureRegion> dog;
    public Animation<TextureRegion> treesIdle;
    public Animation<TextureRegion> treesActive;
    public Animation<TextureRegion> grassA;
    public Animation<TextureRegion> grassB;
    public Animation<TextureRegion> grassC;
    public Animation<TextureRegion> grassD;
    public Animation<TextureRegion> bushA;
    public Animation<TextureRegion> bushB;
    public TextureRegion tower;

    public Array<ShaderProgram> randomTransitions;
    public ShaderProgram blindsShader;
    public ShaderProgram fadeShader;
    public ShaderProgram radialShader;
    public ShaderProgram doomShader;
    public ShaderProgram pizelizeShader;
    public ShaderProgram doorwayShader;
    public ShaderProgram crosshatchShader;
    public ShaderProgram rippleShader;
    public ShaderProgram heartShader;
    public ShaderProgram stereoShader;
    public ShaderProgram circleCropShader;
    public ShaderProgram cubeShader;
    public ShaderProgram dreamyShader;

    public ShaderProgram flameShader;

    public Music exampleMusic;
    public Music music1;

    public Sound exampleSound;
    public Sound slingshotReload;
    public Sound slingshotPull1;
    public Sound slingshotPull2;
    public Sound slingshotPull3;
    public Sound slingshotPull4;
    public Sound slingshotRelease1;
    public Sound slingshotRelease2;
    public Sound slingshotRelease3;
    public Sound slingshotRelease4;
    public Sound rodHit1;
    public Sound rodHit2;
    public Sound rodHit3;
    public Sound rodHit4;
    public Sound rodHit5;
    public Sound rodHit6;
    public Sound rodHit7;
    public Sound wallHit1;
    public Sound wallHit2;
    public Sound wallHit3;
    public Sound wallHit4;
    public Sound wallHit5;
    public Sound wallHit6;
    public Sound wallHit7;
    public Sound wallHit8;
    public Sound wallHit9;
    public Sound wallHit10;
    public Sound pistonUp1;
    public Sound pistonUp2;
    public Sound pistonUp3;
    public Sound pistonUp4;
    public Sound pistonUp5;
    public Sound pistonDown1;
    public Sound pistonDown2;
    public Sound pistonDown3;
    public Sound pistonDown4;
    public Sound pistonDown5;
    public Sound steamHiss1;
    public Sound steamHiss2;
    public Sound steamHiss3;
    public Sound steamHiss4;
    public Sound steamHiss5;
    public Sound steamHiss6;
    public Sound steamHiss7;
    public Sound steamHiss8;
    public Sound steamHiss9;
    public Sound steamHiss10;

    public Particles particles;
    public static class Particles {
        public TextureRegion circle;
        public TextureRegion sparkle;
        public TextureRegion smoke;
        public TextureRegion ring;
    }

    public Backgrounds backgrounds;
    public static class Backgrounds {
        public TextureRegion empty;
        public TextureRegion grass;
        public TextureRegion castles;
        public TextureRegion nuclearPlant;
        public TextureRegion titleImage;
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
            pixelRegion = new TextureRegion(pixel);
        }
        pixmap.dispose();

        shapes.setTextureRegion(new TextureRegion(pixel));

        mgr = new AssetManager();
        {
            mgr.load(new AssetDescriptor<>("sprites/sprites.atlas", TextureAtlas.class));
            mgr.load("textures/noise.png", Texture.class);
            mgr.load(new AssetDescriptor("fonts/chevyray-rise-16.fnt", BitmapFont.class));
            mgr.load("audio/music/example-music.ogg", Music.class);
            mgr.load("audio/music/music1.ogg", Music.class);
            mgr.load("audio/sound/example-sound.wav", Sound.class);
            mgr.load("audio/sound/slingshotPull1.ogg", Sound.class);
            mgr.load("audio/sound/slingshotPull2.ogg", Sound.class);
            mgr.load("audio/sound/slingshotPull3.ogg", Sound.class);
            mgr.load("audio/sound/slingshotPull4.ogg", Sound.class);
            mgr.load("audio/sound/slingshotRelease1.ogg", Sound.class);
            mgr.load("audio/sound/slingshotRelease2.ogg", Sound.class);
            mgr.load("audio/sound/slingshotRelease3.ogg", Sound.class);
            mgr.load("audio/sound/slingshotRelease4.ogg", Sound.class);
            mgr.load("audio/sound/rodHit1.ogg", Sound.class);
            mgr.load("audio/sound/rodHit2.ogg", Sound.class);
            mgr.load("audio/sound/rodHit3.ogg", Sound.class);
            mgr.load("audio/sound/rodHit4.ogg", Sound.class);
            mgr.load("audio/sound/rodHit5.ogg", Sound.class);
            mgr.load("audio/sound/rodHit6.ogg", Sound.class);
            mgr.load("audio/sound/rodHit7.ogg", Sound.class);
            mgr.load("audio/sound/wallHit1.ogg", Sound.class);
            mgr.load("audio/sound/wallHit1.ogg", Sound.class);
            mgr.load("audio/sound/wallHit2.ogg", Sound.class);
            mgr.load("audio/sound/wallHit3.ogg", Sound.class);
            mgr.load("audio/sound/wallHit4.ogg", Sound.class);
            mgr.load("audio/sound/wallHit5.ogg", Sound.class);
            mgr.load("audio/sound/wallHit6.ogg", Sound.class);
            mgr.load("audio/sound/wallHit7.ogg", Sound.class);
            mgr.load("audio/sound/wallHit8.ogg", Sound.class);
            mgr.load("audio/sound/wallHit9.ogg", Sound.class);
            mgr.load("audio/sound/wallHit10.ogg", Sound.class);
            mgr.load("audio/sound/pistonUp1.ogg", Sound.class);
            mgr.load("audio/sound/pistonUp2.ogg", Sound.class);
            mgr.load("audio/sound/pistonUp3.ogg", Sound.class);
            mgr.load("audio/sound/pistonUp4.ogg", Sound.class);
            mgr.load("audio/sound/pistonUp5.ogg", Sound.class);
            mgr.load("audio/sound/pistonDown1.ogg", Sound.class);
            mgr.load("audio/sound/pistonDown2.ogg", Sound.class);
            mgr.load("audio/sound/pistonDown3.ogg", Sound.class);
            mgr.load("audio/sound/pistonDown4.ogg", Sound.class);
            mgr.load("audio/sound/pistonDown5.ogg", Sound.class);
            mgr.load("audio/sound/steamHiss1.ogg", Sound.class);
            mgr.load("audio/sound/steamHiss2.ogg", Sound.class);
            mgr.load("audio/sound/steamHiss3.ogg", Sound.class);
            mgr.load("audio/sound/steamHiss4.ogg", Sound.class);
            mgr.load("audio/sound/steamHiss5.ogg", Sound.class);
            mgr.load("audio/sound/steamHiss6.ogg", Sound.class);
            mgr.load("audio/sound/steamHiss7.ogg", Sound.class);
            mgr.load("audio/sound/steamHiss8.ogg", Sound.class);
            mgr.load("audio/sound/steamHiss9.ogg", Sound.class);
            mgr.load("audio/sound/steamHiss10.ogg", Sound.class);
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
        pixelFont16 = mgr.get("fonts/chevyray-rise-16.fnt");

        noise = mgr.get("textures/noise.png");
        noise.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);


        ripelyIdleAnim = new Animation<>(0.1f, atlas.findRegions("ripely/idle/ripely-idle"));
        ripelyRunAnim  = new Animation<>(0.1f, atlas.findRegions("ripely/run/ripely-run"));
        presidenteRunAnim = new Animation<>(0.1f, atlas.findRegions("presidente/run/presidente-run"));
        ripelyIdleAnim.setPlayMode(Animation.PlayMode.LOOP);
        ripelyRunAnim.setPlayMode(Animation.PlayMode.LOOP);
        presidenteRunAnim.setPlayMode(Animation.PlayMode.LOOP);
        cat = new Animation<>(0.1f, atlas.findRegions("pets/cat"), Animation.PlayMode.LOOP);
        dog = new Animation<>(0.1f, atlas.findRegions("pets/dog"), Animation.PlayMode.LOOP);
        treesIdle = new Animation<>(0.3f, atlas.findRegions("plants/bhc-trees-idle"), Animation.PlayMode.LOOP);
        treesActive = new Animation<>(0.2f, atlas.findRegions("plants/bhc-trees-active"), Animation.PlayMode.LOOP);
        grassA = new Animation<>(0.1f, atlas.findRegions("plants/grasspatch-a"), Animation.PlayMode.LOOP);
        grassB = new Animation<>(0.1f, atlas.findRegions("plants/grasspatch-b"), Animation.PlayMode.LOOP);
        grassC = new Animation<>(0.1f, atlas.findRegions("plants/grasspatch-c"), Animation.PlayMode.LOOP);
        grassD = new Animation<>(0.1f, atlas.findRegions("plants/grasspatch-d"), Animation.PlayMode.LOOP);
        bushA = new Animation<>(0.1f, atlas.findRegions("plants/bush-a"), Animation.PlayMode.LOOP);
        bushB = new Animation<>(0.1f, atlas.findRegions("plants/bush-b"), Animation.PlayMode.LOOP);

        particles = new Particles();
        particles.circle  = atlas.findRegion("particles/circle");
        particles.ring    = atlas.findRegion("particles/ring");
        particles.smoke   = atlas.findRegion("particles/smoke");
        particles.sparkle = atlas.findRegion("particles/sparkle");

        backgrounds = new Backgrounds();
        backgrounds.empty   = atlas.findRegion("backgrounds/empty");
        backgrounds.grass   = atlas.findRegion("backgrounds/grass");
        backgrounds.castles = atlas.findRegion("backgrounds/castles");
        backgrounds.nuclearPlant = atlas.findRegion("backgrounds/nuclear-plant");
        backgrounds.titleImage = atlas.findRegion("backgrounds/title-image");

        tower = atlas.findRegion("tower");

        debugNinePatch = new NinePatch(atlas.findRegion("debug-patch"), 2, 2, 2, 2);


        randomTransitions = new Array<>();
        blindsShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/blinds.frag");
        fadeShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/dissolve.frag");
        radialShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/radial.frag");
        doomShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/doomdrip.frag");
        pizelizeShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/pixelize.frag");
        doorwayShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/doorway.frag");
        crosshatchShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/crosshatch.frag");
        rippleShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/ripple.frag");
        heartShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/heart.frag");
        stereoShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/stereo.frag");
        circleCropShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/circlecrop.frag");
        cubeShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/cube.frag");
        dreamyShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/dreamy.frag");

        flameShader = loadShader("shaders/default.vert", "shaders/flame.frag");


        exampleMusic = mgr.get("audio/music/example-music.ogg", Music.class);
        music1 = mgr.get("audio/music/music1.ogg", Music.class);

        exampleSound = mgr.get("audio/sound/example-sound.wav", Sound.class);
        slingshotPull1 = mgr.get("audio/sound/slingshotPull1.ogg", Sound.class);
        slingshotPull2 = mgr.get("audio/sound/slingshotPull2.ogg", Sound.class);
        slingshotPull3 = mgr.get("audio/sound/slingshotPull3.ogg", Sound.class);
        slingshotPull4 = mgr.get("audio/sound/slingshotPull4.ogg", Sound.class);
        slingshotRelease1 = mgr.get("audio/sound/slingshotRelease1.ogg", Sound.class);
        slingshotRelease2 = mgr.get("audio/sound/slingshotRelease2.ogg", Sound.class);
        slingshotRelease3 = mgr.get("audio/sound/slingshotRelease3.ogg", Sound.class);
        slingshotRelease4 = mgr.get("audio/sound/slingshotRelease4.ogg", Sound.class);
        rodHit1 = mgr.get("audio/sound/rodHit1.ogg", Sound.class);
        rodHit2 = mgr.get("audio/sound/rodHit2.ogg", Sound.class);
        rodHit3 = mgr.get("audio/sound/rodHit3.ogg", Sound.class);
        rodHit4 = mgr.get("audio/sound/rodHit4.ogg", Sound.class);
        rodHit5 = mgr.get("audio/sound/rodHit5.ogg", Sound.class);
        rodHit6 = mgr.get("audio/sound/rodHit6.ogg", Sound.class);
        rodHit7 = mgr.get("audio/sound/rodHit7.ogg", Sound.class);
        wallHit1 = mgr.get("audio/sound/wallHit1.ogg", Sound.class);
        wallHit2 = mgr.get("audio/sound/wallHit2.ogg", Sound.class);
        wallHit3 = mgr.get("audio/sound/wallHit3.ogg", Sound.class);
        wallHit4 = mgr.get("audio/sound/wallHit4.ogg", Sound.class);
        wallHit5 = mgr.get("audio/sound/wallHit5.ogg", Sound.class);
        wallHit6 = mgr.get("audio/sound/wallHit6.ogg", Sound.class);
        wallHit7 = mgr.get("audio/sound/wallHit7.ogg", Sound.class);
        wallHit8 = mgr.get("audio/sound/wallHit8.ogg", Sound.class);
        wallHit9 = mgr.get("audio/sound/wallHit9.ogg", Sound.class);
        wallHit10 = mgr.get("audio/sound/wallHit10.ogg", Sound.class);
        pistonUp1 = mgr.get("audio/sound/pistonUp1.ogg", Sound.class);
        pistonUp2 = mgr.get("audio/sound/pistonUp2.ogg", Sound.class);
        pistonUp3 = mgr.get("audio/sound/pistonUp3.ogg", Sound.class);
        pistonUp4 = mgr.get("audio/sound/pistonUp4.ogg", Sound.class);
        pistonUp5 = mgr.get("audio/sound/pistonUp5.ogg", Sound.class);
        pistonDown1 = mgr.get("audio/sound/pistonDown1.ogg", Sound.class);
        pistonDown2 = mgr.get("audio/sound/pistonDown2.ogg", Sound.class);
        pistonDown3 = mgr.get("audio/sound/pistonDown3.ogg", Sound.class);
        pistonDown4 = mgr.get("audio/sound/pistonDown4.ogg", Sound.class);
        pistonDown5 = mgr.get("audio/sound/pistonDown5.ogg", Sound.class);
        steamHiss1 = mgr.get("audio/sound/steamHiss1.ogg", Sound.class);
        steamHiss2 = mgr.get("audio/sound/steamHiss2.ogg", Sound.class);
        steamHiss3 = mgr.get("audio/sound/steamHiss3.ogg", Sound.class);
        steamHiss4 = mgr.get("audio/sound/steamHiss4.ogg", Sound.class);
        steamHiss5 = mgr.get("audio/sound/steamHiss5.ogg", Sound.class);
        steamHiss6 = mgr.get("audio/sound/steamHiss6.ogg", Sound.class);
        steamHiss7 = mgr.get("audio/sound/steamHiss7.ogg", Sound.class);
        steamHiss8 = mgr.get("audio/sound/steamHiss8.ogg", Sound.class);
        steamHiss9 = mgr.get("audio/sound/steamHiss9.ogg", Sound.class);
        steamHiss10 = mgr.get("audio/sound/steamHiss1.ogg", Sound.class);

        randomTransitions.add(radialShader);
        randomTransitions.add(pizelizeShader);

        initialized = true;
        return 1;
    }

    private static ShaderProgram loadShader(String vertSourcePath, String fragSourcePath) {
        ShaderProgram.pedantic = false;
        ShaderProgram shaderProgram = new ShaderProgram(
                Gdx.files.internal(vertSourcePath),
                Gdx.files.internal(fragSourcePath));

        if (!shaderProgram.isCompiled()) {
             Gdx.app.error("LoadShader", "compilation failed:\n" + shaderProgram.getLog());
            throw new GdxRuntimeException("LoadShader: compilation failed:\n" + shaderProgram.getLog());
        } else if (Config.shader_debug){
             Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
             Gdx.app.debug("LoadShader", "ShaderProgram compilation log: " + shaderProgram.getLog());
        }

        return shaderProgram;
    }

    @Override
    public void dispose() {
        mgr.dispose();
        batch.dispose();
        pixel.dispose();
    }

}
