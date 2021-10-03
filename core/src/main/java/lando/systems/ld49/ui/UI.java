package lando.systems.ld49.ui;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Back;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import lando.systems.ld49.Config;
import lando.systems.ld49.Main;
import lando.systems.ld49.utils.accessors.RectangleAccessor;

public class UI extends InputAdapter {

    private final Main game;
    private final UIElements uiElements;

    // comms panel related stuff
    private final String commsTextPrompt = "Fine cooling tower you have there. Be a shame if an angry mob were told to destroy it...\n(By me. I'd be the one telling them.)\n\nI'm sure we can find a way to prevent that, hmm?";
    private final String commsTextRejected = "It seems you've forgotten who's really in charge here.\n\nYOU may survive this, but your tower won't.";
    private final String commsTextAccepted = "I'm glad we see eye to eye. \n\nCarry on with your... \nweird little ritual or whatever.";
    private String commsText = commsTextPrompt;
    private final Animation<TextureRegion> ciaGuyAnim;
    private final Rectangle commsPanelBounds  = new Rectangle();
    private final Rectangle commsPanelVisible = new Rectangle();
    private final Rectangle commsPanelHidden  = new Rectangle();
    private final Rectangle commsPanelAcceptButton = new Rectangle();
    private final Rectangle commsPanelRejectButton = new Rectangle();
    private boolean acceptButtonPressed = false;
    private boolean rejectButtonPressed = false;
    private boolean respondedToComms = false;
    private boolean commsAnimating = false;
    private boolean commsOpen = true;
    private float ciaGuyAnimState = 0;

    public UI(Main game, UIElements uiElements) {
        this.game = game;
        this.uiElements = uiElements;

        this.ciaGuyAnim = new Animation<>(0.2f, game.assets.atlas.findRegions("people/cia-guy"));
        this.ciaGuyAnim.setPlayMode(Animation.PlayMode.LOOP);

        float commsSize = 256;
        this.commsPanelVisible.set(0, Config.window_height - commsSize, commsSize, commsSize);
        this.commsPanelHidden.set(-commsSize, commsPanelVisible.y, commsSize, commsSize);
        this.commsPanelBounds.set(commsOpen ? commsPanelVisible : commsPanelHidden);

        float buttonHeight = 40;
        float buttonWidth = commsPanelVisible.width / 2f;
        this.commsPanelAcceptButton.set(commsPanelVisible.x + commsPanelVisible.width, commsPanelVisible.y, buttonWidth, buttonHeight);
        this.commsPanelRejectButton.set(commsPanelVisible.x + commsPanelVisible.width * 2 - buttonWidth, commsPanelVisible.y, buttonWidth, buttonHeight);
    }

    public void toggleComms() {
        if (commsAnimating) {
            return;
        } else {
            commsAnimating = true;
        }

        if (commsOpen) {
            respondedToComms = false;
            commsText = commsTextPrompt;
            Tween.to(commsPanelBounds, RectangleAccessor.XYWH, 0.2f)
                    .target(commsPanelHidden.x, commsPanelHidden.y, commsPanelHidden.width, commsPanelHidden.height)
                    .ease(Back.IN)
                    .setCallback((type, source) -> {
                        commsOpen = false;
                        commsAnimating = false;
                    })
                    .start(game.tween);
        } else {
            Tween.to(commsPanelBounds, RectangleAccessor.XYWH, 0.3f)
                    .target(commsPanelVisible.x, commsPanelVisible.y, commsPanelVisible.width, commsPanelVisible.height)
                    .ease(Back.OUT)
                    .setCallback((type, source) -> {
                        commsOpen = true;
                        commsAnimating = false;
                    })
                    .start(game.tween);
        }
    }

    public void update(float dt) {
        ciaGuyAnimState += dt;
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            toggleComms();
        }
    }

    public void draw(SpriteBatch batch) {
        float margin = 0;
        Rectangle bounds = commsPanelBounds;
        TextureRegion cia = ciaGuyAnim.getKeyFrame(ciaGuyAnimState);
        uiElements.drawPanel(batch, commsPanelBounds);
        batch.draw(cia, bounds.x + margin, bounds.y + margin, bounds.width - 2 * margin, bounds.height - 2 * margin);

        if (commsOpen && !commsAnimating) {
            batch.setColor(0.2f, 0.2f, 0.2f, 0.5f);
            batch.draw(game.assets.pixel, bounds.x + bounds.width, bounds.y, bounds.width, bounds.height);
            batch.setColor(Color.LIGHT_GRAY);
            game.assets.debugNinePatch.draw(batch, bounds.x + bounds.width, bounds.y, bounds.width, bounds.height);
            batch.setColor(Color.WHITE);

            float textMargin = 10;
            float scaleX = game.assets.font.getScaleX();
            float scaleY = game.assets.font.getScaleY();
            game.assets.font.getData().setScale(1.25f);
            game.assets.layout.setText(game.assets.font, commsText, Color.WHITE, bounds.width - 2 * textMargin, Align.topLeft, true);
            game.assets.font.draw(batch, game.assets.layout, bounds.x + bounds.width + textMargin, bounds.y + bounds.height - textMargin);
            game.assets.font.getData().setScale(scaleX, scaleY);

            uiElements.drawButton(batch, commsPanelAcceptButton, respondedToComms ? Color.DARK_GRAY : Color.WHITE, acceptButtonPressed);
            uiElements.drawButton(batch, commsPanelRejectButton, respondedToComms ? Color.DARK_GRAY : Color.WHITE, rejectButtonPressed);

            float textPressOffset = 2f;
            game.assets.font.getData().setScale(1f);
            game.assets.layout.setText(game.assets.font, "Pay Up ($$$)", respondedToComms ? Color.LIGHT_GRAY : Color.LIME, commsPanelAcceptButton.width, Align.center, false);
            game.assets.font.draw(batch, game.assets.layout, commsPanelAcceptButton.x, commsPanelAcceptButton.y + commsPanelAcceptButton.height / 2f + game.assets.layout.height / 2f + 4 - (acceptButtonPressed ? textPressOffset : 0));
            game.assets.layout.setText(game.assets.font, "Never! (T_T)", respondedToComms ? Color.LIGHT_GRAY : Color.FIREBRICK, commsPanelRejectButton.width, Align.center, false);
            game.assets.font.draw(batch, game.assets.layout, commsPanelRejectButton.x, commsPanelRejectButton.y + commsPanelRejectButton.height / 2f + game.assets.layout.height / 2f + 4 - (rejectButtonPressed ? textPressOffset : 0));
            game.assets.font.getData().setScale(scaleX, scaleY);
        }
    }

    // technically the windowCamera should be used to unproject these coords,
    // but it's not really needed since windowCamera is sized to the window,
    // we can just flip the touch y coords instead

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (commsOpen && !commsAnimating && !respondedToComms) {
            if (commsPanelAcceptButton.contains(screenX, Config.window_height - screenY)) {
                acceptButtonPressed = true;
                return true;
            }
            if (commsPanelRejectButton.contains(screenX, Config.window_height - screenY)) {
                rejectButtonPressed = true;
                return true;
            }
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (commsOpen && !commsAnimating && !respondedToComms) {
            acceptButtonPressed = false;
            rejectButtonPressed = false;
            if (commsPanelAcceptButton.contains(screenX, Config.window_height - screenY)) {
                respondedToComms = true;
                commsText = commsTextAccepted;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        toggleComms();
                    }
                }, 2);
                return true;
            }
            if (commsPanelRejectButton.contains(screenX, Config.window_height - screenY)) {
                respondedToComms = true;
                commsText = commsTextRejected;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        toggleComms();
                    }
                }, 2);
                return true;
            }
        }
        return super.touchUp(screenX, screenY, pointer, button);
    }

}
