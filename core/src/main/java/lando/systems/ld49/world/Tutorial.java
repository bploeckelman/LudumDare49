package lando.systems.ld49.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld49.Config;
import lando.systems.ld49.Main;

public class Tutorial {
    private static float maxAlpha = 1f;

    Array<TutorialItem> activeItems = new Array<>();
    TutorialItem currentTutorialItem;
    boolean initialized = false;
    boolean addedCIA = false;
    float accum = 0;
    float targetAlpha;
    float alpha;

    public Tutorial ( ) {
        targetAlpha = 0;
        alpha = 0;
    }

    public void update(float dt) {
        if (!Config.show_tutorial) {
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
                alpha = MathUtils.clamp(alpha + dt, 0, maxAlpha);
            } else if (targetAlpha < alpha) {
                alpha = MathUtils.clamp(alpha - dt, 0, maxAlpha);
            }
            if (alpha == maxAlpha) {
                // Ready to draw
                currentTutorialItem.typingLabel.update(dt);
                if (Gdx.input.justTouched()){
                    if (!currentTutorialItem.typingLabel.hasEnded()){
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
        activeItems.add(new TutorialItem(Main.game.assets.strings.get("introText"), new Rectangle(0, 0, 225, 136), new Rectangle(200, 400, 800, 300)));
        activeItems.add(new TutorialItem(Main.game.assets.strings.get("structureText"), new Rectangle(1059, 0, 225, 136), new Rectangle(200, 400, 800, 300)));


    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        if (currentTutorialItem != null){
            TutorialItem item = currentTutorialItem;
            TextureRegion pixel = Main.game.assets.pixelRegion;

            batch.setColor(.2f, .2f, .2f, alpha*.9f);
            // Draw shaded area outlining the area of interest
            batch.draw(pixel, 0, 0, item.bounds.x, item.bounds.y);
            batch.draw(pixel, 0, item.bounds.y, item.bounds.x, item.bounds.height);
            batch.draw(pixel, 0, item.bounds.y + item.bounds.height, item.bounds.x, camera.viewportHeight - (item.bounds.y +item.bounds.height));

            batch.draw(pixel, item.bounds.x, 0, item.bounds.width, item.bounds.y);
            batch.draw(pixel, item.bounds.x, item.bounds.y + item.bounds.height, item.bounds.width, camera.viewportHeight - (item.bounds.y +item.bounds.height));

            batch.draw(pixel, item.bounds.x + item.bounds.width, 0, camera.viewportWidth, item.bounds.y);
            batch.draw(pixel, item.bounds.x + item.bounds.width, item.bounds.y, camera.viewportWidth, item.bounds.height);
            batch.draw(pixel, item.bounds.x + item.bounds.width, item.bounds.y + item.bounds.height, camera.viewportWidth, camera.viewportHeight);

            batch.setColor(Color.WHITE);
            // Draw the text
            batch.setColor(0, .5f, .5f, alpha * alpha);
            Main.game.assets.panelNinePatch.draw(batch, item.textBounds.x - 5, item.textBounds.y -5, item.textBounds.width + 10, item.textBounds.height + 10);
            batch.setColor(Color.WHITE);
            item.typingLabel.render(batch, alpha);
            if (item.typingLabel.hasEnded()){
                Main.game.assets.layout.setText(Main.game.assets.pixelFont16, "Click to Continue", Color.WHITE, 200, Align.left, false);
                Main.game.assets.pixelFont16.setColor(1, 1, 1, alpha);
                Main.game.assets.pixelFont16.draw(batch, "Click to Continue", item.textBounds.x + item.textBounds.width - Main.game.assets.layout.width, item.textBounds.y + 5 + Main.game.assets.layout.height);
            }
        }

    }

    public boolean isActive() {
        return activeItems.size > 0;
    }

    public void addCIA() {
        if (addedCIA) return;
        activeItems.add(new TutorialItem(Main.game.assets.strings.get("ciaText"), new Rectangle(0, 464, 512, 255), new Rectangle(200, 100, 800, 300)));

    }
}