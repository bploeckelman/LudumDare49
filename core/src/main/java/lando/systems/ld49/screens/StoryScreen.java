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
    float accum = 0;
    OrthographicCamera textCamera;
    GlyphLayout layout;
    FrameBuffer textFb;
    Texture textTexture;
    String text = "\n\n\nThis is the story, all about how\n\n" +
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
            "\n\n\n\n";

    public StoryScreen(Main game) {
        super(game);
        layout = new GlyphLayout();

        layout.setText(game.assets.pixelFont16, text, Color.WHITE, worldCamera.viewportWidth, Align.center, true);
        textFb = new FrameBuffer(Pixmap.Format.RGBA8888, (int)worldCamera.viewportWidth, (int)layout.height, true);
//        worldCamera.setToOrtho(false, Config.viewport_width, layout.height);
//        worldCamera.update();
        textTexture = textFb.getColorBufferTexture();
        textCamera = new OrthographicCamera();
        textCamera.setToOrtho(false, worldCamera.viewportWidth, layout.height);
        textCamera.update();
    }

    public void update(float dt) {
        accum += 30*dt;
        accum = MathUtils.clamp(accum, 0, layout.height);
        if (accum == layout.height && Gdx.input.justTouched()) {
            game.setScreen(new GameScreen(game), assets.cubeShader, 3f);
        }
    }

    @Override
    public void renderFrameBuffers(SpriteBatch batch) {
        textFb.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

        batch.setProjectionMatrix(textCamera.combined);
        batch.begin();
//        batch.setColor(Color.RED);
//        batch.draw(game.assets.pixelRegion, 1, 1, worldCamera.viewportWidth- 2, worldCamera.viewportHeight - 2);
        game.assets.pixelFont16.draw(batch, text, 0, accum, worldCamera.viewportWidth, Align.center, true);
        batch.end();

        textFb.end();
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        batch.setShader(game.assets.starWarsShader);
        batch.draw(textTexture, 0, worldCamera.viewportHeight*.66f, worldCamera.viewportWidth, -worldCamera.viewportHeight*.64f);
        batch.setShader(null);
        batch.end();
    }
}
