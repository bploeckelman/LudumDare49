package lando.systems.ld49.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld49.Config;
import lando.systems.ld49.Main;
import lando.systems.ld49.utils.typinglabel.TypingLabel;

public class EndScreen extends BaseScreen {

    private TypingLabel titleLabel;
    private TypingLabel themeLabel;
    private TypingLabel leftCreditLabel;
    private TypingLabel rightCreditLabel;
    private TypingLabel thanksLabel;
    private TypingLabel disclaimerLabel;

    private Animation<TextureRegion> catAnimation;
    private Animation<TextureRegion> dogAnimation;
    private TextureRegion background;

    static String title = "{GRADIENT=purple;cyan}Banana Republic: Nuclear Power Plantain{ENDGRADIENT}";
    static String theme = "Made for Ludum Dare 49: Unstable";

    static String thanks = "{GRADIENT=purple;cyan}Thanks for playing our game!{ENDGRADIENT}";
    static String developers = "{COLOR=gray}Developed by:{COLOR=white}\n Brian Ploeckelman \n Doug Graham \n Jeffrey Hwang";
    static String artists = "{COLOR=gray}Art by:{COLOR=white}\n Matt Neumann";
    static String emotionalSupport = "{COLOR=cyan}Emotional Support:{COLOR=white}\n  Asuka and     Cherry";
    static String music = "{COLOR=gray}Sound by:{COLOR=white}\n Pete Valeo";
    static String libgdx = "Made with {COLOR=red}<3{COLOR=white} and LibGDX";
    static String disclaimer = "Disclaimer!!!\nNo bananas were harmed in the making of this game";

    private float accum = 0f;

    public EndScreen(Main game) {
        super(game);

        titleLabel = new TypingLabel(game.assets.pixelFont16, title.toLowerCase(), 0f, Config.window_height / 2f + 290f);
        titleLabel.setWidth(Config.window_width);
        titleLabel.setFontScale(1f);

        themeLabel = new TypingLabel(game.assets.pixelFont16, theme.toLowerCase(), 0f, Config.window_height / 2f + 220f);
        themeLabel.setWidth(Config.window_width);
        themeLabel.setFontScale(1f);

        leftCreditLabel = new TypingLabel(game.assets.pixelFont16, developers.toLowerCase() + "\n\n\n\n" + emotionalSupport.toLowerCase() + "\n\n", 75f, Config.window_height / 2f + 135f);
        leftCreditLabel.setWidth(Config.window_width / 2f - 150f);
        leftCreditLabel.setLineAlign(Align.left);
        leftCreditLabel.setFontScale(1f);
        catAnimation = game.assets.cat;
        dogAnimation = game.assets.dog;
        background = game.assets.atlas.findRegion("lando");

        rightCreditLabel = new TypingLabel(game.assets.pixelFont16, artists.toLowerCase() + "\n\n" + music.toLowerCase() + "\n\n" + libgdx.toLowerCase(), Config.window_width / 2 + 75f, Config.window_height / 2f + 135f);
        rightCreditLabel.setWidth(Config.window_width / 2f - 150f);
        rightCreditLabel.setLineAlign(Align.left);
        rightCreditLabel.setFontScale(1f);

        thanksLabel = new TypingLabel(game.assets.pixelFont16, thanks.toLowerCase(), 0f, 115f);
        thanksLabel.setWidth(Config.window_width);
        thanksLabel.setLineAlign(Align.center);
        thanksLabel.setFontScale(1f);

        disclaimerLabel = new TypingLabel(game.assets.pixelFont16, "{COLOR=yellow}{JUMP=.2}{WAVE=0.9;1.2;1.75}" + disclaimer + "{ENDWAVE}{ENDJUMP}", 0f, 60f);
        disclaimerLabel.setWidth(Config.window_width);
        thanksLabel.setLineAlign(Align.center);
        disclaimerLabel.setFontScale(.7f);

    }

    @Override
    public void update(float dt) {
        accum += dt;
        titleLabel.update(dt);
        themeLabel.update(dt);
        leftCreditLabel.update(dt);
        rightCreditLabel.update(dt);
        thanksLabel.update(dt);
        disclaimerLabel.update(dt);
    }

    @Override
    public void render(SpriteBatch batch) {

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
            batch.draw(background, 0, 0, Config.window_width, Config.window_height);

            batch.setColor(0f, 0f, 0f, 0.6f);
            batch.draw(game.assets.pixelRegion, 25f, 130f, Config.window_width / 2f - 50f, 400f);
            batch.draw(game.assets.pixelRegion, Config.window_width / 2f + 25f, 130f, Config.window_width / 2f - 50f, 400f);

            batch.setColor(Color.WHITE);
            titleLabel.render(batch);
            themeLabel.render(batch);
            leftCreditLabel.render(batch);
            rightCreditLabel.render(batch);
            thanksLabel.render(batch);
            disclaimerLabel.render(batch);
            if (accum > 7.5) {
                TextureRegion catTexture = catAnimation.getKeyFrame(accum);
                TextureRegion dogTexture = dogAnimation.getKeyFrame(accum);
                batch.draw(catTexture, 330f, 180f);
                batch.draw(dogTexture, 60f, 175f);
            }
            batch.setColor(Color.WHITE);
        }
        batch.end();
    }

}
