package lando.systems.ld49.ui;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Back;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import lando.systems.ld49.Config;
import lando.systems.ld49.Main;
import lando.systems.ld49.screens.GameScreen;
import lando.systems.ld49.utils.accessors.RectangleAccessor;

public class UI extends InputAdapter {

    private final Main game;
    private final UIElements uiElements;

    // comms panel related stuff
    private final String commsTextPrompt = "Fine cooling tower you have there. Be a shame if someone told an angry mob to destroy it...\n\nI'm sure we can find a way to prevent that, hmm?";
    private final String commsTextRejected = "It seems you've forgotten who's really in charge here.\n\nYOU may survive this, but your tower won't.";
    private final String commsTextAccepted = "I'm glad we see eye to eye. \n\nCarry on with your... \nweird little ritual or whatever.";
    private String commsText = commsTextPrompt;
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
    private boolean acceptButtonPressed = false;
    private boolean rejectButtonPressed = false;
    private boolean respondedToComms = false;
    private boolean commsAnimating = false;
    private boolean commsOpen = false;
    private float ciaGuyAnimState = 0;
    private float presidenteAnimState = 0;
    private float bananaManAnimState = 0;

    private final TextureRegion meterTexture;
    private final TextureRegion bananaTexture;
    private final Rectangle tempMeterBounds = new Rectangle();
    private final Rectangle tempMeterBgBounds = new Rectangle();
    private final Rectangle structMeterBounds = new Rectangle();
    private final Rectangle structMeterBgBounds = new Rectangle();
    private final Rectangle tempIconBounds = new Rectangle();
    private final Rectangle structIconBounds = new Rectangle();
    private float structDamageFlash = 0;
    private float tempWarningPulseRate = 0;
    private float tempFlash = 0;
    private float tempPercent = 0;
    private float structPercent = 0;
    private float accum = 0;
    private float tempAccum = 0;

    private final Rectangle controlPanelBounds = new Rectangle();

    public int cash = 42069;
    public int shots = 0;

    public UI(Main game, UIElements uiElements) {
        this.game = game;
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
        this.commsLeftAcceptButton.set(commsLeftVisible.x, commsLeftVisible.y - buttonHeight, buttonWidth, buttonHeight);
        this.commsLeftRejectButton.set(commsLeftVisible.x, commsLeftVisible.y - buttonHeight * 2, buttonWidth, buttonHeight);

        this.meterTexture = game.assets.atlas.findRegion("meter");
        this.bananaTexture = game.assets.atlas.findRegion("banana");

        float margin = 5;
        this.tempMeterBounds.set(
                margin, margin,
                meterTexture.getRegionWidth() + 2 * margin,
                meterTexture.getRegionHeight() + 2 * margin);
        this.structMeterBounds.set(
                Config.window_width - meterTexture.getRegionWidth() - 2 * margin, margin,
                meterTexture.getRegionWidth() + 2 * margin,
                meterTexture.getRegionHeight() + 2 * margin);
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
                        commsAnimating = false;
                        ((GameScreen)game.getScreen()).tutorial.addCIA();

                    })
                    .start(game.tween);
        }
    }

    public void update(float dt) {
        // yes this is dumb, it's ludum dare don't worry about it
        float temp = 1f - tempPercent;
        if      (temp  < .2f)  tempWarningPulseRate =  50;
        else if (temp  < .4f)  tempWarningPulseRate = 300;
        else if (temp  < .6f)  tempWarningPulseRate = 500;
        else if (temp  < .8f)  tempWarningPulseRate = 700;
        else if (temp  <  1f)  tempWarningPulseRate = 1000;
        accum += tempWarningPulseRate * dt;
        tempFlash = (MathUtils.sinDeg(accum) + 1) / 2f;
        // force no flashy when low, for some value of low
        if (temp < .2f) tempFlash = 0;
        // force solid red when max
        if (temp == 1f) tempFlash = 1;

        // if we just got damaged, single pulse a warning flash in the structure meter
        float structFlashSlowdownSpeed = 1;
        structDamageFlash -= structFlashSlowdownSpeed * dt;
        if (structDamageFlash < 0) {
            structDamageFlash = 0;
        }

        // if we're in real structural danger, flash or solid red the structure meter
        float struct = MathUtils.clamp(1f - structPercent, 0, 1);
        if (struct == 1f) {
            structDamageFlash = 1;
        } else if (struct >= .8) {
            tempAccum += 1000 * dt;
            structDamageFlash = (MathUtils.sinDeg(tempAccum) + 1) / 2f;
        }

        ciaGuyAnimState += dt;
        presidenteAnimState += dt;
        bananaManAnimState += dt;

        // TODO: just for testing, remove me
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            toggleComms();
        }
    }

    public void draw(SpriteBatch batch) {
        // temperature and structural integrity meters
        {
            float margin = 5;

            tempMeterBgBounds.set(tempMeterBounds.x - margin, tempMeterBounds.y - margin, tempMeterBounds.width + 2 * margin, tempMeterBounds.height + 2 * margin);
            batch.setColor(
                    MathUtils.lerp(0.4f, 1f, tempFlash),
                    MathUtils.lerp(0.4f, 0f, tempFlash),
                    MathUtils.lerp(0.4f, 0f, tempFlash), 1.0f);
            uiElements.drawPanel(batch, tempMeterBgBounds);
            batch.setColor(Color.WHITE);
            batch.draw(meterTexture, tempMeterBounds.x, tempMeterBounds.y, tempMeterBounds.width, tempMeterBounds.height);
            batch.draw(bananaTexture,
                    tempMeterBounds.x + tempMeterBounds.width / 2f - 23,
                    tempMeterBounds.y,
                    23, 23,
                    bananaTexture.getRegionWidth(), bananaTexture.getRegionHeight(),
                    1f, 1f,
                    tempPercent * 180
            );

            structMeterBgBounds.set(structMeterBounds.x - margin, structMeterBounds.y - margin, structMeterBounds.width + 2 * margin, structMeterBounds.height + 2 * margin);
            batch.setColor(
                    MathUtils.lerp(0.4f, 1f, structDamageFlash),
                    MathUtils.lerp(0.4f, 0f, structDamageFlash),
                    MathUtils.lerp(0.4f, 0f, structDamageFlash), 1.0f);
            uiElements.drawPanel(batch, structMeterBgBounds);
            batch.setColor(Color.WHITE);
            batch.draw(meterTexture, structMeterBounds.x, structMeterBounds.y, structMeterBounds.width, structMeterBounds.height);
            batch.draw(bananaTexture,
                    structMeterBounds.x + structMeterBounds.width / 2f - 23,
                    structMeterBounds.y,
                    23, 23,
                    bananaTexture.getRegionWidth(), bananaTexture.getRegionHeight(),
                    1f, 1f,
                    structPercent * 180
            );

            tempIconBounds.set(tempMeterBounds.x, tempMeterBounds.y + tempMeterBounds.height - 38, 32, 38);
            structIconBounds.set(structMeterBounds.x, structMeterBounds.y + structMeterBounds.height - 38, 32, 38);
            uiElements.drawIcon(batch, UIElements.Icon.temperature, tempIconBounds);
            uiElements.drawIcon(batch, UIElements.Icon.structure, structIconBounds);
        }

        // main control panel
        {
            controlPanelBounds.set(
                    tempMeterBounds.x + tempMeterBounds.width + 5, 0,
                    structMeterBounds.x - (tempMeterBounds.x + tempMeterBounds.width) - 10,
                    tempMeterBounds.height * (3f / 4f));
            batch.setColor(0.5f, 0.5f, 0.5f, 1.0f);
            uiElements.drawPanel(batch, controlPanelBounds);
            batch.setColor(Color.WHITE);

            float y;
            float lineSpacing = 10;
            BitmapFont font = game.assets.pixelFont16;
            font.getData().setScale(0.5f);
            game.assets.layout.setText(font, "$" + cash, Color.GREEN, Align.left, (int) controlPanelBounds.width, false);
            y = controlPanelBounds.y + controlPanelBounds.height - 10;
            font.draw(batch, game.assets.layout, controlPanelBounds.x + game.assets.layout.width, y);

            game.assets.layout.setText(font, "Shots: " + shots, Color.SKY, Align.left, (int) controlPanelBounds.width, false);
            y -= game.assets.layout.height + lineSpacing;
            font.draw(batch, game.assets.layout, controlPanelBounds.x + game.assets.layout.width, y);
            font.getData().setScale(1f);
        }

        // comms windows
        {
            float margin = 0;
            Rectangle bounds;
            TextureRegion keyframe;
            Animation<TextureRegion> anim = respondedToComms ? bananaManAnim : ciaGuyAnim;
            float animState = respondedToComms ? bananaManAnimState : ciaGuyAnimState;

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
                // TODO: update dialogue window (wider, but only as tall as comms window), add another one on the right

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

                uiElements.drawButton(batch, commsLeftAcceptButton, respondedToComms ? Color.DARK_GRAY : Color.WHITE, acceptButtonPressed);
                uiElements.drawButton(batch, commsLeftRejectButton, respondedToComms ? Color.DARK_GRAY : Color.WHITE, rejectButtonPressed);

                float textPressOffset = 2f;
                font.getData().setScale(0.5f);
                game.assets.layout.setText(font, "$ Pay Up $", respondedToComms ? Color.LIGHT_GRAY : Color.LIME, commsLeftAcceptButton.width, Align.center, false);
                font.draw(batch, game.assets.layout, commsLeftAcceptButton.x, commsLeftAcceptButton.y + commsLeftAcceptButton.height / 2f + game.assets.layout.height / 2f + 4 - (acceptButtonPressed ? textPressOffset : 0));
                game.assets.layout.setText(font, "Never!", respondedToComms ? Color.LIGHT_GRAY : Color.FIREBRICK, commsLeftRejectButton.width, Align.center, false);
                font.draw(batch, game.assets.layout, commsLeftRejectButton.x, commsLeftRejectButton.y + commsLeftRejectButton.height / 2f + game.assets.layout.height / 2f + 4 - (rejectButtonPressed ? textPressOffset : 0));
                font.getData().setScale(scaleX, scaleY);

                // TODO: add a comms dialogue for mob riot text
                // Paid: "The people appreciate your generosity"
                // Reject: "Viva la revolución!" (not sure if we can do accent chars)


                bounds = commsRightDialogueBounds;
                batch.setColor(0.2f, 0.2f, 0.2f, 0.5f);
                batch.draw(game.assets.pixelRegion, bounds.x, bounds.y, bounds.width, bounds.height);
                batch.setColor(Color.LIGHT_GRAY);
                game.assets.debugNinePatch.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
                batch.setColor(Color.WHITE);

                font = game.assets.pixelFont16;
                textMargin = 10;
                scaleX = font.getScaleX();
                scaleY = font.getScaleY();
                font.getData().setScale(0.5f);
                game.assets.layout.setText(font, commsText, Color.WHITE, bounds.width - 2 * textMargin, Align.right, true);
                font.draw(batch, game.assets.layout, bounds.x + textMargin, bounds.y + bounds.height - textMargin);
                font.getData().setScale(scaleX, scaleY);
            }
        }
    }

    // technically the windowCamera should be used to unproject these coords,
    // but it's not really needed since windowCamera is sized to the window,
    // we can just flip the touch y coords instead

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (commsOpen && !commsAnimating && !respondedToComms) {
            if (commsLeftAcceptButton.contains(screenX, Config.window_height - screenY)) {
                acceptButtonPressed = true;
                return true;
            }
            if (commsLeftRejectButton.contains(screenX, Config.window_height - screenY)) {
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
            if (commsLeftAcceptButton.contains(screenX, Config.window_height - screenY)) {
                respondedToComms = true;
                commsText = commsTextAccepted;
                if (game.getScreen() instanceof GameScreen) {
                    GameScreen currentScreen = (GameScreen) game.getScreen();
                    currentScreen.world.makeBananasHappy();
                }
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        toggleComms();
                    }
                }, 4);
                return true;
            }
            if (commsLeftRejectButton.contains(screenX, Config.window_height - screenY)) {
                respondedToComms = true;
                commsText = commsTextRejected;
                if (game.getScreen() instanceof GameScreen) {
                    GameScreen currentScreen = (GameScreen) game.getScreen();
                    currentScreen.world.makeBananasRiot();
                }
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        toggleComms();
                    }
                }, 4);
                return true;
            }
        }
        return super.touchUp(screenX, screenY, pointer, button);
    }

    // NOTE: these are inverted to get the banana needle angle correct without a hassle

    public void setTemperature(float temperaturePercent) {
        this.tempPercent = 1f - temperaturePercent;
    }

    public void setStructuralDmg(float structuralDmgPercent) {
        this.structPercent = 1f - structuralDmgPercent;
    }

    public void structureDamaged() {
        structDamageFlash = 1f;
    }
}
