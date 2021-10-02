package lando.systems.ld49.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld49.Assets;

public class InputPrompts {

    private final TextureRegion sheet;
    private final TextureRegion[][] regions;

    public InputPrompts(Assets assets) {
        this.sheet = assets.atlas.findRegion("input-prompts");
        this.regions = sheet.split(16, 16);
    }

    public TextureRegion get(Type type) {
        if (type.x < 0 || type.x >= regions[0].length
         || type.y < 0 || type.y >= regions.length) {
            throw new GdxRuntimeException("Can't get input prompt for type '" + type + "', invalid tilesheet coordinates: (" + type.x + ", " + type.y + ")");
        }
        return regions[type.y][type.x];
    }

    public enum Type {
        // letters
          key_light_a(18, 3)
        , key_light_b(23, 4)
        // ... add the rest as needed or when bored
        // numbers
        // ...
        // spacebar
        , key_light_spacebar_1(31, 6)
        , key_light_spacebar_2(32, 6)
        , key_light_spacebar_3(33, 6)
        // arrows
        , key_light_arrow_up(30, 4)
        , key_light_arrow_right(31, 4)
        , key_light_arrow_down(32, 4)
        , key_light_arrow_left(33, 4)
        ;

        public final int x;
        public final int y;
        Type(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

}
