package lando.systems.ld49.world;

import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld49.Main;
import lando.systems.ld49.utils.typinglabel.TypingLabel;

public class TutorialItem {
    public String text;
    public Rectangle bounds;
    public Rectangle textBounds;
    public TypingLabel typingLabel;

    public TutorialItem(String text, Rectangle bounds, Rectangle textBounds) {
        this.text = text;
        this.bounds = bounds;
        this.textBounds = textBounds;
        typingLabel = new TypingLabel(Main.game.assets.pixelFont16, text, textBounds.x, textBounds.y + textBounds.height- 10);
        typingLabel.setWidth(textBounds.width);
        typingLabel.setFontScale(.8f);

    }
}
