package lando.systems.ld49.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld49.Audio;
import lando.systems.ld49.Config;
import lando.systems.ld49.Main;

public class StoryScreen extends BaseScreen {
    private static float textScale = 1.0f;
    float accum = 0;
    PerspectiveCamera perspectiveCamera;
    GlyphLayout layout;

    String text = "\n\n\n\n\nThis is the story, all about how\n\n" +
            "my life got flip turned upside down\n\n" +
            "And I'd like to take a minute so just sit right there\n\n" +
            "I'll tell you all about how corporate\n\n"+
            "imperialism destroyed a country called POTASSIA\n\n"+
            "\n\n" +
            "--------\n\n" +
            "\n\n" +
            "For the plantain kingdom of Potassia,\n\n" +
            "life was peaceful and prosperous.\n\n" +
            "\n\n" +
            "The people were happy and healthy,\n\n" +
            "and there was always enough to go around. \n\n"+
            "\n\n" +
            "Life wasn`t perfect,\n\n" +
            "but the people were in charge of their own lives.\n\n" +
            "\n\n" +
            "...Until Cavendish came.\n\n" +
            "\n\n" +
            "Cavendish Industrial Agronomics\n\n" +
            "took one look at the lush forests and abundant fruit\n\n" +
            "of Potassia and immediately craved\n\n" +
            "its every last resource.\n\n" +
            "\n\n" +
            "Fueled by greed and indifference,\n\n" +
            "Cavendish stole the land from its people.\n\n" +
            "\n\n" +
            "________\n\n" +
            "\n\n" +
            "The company paid the locals a pittance,\n\n" +
            "Forcing them to work grueling hours\n\n" +
            "in miserable conditions. \n\n" +
            "\n\n" +
            "The Cavendish CEO became de facto dictator.\n\n" +
            "He forces the people to call him \"Dole Presidente,\"\n\n" +
            "and unless he pays kickbacks to\n\n" +
            "the -OTHER- CIA, they threaten to destroy\n\n" +
            "the island and everything on it." +
            "\n\n\n\n" +
            "Even worse, the shoddy Cavendish nuclear plants\n\n" +
            "are always on the verge of catastrophic failure.\n\n" +
            "\n\n" +
            "The Potassians must slingshot whatever they can find\n\n" +
            "into the cooling towers to keep them from overheating.\n\n" +
            "\n\n" +
            "It`s only matter of time until a meltdown\n\n" +
            "fueled by greed and consumption \n\n" +
            "destroys Potassia and its people…\n\n" +
            "\n\n________\n\n" +
            
            "\n\nAre you a bad enough plantain to save Potassia?" +

            "\n\n\nClick to play!";

    public StoryScreen(Main game) {
        super(game);
        layout = new GlyphLayout();
        game.assets.pixelFont16.getData().setScale(textScale);
        layout.setText(game.assets.pixelFont16, text, Color.WHITE, worldCamera.viewportWidth, Align.center, true);

        game.assets.pixelFont16.getData().setScale(1f);
        game.audio.fadeMusic(Audio.Musics.storyMusic);

        perspectiveCamera = new PerspectiveCamera(90, 1280, 800);
        perspectiveCamera.far=10000;
        perspectiveCamera.position.set(640, 0, 500);
        perspectiveCamera.lookAt(640, 400, 0);
        perspectiveCamera.update();

    }

    public void update(float dt) {
        float speedMultiplier = 1.0f;

        if (Gdx.input.isTouched()){
            speedMultiplier = 10f;
        }
        accum += 75*dt * speedMultiplier;
//        accum = MathUtils.clamp(accum, 0, layout.height);
        if (accum > layout.height && Gdx.input.justTouched()) {
            launchGame();
        }
        if (accum >= layout.height) {
            launchGame();
        }
    }

    private void launchGame() {
        if (!exitingScreen){
            exitingScreen = true;
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
