package lando.systems.ld49.ui;

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
    private final Rectangle commsPanelBounds  = new Rectangle();
    private final Rectangle commsPanelVisible = new Rectangle();
    private final Rectangle commsPanelHidden  = new Rectangle();
    private final Rectangle commsPanelAcceptButton = new Rectangle();
    private final Rectangle commsPanelRejectButton = new Rectangle();
    private boolean acceptButtonPressed = false;
    private boolean rejectButtonPressed = false;
    private boolean respondedToComms = false;
    private boolean commsAnimating = false;
    private boolean commsOpen = false;
    private float ciaGuyAnimState = 0;

    private final TextureRegion meterTexture;
    private final TextureRegion bananaTexture;
    private final Rectangle tempMeterBounds = new Rectangle();
    private final Rectangle tempMeterBgBounds = new Rectangle();
    private final Rectangle structMeterBounds = new Rectangle();
    private final Rectangle structMeterBgBounds = new Rectangle();
    private final Rectangle tempIconBounds = new Rectangle();
    private final Rectangle structIconBounds = new Rectangle();
    private float tempPercent = 0;
    private float structPercent = 0;
    private float accum = 0;

    private final Rectangle controlPanelBounds = new Rectangle();

    public int cash = 42069;
    public int shots = 0;

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
//        float speed = 50;
//        accum += speed * dt;
//        tempPercent   = (MathUtils.sinDeg(accum) + 1) / 2f;
//        structPercent = (MathUtils.cosDeg(accum) + 1) / 2f;

        ciaGuyAnimState += dt;
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            toggleComms();
        }
    }

    public void draw(SpriteBatch batch) {
        // temperature and structural integrity meters
        {
            float margin = 5;

            tempMeterBgBounds.set(tempMeterBounds.x - margin, tempMeterBounds.y - margin, tempMeterBounds.width + 2 * margin, tempMeterBounds.height + 2 * margin);
            batch.setColor(0.4f, 0.4f, 0.4f, 1.0f);
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
            batch.setColor(0.4f, 0.4f, 0.4f, 1.0f);
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

        // cia guy comms window
        {
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

                BitmapFont font = game.assets.pixelFont16;
                float textMargin = 10;
                float scaleX = font.getScaleX();
                float scaleY = font.getScaleY();
                font.getData().setScale(0.5f);
                game.assets.layout.setText(font, commsText, Color.WHITE, bounds.width - 2 * textMargin, Align.topLeft, true);
                font.draw(batch, game.assets.layout, bounds.x + bounds.width + textMargin, bounds.y + bounds.height - textMargin);
                font.getData().setScale(scaleX, scaleY);

                uiElements.drawButton(batch, commsPanelAcceptButton, respondedToComms ? Color.DARK_GRAY : Color.WHITE, acceptButtonPressed);
                uiElements.drawButton(batch, commsPanelRejectButton, respondedToComms ? Color.DARK_GRAY : Color.WHITE, rejectButtonPressed);

                float textPressOffset = 2f;
                font.getData().setScale(0.5f);
                game.assets.layout.setText(font, "$ Pay Up $", respondedToComms ? Color.LIGHT_GRAY : Color.LIME, commsPanelAcceptButton.width, Align.center, false);
                font.draw(batch, game.assets.layout, commsPanelAcceptButton.x, commsPanelAcceptButton.y + commsPanelAcceptButton.height / 2f + game.assets.layout.height / 2f + 4 - (acceptButtonPressed ? textPressOffset : 0));
                game.assets.layout.setText(font, "Never!", respondedToComms ? Color.LIGHT_GRAY : Color.FIREBRICK, commsPanelRejectButton.width, Align.center, false);
                font.draw(batch, game.assets.layout, commsPanelRejectButton.x, commsPanelRejectButton.y + commsPanelRejectButton.height / 2f + game.assets.layout.height / 2f + 4 - (rejectButtonPressed ? textPressOffset : 0));
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

    // NOTE: these are inverted to get the banana needle angle correct without a hassle

    public void setTemperature(float temperaturePercent) {
        this.tempPercent = 1f - temperaturePercent;
    }

    public void setStructuralDmg(float structuralDmgPercent) {
        this.structPercent = 1f - structuralDmgPercent;
    }

}
