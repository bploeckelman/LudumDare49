package lando.systems.ld49.ui;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Back;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import lando.systems.ld49.Audio;
import lando.systems.ld49.Config;
import lando.systems.ld49.Main;
import lando.systems.ld49.screens.GameScreen;
import lando.systems.ld49.utils.accessors.RectangleAccessor;

public class UIGameOver extends InputAdapter {

    private final Main game;
    private final GameScreen gameScreen;
    private final UIElements uiElements;

    // TODO: add a comms dialogue for mob riot text
    // Paid: "The people appreciate your generosity"
    // Reject: "Viva la revolución!" (not sure if we can do accent chars)


    // comms panel related stuff
    private final String commsTextPrompt = "Fine cooling tower you have there. Be a shame if someone told an angry mob to destroy it...\n\nI'm sure we can find a way to prevent that, hmm?";
    private final String commsTextRejected = "It seems you've forgotten who's really in charge here.\n\nYOU may survive this, but your tower won't.";
    private final String commsTextAccepted = "I'm glad we see eye to eye. \n\nCarry on with your... \nweird little ritual or whatever.";
    private final String projectilesCtrlHeader = "Projectile Inventory";
    private final String griftingCtrlHeader = "Exploitation Station";
    private final String repairingCtrlHeader = "Structural\n\nRepair Station";
    private final String buyMoreProjectilesButtonText = "$ Buy More";
    private final String buyMoreGriftingButtonText = "$ Exploit More Efficiently";
    private final String buyStructureRepairingButtonText = "$ Repair Tower";

    private String commsText = commsTextPrompt;
    private String commsLeftName = "";
    private String commsRightName = "";

    private final Animation<TextureRegion> ciaGuyAnim;
    private final Animation<TextureRegion> presidenteAnim;
    private final Animation<TextureRegion> bananaManAnim;
    private final Rectangle commsLeftBounds = new Rectangle();
    private final Rectangle commsLeftVisible = new Rectangle();
    private final Rectangle commsLeftHidden = new Rectangle();
    private final Rectangle commsLeftAcceptButton = new Rectangle();
    private final Rectangle commsLeftRejectButton = new Rectangle();
    private final Rectangle commsRightBounds = new Rectangle();
    private final Rectangle commsRightVisible = new Rectangle();
    private final Rectangle commsRightHidden = new Rectangle();
    private final Rectangle commsLeftDialogueBounds = new Rectangle();
    private final Rectangle commsRightDialogueBounds = new Rectangle();
    private final Rectangle commsLeftNamePlateBounds = new Rectangle();
    private final Rectangle commsRightNamePlateBounds = new Rectangle();
    private boolean acceptButtonPressed = false;
    private boolean rejectButtonPressed = false;
    private boolean respondedToComms = false;
    private boolean commsAnimating = false;
    private boolean commsOpen = false;
    private float ciaGuyAnimState = 0;
    private float presidenteAnimState = 0;
    private float bananaManAnimState = 0;

    private float commsTimer = 0;
    private final String commaRegex = "(\\d)(?=(\\d{3})+$)";

    public UIGameOver(GameScreen gameScreen, UIElements uiElements) {
        this.game = gameScreen.game;
        this.gameScreen = gameScreen;
        this.uiElements = uiElements;

        this.ciaGuyAnim = new Animation<>(0.2f, game.assets.atlas.findRegions("people/cia-guy/cia-guy"), Animation.PlayMode.LOOP);
        this.bananaManAnim = new Animation<>(0.2f, game.assets.atlas.findRegions("people/banana-man/banana-man-talk/banana-man-talk"), Animation.PlayMode.LOOP);
        this.presidenteAnim = new Animation<>(0.2f, game.assets.atlas.findRegions("people/dole-presidente/dole-presidente-talk/dole-presidente-talk"), Animation.PlayMode.LOOP);
        // presidente is in the right window, so he should face left
        for (TextureRegion texture : presidenteAnim.getKeyFrames()) {
            texture.flip(true, false);
        }

        float commsSize = 128;
        this.commsLeftVisible.set(0, Config.window_height - commsSize, commsSize, commsSize);
        this.commsLeftHidden.set(-commsSize, commsLeftVisible.y, commsSize, commsSize);
        this.commsLeftBounds.set(commsOpen ? commsLeftVisible : commsLeftHidden);

        this.commsRightVisible.set(Config.window_width - commsSize, Config.window_height - commsSize, commsSize, commsSize);
        this.commsRightHidden.set(Config.window_width, Config.window_height - commsSize, commsSize, commsSize);
        this.commsRightBounds.set(commsOpen ? commsRightVisible : commsRightHidden);

        float dialogueWidth = 4 * commsSize;
        this.commsLeftDialogueBounds.set(commsLeftVisible.x + commsLeftVisible.width, commsLeftBounds.y, dialogueWidth, commsSize);
        this.commsRightDialogueBounds.set(Config.window_width - commsRightVisible.width - dialogueWidth, commsRightBounds.y, dialogueWidth, commsSize);

        float buttonHeight = 40;
        float buttonWidth = commsSize;
        this.commsLeftNamePlateBounds.set(commsLeftVisible.x, commsLeftVisible.y - buttonHeight, buttonWidth, buttonHeight);
        this.commsRightNamePlateBounds.set(commsRightVisible.x, commsRightVisible.y - buttonHeight, buttonWidth, buttonHeight);
        this.commsLeftAcceptButton.set(commsLeftVisible.x, commsLeftVisible.y - 2 * buttonHeight, buttonWidth, buttonHeight);
        this.commsLeftRejectButton.set(commsLeftVisible.x, commsLeftVisible.y - 3 * buttonHeight, buttonWidth, buttonHeight);

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
            Timeline.createParallel()
                    .push(
                            Tween.to(commsLeftBounds, RectangleAccessor.XYWH, 0.2f)
                                    .target(commsLeftHidden.x, commsLeftHidden.y, commsLeftHidden.width, commsLeftHidden.height)
                                    .ease(Back.IN)
                    )
                    .push(
                            Tween.to(commsRightBounds, RectangleAccessor.XYWH, 0.2f)
                                    .target(commsRightHidden.x, commsRightHidden.y, commsRightHidden.width, commsRightHidden.height)
                                    .ease(Back.IN)
                    )
                    .setCallback((type, source) -> {
                        commsOpen = false;
                        commsAnimating = false;
                    })
                    .start(game.tween);
        } else {
            Timeline.createParallel()
                    .push(
                            Tween.to(commsLeftBounds, RectangleAccessor.XYWH, 0.3f)
                                    .target(commsLeftVisible.x, commsLeftVisible.y, commsLeftVisible.width, commsLeftVisible.height)
                                    .ease(Back.OUT)
                    )
                    .push(
                            Tween.to(commsRightBounds, RectangleAccessor.XYWH, 0.3f)
                                    .target(commsRightVisible.x, commsRightVisible.y, commsRightVisible.width, commsRightVisible.height)
                                    .ease(Back.OUT)
                    )
                    .setCallback((type, source) -> {
                        commsOpen = true;
                        commsTimer = 0;
                        commsAnimating = false;

                    })
                    .start(game.tween);
        }
    }

    public void update(float dt, boolean paused) {
        if (!paused && commsOpen){
            commsTimer += dt;
            if (commsTimer > 15){
                rejectComms();
                commsTimer = 0;
            }
        }


        ciaGuyAnimState += dt;
        presidenteAnimState += dt;
        bananaManAnimState += dt;

    }

    public void draw(SpriteBatch batch) {
        // comms windows
        {
            float margin = 0;
            Rectangle bounds;
            TextureRegion keyframe;
            Animation<TextureRegion> anim = respondedToComms ? bananaManAnim : ciaGuyAnim;
            float animState = respondedToComms ? bananaManAnimState : ciaGuyAnimState;
            commsLeftName = respondedToComms ? "Banana\nCitizen" : "CIA Guy";
            commsRightName = "Dole\nPresidente";

            bounds = commsLeftBounds;
            keyframe = anim.getKeyFrame(animState);
            uiElements.drawPanel(batch, commsLeftBounds);
            if (!commsAnimating) {
                batch.draw(keyframe, bounds.x + margin, bounds.y + margin, bounds.width - 2 * margin, bounds.height - 2 * margin);
            }

            bounds = commsRightBounds;
            keyframe = presidenteAnim.getKeyFrame(presidenteAnimState);
            uiElements.drawPanel(batch, commsRightBounds);
            if (!commsAnimating) {
                batch.draw(keyframe, bounds.x + margin, bounds.y + margin, bounds.width - 2 * margin, bounds.height - 2 * margin);
            }

            if (commsOpen && !commsAnimating) {
                bounds = commsLeftDialogueBounds;
                batch.setColor(0.2f, 0.2f, 0.2f, 0.5f);
                batch.draw(game.assets.pixelRegion, bounds.x, bounds.y, bounds.width, bounds.height);
                batch.setColor(Color.LIGHT_GRAY);
                game.assets.debugNinePatch.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
                batch.setColor(Color.WHITE);

                BitmapFont font = game.assets.pixelFont16;
                float textMargin = 10;
                float scaleX = font.getScaleX();
                float scaleY = font.getScaleY();
                font.getData().setScale(0.5f);
                game.assets.layout.setText(font, commsText, Color.WHITE, bounds.width - 2 * textMargin, Align.left, true);
                font.draw(batch, game.assets.layout, bounds.x + textMargin, bounds.y + bounds.height - textMargin);
                font.getData().setScale(scaleX, scaleY);

                if (!respondedToComms) {
                    Color acceptButtonColor = Color.WHITE;
                    uiElements.drawButton(batch, commsLeftAcceptButton, acceptButtonColor, acceptButtonPressed);
                    uiElements.drawButton(batch, commsLeftRejectButton, respondedToComms ? Color.DARK_GRAY : Color.WHITE, rejectButtonPressed);

                    float textPressOffset = 2f;
                    font.getData().setScale(0.5f);
                    Color acceptButtonTextColor = Color.LIME;
                    game.assets.layout.setText(font, "$ Pay Up $", acceptButtonTextColor, commsLeftAcceptButton.width, Align.center, false);
                    font.draw(batch, game.assets.layout, commsLeftAcceptButton.x, commsLeftAcceptButton.y + commsLeftAcceptButton.height / 2f + game.assets.layout.height / 2f + 4 - (acceptButtonPressed ? textPressOffset : 0));
                    game.assets.layout.setText(font, "Never!", respondedToComms ? Color.LIGHT_GRAY : Color.FIREBRICK, commsLeftRejectButton.width, Align.center, false);
                    font.draw(batch, game.assets.layout, commsLeftRejectButton.x, commsLeftRejectButton.y + commsLeftRejectButton.height / 2f + game.assets.layout.height / 2f + 4 - (rejectButtonPressed ? textPressOffset : 0));
                    font.getData().setScale(scaleX, scaleY);
                }

                // left name plate and text
                bounds = commsLeftNamePlateBounds;
                batch.setColor(0.2f, 0.2f, 0.2f, 0.5f);
                batch.draw(game.assets.pixelRegion, bounds.x, bounds.y, bounds.width, bounds.height);
                batch.setColor(Color.LIGHT_GRAY);
                game.assets.debugNinePatch.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
                batch.setColor(Color.WHITE);
                font.getData().setScale(0.4f);
                game.assets.layout.setText(font, commsLeftName, Color.DARK_GRAY, commsLeftNamePlateBounds.width, Align.center, false);
                font.draw(batch, game.assets.layout, commsLeftNamePlateBounds.x, commsLeftNamePlateBounds.y + commsLeftNamePlateBounds.height / 2f + game.assets.layout.height / 2f);
                font.getData().setScale(scaleX, scaleY);

                bounds = commsRightDialogueBounds;
                batch.setColor(0.2f, 0.2f, 0.2f, 0.5f);
                batch.draw(game.assets.pixelRegion, bounds.x, bounds.y, bounds.width, bounds.height);
                batch.setColor(Color.LIGHT_GRAY);
                game.assets.debugNinePatch.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
                batch.setColor(Color.WHITE);

                scaleX = font.getScaleX();
                scaleY = font.getScaleY();
                font.getData().setScale(0.5f);
                game.assets.layout.setText(font, commsText, Color.WHITE, bounds.width - 2 * textMargin, Align.right, true);
                font.draw(batch, game.assets.layout, bounds.x + textMargin, bounds.y + bounds.height - textMargin);
                font.getData().setScale(scaleX, scaleY);

                // right name plate and text
                bounds = commsRightNamePlateBounds;
                batch.setColor(0.2f, 0.2f, 0.2f, 0.5f);
                batch.draw(game.assets.pixelRegion, bounds.x, bounds.y, bounds.width, bounds.height);
                batch.setColor(Color.LIGHT_GRAY);
                game.assets.debugNinePatch.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
                batch.setColor(Color.WHITE);
                scaleX = font.getScaleX();
                scaleY = font.getScaleY();
                font.getData().setScale(0.38f);
                game.assets.layout.setText(font, commsRightName, Color.DARK_GRAY, commsRightNamePlateBounds.width, Align.center, true);
                font.draw(batch, game.assets.layout, commsRightNamePlateBounds.x, commsRightNamePlateBounds.y + commsRightNamePlateBounds.height / 2f + game.assets.layout.height / 2f);
                font.getData().setScale(scaleX, scaleY);
            }
        }
    }

    // technically the windowCamera should be used to unproject these coords,
    // but it's not really needed since windowCamera is sized to the window,
    // we can just flip the touch y coords instead

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        float x = screenX;
        float y = Config.window_height - screenY;

        if (commsOpen && !commsAnimating && !respondedToComms) {
            if (commsLeftAcceptButton.contains(x, y)) {
                acceptButtonPressed = true;
                return true;
            }
            if (commsLeftRejectButton.contains(x, y)) {
                rejectButtonPressed = true;
                return true;
            }
        }


        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        float x = screenX;
        float y = Config.window_height - screenY;

        if (commsOpen && !commsAnimating && !respondedToComms) {
            acceptButtonPressed = false;
            rejectButtonPressed = false;
            if (commsLeftAcceptButton.contains(x, y)) {
                respondedToComms = true;
                commsText = commsTextAccepted;

                gameScreen.world.makeBananasHappy();

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        toggleComms();
                    }
                }, 4);
                return true;
            }
            if (commsLeftRejectButton.contains(x, y)) {
                rejectComms();
                return true;
            }
        }


        return super.touchUp(screenX, screenY, pointer, button);
    }

    private void rejectComms() {
        respondedToComms = true;
        commsText = commsTextRejected;
        gameScreen.world.makeBananasRiot();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                toggleComms();
            }
        }, 4);
    }

}
