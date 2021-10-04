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
import lando.systems.ld49.screens.BaseScreen;
import lando.systems.ld49.screens.GameScreen;
import lando.systems.ld49.utils.accessors.RectangleAccessor;

public class UI extends InputAdapter {

    private final Main game;
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
    private boolean canAcceptCiaBuyoffOffer = false;
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
    private final Rectangle controlPanelBounds = new Rectangle();
    private final Rectangle projectilesCtrlBounds = new Rectangle();
    private final Rectangle griftingCtrlBounds = new Rectangle();
    private final Rectangle repairingCtrlBounds = new Rectangle();
    private final Rectangle buyProjectilesButtonBounds = new Rectangle();
    private final Rectangle buyMoreGriftingButtonBounds = new Rectangle();
    private final Rectangle buyRepairingButtonBounds = new Rectangle();
    private final Rectangle griftProgressBarBounds = new Rectangle();
    private boolean buyProjectilesButtonPressed = false;
    private boolean buyMoreGriftingButtonPressed = false;
    private boolean buyRepairingButtonPressed = false;
    private boolean canBuyProjectiles = false;
    private boolean canBuyMoreGrifting = false;
    private boolean canBuyRepairing = false;
    private final float griftSpeedIncrement = 0.05f;
    private float griftSpeed = 0.05f;
    private float structDamageFlash = 0;
    private float tempWarningPulseRate = 0;
    private float tempFlash = 0;
    private float tempPercent = 0;
    private float structPercent = 0;
    private float accum = 0;
    private float tempAccum = 0;
    private float commsTimer = 0;
    private float griftProgressPercent = 0;

    // TODO: change these values over time?
    static class PurchasePrice {
        static int ciaBuyoff = 1000;
        static int grift = 100;
        static int projectiles = 100;
        static int repairs = 5000;
    }

    private final int projectilesPerPurchase = 3;
    private final int startingCash = 200;

    // TODO: move these, they belong in a museum
    public int numProjectiles = 5;
    public int cashOnHand = startingCash;

    private String cashOnHandString;
    private final String commaRegex = "(\\d)(?=(\\d{3})+$)";
    private void addToCash(int amount) {
        cashOnHand += amount;
        String source = "$" + Integer.toString(cashOnHand, 10);
        cashOnHandString = source.replaceAll(commaRegex, "$1,");
    }

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
        this.commsLeftNamePlateBounds.set(commsLeftVisible.x, commsLeftVisible.y - buttonHeight, buttonWidth, buttonHeight);
        this.commsRightNamePlateBounds.set(commsRightVisible.x, commsRightVisible.y - buttonHeight, buttonWidth, buttonHeight);
        this.commsLeftAcceptButton.set(commsLeftVisible.x, commsLeftVisible.y - 2 * buttonHeight, buttonWidth, buttonHeight);
        this.commsLeftRejectButton.set(commsLeftVisible.x, commsLeftVisible.y - 3 * buttonHeight, buttonWidth, buttonHeight);

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

        // initializes cash string
        addToCash(0);
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
                        ((GameScreen)game.getScreen()).tutorial.addCIA();

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

        griftProgressPercent += griftSpeed * dt;
        if (griftProgressPercent > 1) {
            griftProgressPercent = 0;

            game.audio.playSound(Audio.Sounds.dingUp, 0.35f);

            addToCash(50);
        }

        ciaGuyAnimState += dt;
        presidenteAnimState += dt;
        bananaManAnimState += dt;

        canAcceptCiaBuyoffOffer = (cashOnHand >= PurchasePrice.ciaBuyoff);
        canBuyMoreGrifting      = (cashOnHand >= PurchasePrice.grift);
        canBuyProjectiles       = (cashOnHand >= PurchasePrice.projectiles);
        canBuyRepairing         = (cashOnHand >= PurchasePrice.repairs);

//        // for testing
//        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
//            toggleComms();
//            if (game.getScreen() instanceof GameScreen) {
//                GameScreen currentScreen = (GameScreen) game.getScreen();
//                currentScreen.world.makeBananasPrepRiot();
//            }
//        }
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
            BitmapFont font = game.assets.pixelFont16;
            GlyphLayout layout = game.assets.layout;

            // setup overall control panel bounds and draw it
            controlPanelBounds.set(
                    tempMeterBounds.x + tempMeterBounds.width + 5, 0,
                    structMeterBounds.x - (tempMeterBounds.x + tempMeterBounds.width) - 10,
                    tempMeterBounds.height * (3f / 4f));
            batch.setColor(0.5f, 0.5f, 0.5f, 1.0f);
            uiElements.drawPanel(batch, controlPanelBounds);
            batch.setColor(Color.WHITE);

            // setup sub-regions for control panel areas
            float projWidth = (1f / 4f) * controlPanelBounds.width;
            float griftWidth = (2f / 4f) * controlPanelBounds.width;
            float repairWidth = (1f / 4f) * controlPanelBounds.width;
            projectilesCtrlBounds.set(controlPanelBounds.x, controlPanelBounds.y, projWidth, controlPanelBounds.height);
            griftingCtrlBounds.set(controlPanelBounds.x + projWidth, controlPanelBounds.y, griftWidth, controlPanelBounds.height);
            repairingCtrlBounds.set(controlPanelBounds.x + projWidth + griftWidth, controlPanelBounds.y, repairWidth, controlPanelBounds.height);

            // draw sub-regions -----------------------------------------------------------

            // draw projectile inventory
            game.assets.debugNinePatch.draw(batch, projectilesCtrlBounds.x, projectilesCtrlBounds.y, projectilesCtrlBounds.width, projectilesCtrlBounds.height);
            {
                float x = projectilesCtrlBounds.x;
                float y = projectilesCtrlBounds.y;
                float spacing = 7;

                // draw header text
                float sx = font.getScaleX();
                float sy = font.getScaleY();
                font.getData().setScale(0.4f);
                layout.setText(font, projectilesCtrlHeader, Color.WHITE, projectilesCtrlBounds.width, Align.center, false);
                y = projectilesCtrlBounds.y + projectilesCtrlBounds.height - layout.height;
                font.draw(batch, layout, x, y);
                font.getData().setScale(sx, sy);
                y -= layout.height + spacing;

                // draw current projectiles count
                sx = font.getScaleX();
                sy = font.getScaleY();
                font.getData().setScale(0.7f);
                layout.setText(font, Integer.toString(numProjectiles, 10), Color.SKY, projectilesCtrlBounds.width, Align.center, false);
                font.draw(batch, layout, x, y);
                font.getData().setScale(sx, sy);
                y -= layout.height + spacing;

                // draw '$ buy more projectiles' button
                float margin = 4;
                float width = projectilesCtrlBounds.width - 2 * margin;
                float height = 30;
                buyProjectilesButtonBounds.set(x + margin, projectilesCtrlBounds.y + margin, width, height);
                uiElements.drawButton(batch, buyProjectilesButtonBounds, canBuyProjectiles ? Color.WHITE : Color.DARK_GRAY, buyProjectilesButtonPressed);
                sx = font.getScaleX();
                sy = font.getScaleY();
                font.getData().setScale(0.5f);
                layout.setText(font, buyMoreProjectilesButtonText, Color.FOREST, projectilesCtrlBounds.width, Align.center, false);
                font.draw(batch, layout, x, projectilesCtrlBounds.y + margin + height / 2f + layout.height / 2f);
                font.getData().setScale(sx, sy);
            }

            // draw exploitation station
            game.assets.debugNinePatch.draw(batch, griftingCtrlBounds.x, griftingCtrlBounds.y, griftingCtrlBounds.width, griftingCtrlBounds.height);
            {
                float x = griftingCtrlBounds.x;
                float y = griftingCtrlBounds.y;
                float spacing = 7;
                float margin = 4;
                float buttonHeight = 30;
                float widgetWidth = griftingCtrlBounds.width - 2 * margin;

                // draw header text
                float sx = font.getScaleX();
                float sy = font.getScaleY();
                font.getData().setScale(0.45f);
                layout.setText(font, griftingCtrlHeader, Color.WHITE, griftingCtrlBounds.width, Align.center, false);
                y = griftingCtrlBounds.y + griftingCtrlBounds.height - layout.height + 8;
                font.draw(batch, layout, x, y);
                font.getData().setScale(sx, sy);
                y -= layout.height + spacing;

                // draw cash on hand & grift meter
                sx = font.getScaleX();
                sy = font.getScaleY();
                font.getData().setScale(0.7f);
                layout.setText(font, cashOnHandString, Color.LIME, griftingCtrlBounds.width, Align.center, false);
                {
                    // draw 'grift meter' quick, underneath cash on hand, while we have the text size
                    griftProgressBarBounds.set(griftingCtrlBounds.x + margin, griftingCtrlBounds.y + buttonHeight + 2 * margin + spacing, widgetWidth, buttonHeight - 2 * spacing);
                    uiElements.drawHorizontalProgressBar(batch, griftProgressBarBounds, Color.LIME, griftProgressPercent);

                    // draw a quick shadow behind the cash text to make it pop
                    float pad = 4;
                    float shadowWidth = griftingCtrlBounds.width * (1f / 2f);
                    float xx = griftingCtrlBounds.x + griftingCtrlBounds.width / 2f - shadowWidth / 2f;
                    float yy = griftingCtrlBounds.y + buttonHeight + 8;
                    float hh = layout.height + 3 * pad; //derp
                    batch.setColor(0.2f, 0.2f, 0.2f, 0.75f);
                    batch.draw(game.assets.pixelRegion, xx, yy, shadowWidth, hh);
                    batch.setColor(Color.SKY);
                    game.assets.debugNinePatch.draw(batch, xx, yy, shadowWidth, hh);
                    batch.setColor(Color.WHITE);
                }
                font.draw(batch, layout, x, y);
                font.getData().setScale(sx, sy);
                y -= layout.height + spacing;

                // draw '$ to grift harder' button
                buyMoreGriftingButtonBounds.set(x + margin, griftingCtrlBounds.y + margin, widgetWidth, buttonHeight);
                uiElements.drawButton(batch, buyMoreGriftingButtonBounds, canBuyMoreGrifting ? Color.WHITE : Color.DARK_GRAY, buyMoreGriftingButtonPressed);
                sx = font.getScaleX();
                sy = font.getScaleY();
                font.getData().setScale(0.5f);
                layout.setText(font, buyMoreGriftingButtonText, Color.FOREST, griftingCtrlBounds.width, Align.center, false);
                font.draw(batch, layout, x, griftingCtrlBounds.y + margin + buttonHeight / 2f + layout.height / 2f);
                font.getData().setScale(sx, sy);
            }

            // draw repairs subsection
            game.assets.debugNinePatch.draw(batch, repairingCtrlBounds.x, repairingCtrlBounds.y, repairingCtrlBounds.width, repairingCtrlBounds.height);
            {
                float x = repairingCtrlBounds.x;
                float y = repairingCtrlBounds.y;
                float spacing = 7;

                // draw header text
                float sx = font.getScaleX();
                float sy = font.getScaleY();
                font.getData().setScale(0.4f);
                layout.setText(font, repairingCtrlHeader, Color.WHITE, repairingCtrlBounds.width, Align.center, false);
                y = repairingCtrlBounds.y + repairingCtrlBounds.height - layout.height + 30;
                font.draw(batch, layout, x, y);
                font.getData().setScale(sx, sy);
                y -= layout.height + spacing;

                // draw '$ repair structure' button
                float margin = 4;
                float width = repairingCtrlBounds.width - 2 * margin;
                float height = 30;
                buyRepairingButtonBounds.set(x + margin, repairingCtrlBounds.y + margin, width, height);
                uiElements.drawButton(batch, buyRepairingButtonBounds, canBuyRepairing ? Color.WHITE : Color.DARK_GRAY, buyRepairingButtonPressed);
                sx = font.getScaleX();
                sy = font.getScaleY();
                font.getData().setScale(0.5f);
                layout.setText(font, buyStructureRepairingButtonText, Color.FOREST, repairingCtrlBounds.width, Align.center, false);
                font.draw(batch, layout, x, repairingCtrlBounds.y + margin + height / 2f + layout.height / 2f);
                font.getData().setScale(sx, sy);
            }

            batch.setColor(Color.WHITE);
        }

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

                Color acceptButtonColor = (canAcceptCiaBuyoffOffer && !respondedToComms) ? Color.WHITE : Color.DARK_GRAY;
                uiElements.drawButton(batch, commsLeftAcceptButton, acceptButtonColor, acceptButtonPressed);
                uiElements.drawButton(batch, commsLeftRejectButton, respondedToComms ? Color.DARK_GRAY : Color.WHITE, rejectButtonPressed);

                float textPressOffset = 2f;
                font.getData().setScale(0.5f);
                Color acceptButtonTextColor = (canAcceptCiaBuyoffOffer && !respondedToComms) ? Color.LIME : Color.LIGHT_GRAY;
                game.assets.layout.setText(font, "$ Pay Up $", acceptButtonTextColor, commsLeftAcceptButton.width, Align.center, false);
                font.draw(batch, game.assets.layout, commsLeftAcceptButton.x, commsLeftAcceptButton.y + commsLeftAcceptButton.height / 2f + game.assets.layout.height / 2f + 4 - (acceptButtonPressed ? textPressOffset : 0));
                game.assets.layout.setText(font, "Never!", respondedToComms ? Color.LIGHT_GRAY : Color.FIREBRICK, commsLeftRejectButton.width, Align.center, false);
                font.draw(batch, game.assets.layout, commsLeftRejectButton.x, commsLeftRejectButton.y + commsLeftRejectButton.height / 2f + game.assets.layout.height / 2f + 4 - (rejectButtonPressed ? textPressOffset : 0));
                font.getData().setScale(scaleX, scaleY);

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

        if (canBuyProjectiles && buyProjectilesButtonBounds.contains(x, y)) {
            buyProjectilesButtonPressed = true;
            return true;
        }
        if (canBuyMoreGrifting && buyMoreGriftingButtonBounds.contains(x, y)) {
            buyMoreGriftingButtonPressed = true;
            return true;
        }
        if (canBuyRepairing && buyRepairingButtonBounds.contains(x, y)) {
            buyRepairingButtonPressed = true;
            return true;
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
            if (canAcceptCiaBuyoffOffer && commsLeftAcceptButton.contains(x, y)) {
                respondedToComms = true;
                commsText = commsTextAccepted;
                if (game.getScreen() instanceof GameScreen) {
                    GameScreen currentScreen = (GameScreen) game.getScreen();
                    currentScreen.world.makeBananasHappy();
                    addToCash(PurchasePrice.ciaBuyoff);
                }
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
        {
            buyProjectilesButtonPressed = false;
            buyMoreGriftingButtonPressed = false;
            buyRepairingButtonPressed = false;

            if (canBuyProjectiles && buyProjectilesButtonBounds.contains(x, y)) {
                game.audio.playSound(Audio.Sounds.chaching, 0.5f);
                addToCash(-PurchasePrice.projectiles);
                numProjectiles += projectilesPerPurchase;
                return true;
            }
            if (canBuyMoreGrifting && buyMoreGriftingButtonBounds.contains(x, y)) {
                game.audio.playSound(Audio.Sounds.chaching, 0.5f);
                addToCash(-PurchasePrice.grift);
                griftSpeed += griftSpeedIncrement;
                return true;
            }
            if (canBuyRepairing && buyRepairingButtonBounds.contains(x, y)) {
                game.audio.playSound(Audio.Sounds.chaching, 0.5f);
                addToCash(-PurchasePrice.repairs);
                BaseScreen gameScreen = game.getScreen();
                if (gameScreen instanceof GameScreen) {
                    // TODO: play mechanical sound?
                    ((GameScreen) gameScreen).world.reactor.repairStructure();
                }
                return true;
            }
        }
        return super.touchUp(screenX, screenY, pointer, button);
    }

    private void rejectComms() {
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
