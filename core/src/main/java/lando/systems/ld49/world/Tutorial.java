package lando.systems.ld49.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld49.Config;
import lando.systems.ld49.Main;
import lando.systems.ld49.screens.GameScreen;

public class Tutorial {
    private static float maxAlpha = 1f;

    OrthographicCamera camera;
    GameScreen screen;
    Array<TutorialItem> activeItems = new Array<>();
    TutorialItem currentTutorialItem;
    boolean initialized = false;
    boolean addedCIA = false;
    float accum = 0;
    float targetAlpha;
    float alpha;
    boolean cancelTutorial = false;
    Rectangle cancelTutorialButton;
    Vector3 mousePos = new Vector3();

    public Tutorial ( GameScreen screen ) {
        this.screen = screen;
        this.camera = screen.windowCamera;
        targetAlpha = 0;
        alpha = 0;
        cancelTutorialButton = new Rectangle(camera.viewportWidth- 180, camera.viewportHeight - 60, 170, 50);
    }

    public void update(float dt) {
        if (!Config.show_tutorial || cancelTutorial) {
            currentTutorialItem = null;
            activeItems.clear();
        }
        accum += dt;
        if (!initialized && accum > 3) {
            initialize();
        }
        if (currentTutorialItem == null && activeItems.size > 0){
            currentTutorialItem = activeItems.get(0);
            targetAlpha = maxAlpha;
        }
        if (currentTutorialItem != null) {
            if (targetAlpha > alpha) {
                alpha = MathUtils.clamp(alpha + 1.5f * dt, 0, maxAlpha);
            } else if (targetAlpha < alpha) {
                alpha = MathUtils.clamp(alpha - 3f * dt, 0, maxAlpha);
            }
            if (alpha == maxAlpha) {
                // Ready to draw
                currentTutorialItem.typingLabel.update(dt);
                if (Gdx.input.justTouched()){
                    camera.unproject(mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0));

                    if (cancelTutorialButton.contains(mousePos.x, mousePos.y)){
                        cancelTutorial = true;
                    }
                    else if (!currentTutorialItem.typingLabel.hasEnded()){
                        currentTutorialItem.typingLabel.skipToTheEnd();
                    } else {
                        targetAlpha = 0;
                    }
                }
            }
            if (targetAlpha == 0 && alpha == 0){
                currentTutorialItem = null;
                activeItems.removeIndex(0);
            }
        }
    }

    public void initialize(){
        initialized = true;
        activeItems.add(new TutorialItem(Main.game.assets.strings.get("introText"), new Rectangle(100000, 100000, 0, 0), new Rectangle(200, 300, 400, 400)));
        activeItems.add(new TutorialItem(Main.game.assets.strings.get("reactorText"), new Rectangle(459, 98, 687, 483), new Rectangle(50, 200, 400, 500)));
        activeItems.add(new TutorialItem(Main.game.assets.strings.get("heatText"), new Rectangle(0, 0, 225, 136), new Rectangle(200, 400, 800, 300)));
        activeItems.add(new TutorialItem(Main.game.assets.strings.get("structureText"), new Rectangle(1059, 0, 225, 136), new Rectangle(200, 400, 800, 300)));
        activeItems.add(new TutorialItem(Main.game.assets.strings.get("pistonText"), new Rectangle(636, 100, 350, 50), new Rectangle(200, 300, 800, 400)));
        activeItems.add(new TutorialItem(Main.game.assets.strings.get("slingText"), new Rectangle(215, 184, 130, 70), new Rectangle(200, 350, 800, 350)));
        activeItems.add(new TutorialItem(Main.game.assets.strings.get("ammoText"), new Rectangle(225, 0, 210, 93), new Rectangle(200, 400, 800, 300)));
        activeItems.add(new TutorialItem(Main.game.assets.strings.get("griftText"), new Rectangle(435, 0, 415, 93), new Rectangle(200, 400, 800, 300)));
        activeItems.add(new TutorialItem(Main.game.assets.strings.get("repairText"), new Rectangle(850, 0, 210, 93), new Rectangle(200, 400, 800, 300)));
    }

    public void render(SpriteBatch batch) {
        if (currentTutorialItem != null){

            TutorialItem item = currentTutorialItem;
            TextureRegion pixel = Main.game.assets.pixelRegion;

            batch.setColor(.1f, .1f, .1f, alpha*.9f);
            // Draw shaded area outlining the area of interest
            batch.draw(pixel, 0, 0, item.bounds.x, item.bounds.y);
            batch.draw(pixel, 0, item.bounds.y, item.bounds.x, item.bounds.height);
            batch.draw(pixel, 0, item.bounds.y + item.bounds.height, item.bounds.x, camera.viewportHeight - (item.bounds.y +item.bounds.height));

            batch.draw(pixel, item.bounds.x, 0, item.bounds.width, item.bounds.y);
            batch.draw(pixel, item.bounds.x, item.bounds.y + item.bounds.height, item.bounds.width, camera.viewportHeight - (item.bounds.y +item.bounds.height));

            batch.draw(pixel, item.bounds.x + item.bounds.width, 0, camera.viewportWidth, item.bounds.y);
            batch.draw(pixel, item.bounds.x + item.bounds.width, item.bounds.y, camera.viewportWidth, item.bounds.height);
            batch.draw(pixel, item.bounds.x + item.bounds.width, item.bounds.y + item.bounds.height, camera.viewportWidth, camera.viewportHeight);

            batch.setColor(1,1,0, Math.abs(MathUtils.sin(accum * 3f) * alpha));
            Main.game.assets.debugNinePatch.draw(batch, item.bounds.x, item.bounds.y, item.bounds.width, item.bounds.height);

            batch.setColor(Color.WHITE);
            // Draw the text
            batch.setColor(0, .5f, .5f, alpha * alpha);
            Main.game.assets.panelNinePatch.draw(batch, item.textBounds.x - 5, item.textBounds.y -5, item.textBounds.width + 10, item.textBounds.height + 10);
            batch.setColor(Color.WHITE);
            item.typingLabel.render(batch, alpha);
            if (item.typingLabel.hasEnded()){
                Main.game.assets.pixelFont16.getData().setScale(.5f);
                Main.game.assets.layout.setText(Main.game.assets.pixelFont16, "Click to Continue", Color.WHITE, 200, Align.left, false);
                Main.game.assets.pixelFont16.setColor(1, 1, 1, alpha);
                Main.game.assets.pixelFont16.draw(batch, "Click to Continue", item.textBounds.x + item.textBounds.width - Main.game.assets.layout.width, item.textBounds.y + 5 + Main.game.assets.layout.height);
                Main.game.assets.pixelFont16.getData().setScale(1f);
            }

            // Cancel Button
            batch.setColor(1f, .6f, .6f, 1.0f);
            screen.assets.panelNinePatch.draw(batch, cancelTutorialButton.x, cancelTutorialButton.y, cancelTutorialButton.width, cancelTutorialButton.height);
            screen.assets.pixelFont16.getData().setScale(.4f);
            screen.assets.layout.setText(screen.assets.pixelFont16, "Cancel Tutorial", Color.BLACK, cancelTutorialButton.width, Align.center, false);
            screen.assets.pixelFont16.setColor(0,0,0, alpha);
            screen.assets.pixelFont16.draw(batch, "Cancel Tutorial", cancelTutorialButton.x+1, -1 + cancelTutorialButton.y + cancelTutorialButton.height/2f + screen.assets.layout.height/2, cancelTutorialButton.width, Align.center, false);
            screen.assets.pixelFont16.setColor(1,1f,1f, alpha);
            screen.assets.pixelFont16.draw(batch, "Cancel Tutorial", cancelTutorialButton.x, cancelTutorialButton.y + cancelTutorialButton.height/2f + screen.assets.layout.height/2,  cancelTutorialButton.width, Align.center, false);

            screen.assets.pixelFont16.getData().setScale(1);

            batch.setColor(Color.WHITE);
            Main.game.assets.pixelFont16.setColor(Color.WHITE);


        }

    }

    public boolean isActive() {
        return activeItems.size > 0;
    }

    public void addCIA() {
        if (addedCIA) return;
        addedCIA = true;
        int panelHeight = 128;
        int buttonHeight = 40;
        activeItems.add(new TutorialItem(Main.game.assets.strings.get("ciaText"), new Rectangle(0, Config.window_height - panelHeight - buttonHeight, Config.window_width / 2f, panelHeight + buttonHeight), new Rectangle(200, 100, 800, 300)));

    }
}
