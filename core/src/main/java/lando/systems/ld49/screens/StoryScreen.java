package lando.systems.ld49.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld49.Config;
import lando.systems.ld49.Main;

public class StoryScreen extends BaseScreen {
    private static float textScale = 1.0f;
    float accum = 0;
    PerspectiveCamera perspectiveCamera;
    GlyphLayout layout;

    String text = "\n\n\n\n\nThis is the story, all about how\n\n" +
            "my life got flipped turned upside down.\n\n" +
            "And I'd like to take a minute so just sit right there.\n\n" +
            "I'll tell you all about how I became the prince of a town called Bel-Air" +
            "\n\n\n\n" +
            "In West Philadelphia born and raised\n\n" +
            "on the playground is where I spent most of my days\n\n" +
            "chillin out, maxin', ralaxin' all cool\n\n" +
            "and be shooting some b-ball outside of the school\n\n" +
            "When a couple of guys who were up to no good\n\n" +
            "started making trouble in my neighborhood" +
            "\n\n\n\n" +
            "I got in one little fight and my mom got scared\n\n" +
            "and said you're movin' with your auntie and uncle in Bel-Air" +
            "\n\n\n\nClick to continue";

    public StoryScreen(Main game) {
        super(game);
        layout = new GlyphLayout();
        game.assets.pixelFont16.getData().setScale(textScale);
        layout.setText(game.assets.pixelFont16, text, Color.WHITE, worldCamera.viewportWidth, Align.center, true);

        game.assets.pixelFont16.getData().setScale(1f);

        perspectiveCamera = new PerspectiveCamera(90, 1280, 800);
        perspectiveCamera.far=10000;
        perspectiveCamera.position.set(640, 0, 500);
        perspectiveCamera.lookAt(640, 400, 0);
        perspectiveCamera.update();

    }

    public void update(float dt) {
        accum += 75*dt;
//        accum = MathUtils.clamp(accum, 0, layout.height);
        if (accum > layout.height && Gdx.input.justTouched()) {
            game.setScreen(new GameScreen(game), assets.cubeShader, 3f);
        } else if (Gdx.input.justTouched()) {
            accum = layout.height;
        }
        if (accum >= layout.height * 2f) {
            game.setScreen(new GameScreen(game), assets.cubeShader, 3f);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

        batch.setProjectionMatrix(perspectiveCamera.combined);
        batch.begin();
        game.assets.pixelFont16.getData().setScale(textScale);
        game.assets.pixelFont16.setColor(.3f, .3f, .3f, 1.0f);
        game.assets.pixelFont16.draw(batch, text, 5, accum-5, worldCamera.viewportWidth, Align.center, true);
        game.assets.pixelFont16.setColor(Color.YELLOW);
        game.assets.pixelFont16.draw(batch, text, 0, accum, worldCamera.viewportWidth, Align.center, true);
        game.assets.pixelFont16.getData().setScale(1.0f);
//        batch.draw(textTexture, 0, 0, 1024, layout.height);
        batch.end();

    }
}
