package lando.systems.ld49.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld49.Assets;
import lando.systems.ld49.Main;

public class Reactor {

    private static final boolean DRAW_SEGMENTS = false;

    public enum DamageAmount {
        small(0.01f), medium(1), large(20);
        final float value;
        DamageAmount(float value) {this.value = value;}
    }

    public Array<Segment2D> segments = new Array<>();
    public Array<Pin> pins = new Array<>();
    public Array<Piston> pistons = new Array<>();

    private final TextureRegion shellTexture;
    private final TextureRegion backTexture;
    private final Animation<TextureRegion> glowAnim;
    private float glowAnimTime = 0;

    private float currStructureDmg = 0;
    private final float maxStructureDmg = 100;

    private final Flame greenFlame;

    private final World world;
    public final float left = 450;
    private final float xScale = 1.5f;
    private final float yScale = 1.5f;

    public Reactor(World world) {
        this.world = world;
        Assets assets = Main.game.assets;;
        shellTexture = assets.atlas.findRegion("tower/frontwall/tower-frontwall");
        backTexture = assets.atlas.findRegion("tower/backwall/tower-backwall");
        glowAnim = new Animation<>(0.05f, assets.atlas.findRegions("tower/glow/tower-glow"), Animation.PlayMode.LOOP);

        greenFlame = new Flame(left + 115 * xScale, 0, 250*xScale, 180 * yScale, new Color(.2f, .8f, .3f, 1.0f), new Color(1f, .3f, 1f, 1.0f));
        // height of image, since pixels are top left origin the coords we got from the image are y-flipped, need to re-orient to y-up
        float flipy = 360 * yScale;

        // exterior wall, left side
        segments.add(new Segment2D(this, true, left +   8 * xScale, flipy - 360 * yScale, left +   8 * xScale, flipy - 330 * yScale));
        segments.add(new Segment2D(this, true, left +   8 * xScale, flipy - 330 * yScale, left +  60 * xScale, flipy - 280 * yScale));
        segments.add(new Segment2D(this, true, left +  60 * xScale, flipy - 280 * yScale, left + 105 * xScale, flipy - 220 * yScale));
        segments.add(new Segment2D(this, true, left + 105 * xScale, flipy - 220 * yScale, left + 125 * xScale, flipy - 160 * yScale));
        segments.add(new Segment2D(this, true, left + 125 * xScale, flipy - 160 * yScale, left + 125 * xScale, flipy - 100 * yScale));
        segments.add(new Segment2D(this, true, left + 125 * xScale, flipy - 100 * yScale, left + 115 * xScale, flipy -  70 * yScale));
        segments.add(new Segment2D(this, true, left + 115 * xScale, flipy -  70 * yScale, left + 105 * xScale, flipy -  40 * yScale));
        segments.add(new Segment2D(this, true, left + 105 * xScale, flipy -  40 * yScale, left + 130 * xScale, flipy -  30 * yScale));
        segments.add(new Segment2D(this, true, left + 130 * xScale, flipy -  30 * yScale, left + 130 * xScale, flipy -  40 * yScale));

        // interior wall, left side
        segments.add(new Segment2D(this, false, left + 130 * xScale, flipy -  30 * yScale, left + 125 * xScale, flipy -  65 * yScale));
        segments.add(new Segment2D(this, false, left + 125 * xScale, flipy -  65 * yScale, left + 135 * xScale, flipy - 100 * yScale));
        segments.add(new Segment2D(this, false, left + 135 * xScale, flipy - 100 * yScale, left + 155 * xScale, flipy - 130 * yScale));
        segments.add(new Segment2D(this, false, left + 155 * xScale, flipy - 130 * yScale, left + 170 * xScale, flipy - 160 * yScale));
        segments.add(new Segment2D(this, false, left + 170 * xScale, flipy - 160 * yScale, left + 165 * xScale, flipy - 200 * yScale));
        segments.add(new Segment2D(this, false, left + 165 * xScale, flipy - 200 * yScale, left + 150 * xScale, flipy - 235 * yScale));
        segments.add(new Segment2D(this, false, left + 150 * xScale, flipy - 235 * yScale, left + 135 * xScale, flipy - 270 * yScale));
        segments.add(new Segment2D(this, false, left + 135 * xScale, flipy - 270 * yScale, left + 125 * xScale, flipy - 300 * yScale));
        segments.add(new Segment2D(this, false, left + 125 * xScale, flipy - 300 * yScale, left + 120 * xScale, flipy - 330 * yScale));
        segments.add(new Segment2D(this, false, left + 120 * xScale, flipy - 330 * yScale, left + 140 * xScale, flipy - 335 * yScale));
        segments.add(new Segment2D(this, false, left + 140 * xScale, flipy - 335 * yScale, left + 165 * xScale, flipy - 360 * yScale));
        segments.add(new Segment2D(this, false, left + 165 * xScale, flipy - 360 * yScale, left + 165 * xScale, flipy - 360 * yScale));

        // floor
        segments.add(new Segment2D(this, false, left + 165 * xScale, flipy - 360 * yScale, left + 305 * xScale, flipy - 360 * yScale));

        // interior wall, right side
        segments.add(new Segment2D(this, false, left + 305 * xScale, flipy - 360 * yScale, left + 340 * xScale, flipy - 335 * yScale));
        segments.add(new Segment2D(this, false, left + 340 * xScale, flipy - 335 * yScale, left + 360 * xScale, flipy - 325 * yScale));
        segments.add(new Segment2D(this, false, left + 360 * xScale, flipy - 325 * yScale, left + 360 * xScale, flipy - 300 * yScale));
        segments.add(new Segment2D(this, false, left + 360 * xScale, flipy - 300 * yScale, left + 340 * xScale, flipy - 265 * yScale));
        segments.add(new Segment2D(this, false, left + 340 * xScale, flipy - 265 * yScale, left + 315 * xScale, flipy - 200 * yScale));
        segments.add(new Segment2D(this, false, left + 315 * xScale, flipy - 200 * yScale, left + 315 * xScale, flipy - 155 * yScale));
        segments.add(new Segment2D(this, false, left + 315 * xScale, flipy - 155 * yScale, left + 335 * xScale, flipy - 115 * yScale));
        segments.add(new Segment2D(this, false, left + 335 * xScale, flipy - 115 * yScale, left + 350 * xScale, flipy -  80 * yScale));
        segments.add(new Segment2D(this, false, left + 350 * xScale, flipy -  80 * yScale, left + 365 * xScale, flipy -  50 * yScale));
        segments.add(new Segment2D(this, false, left + 365 * xScale, flipy -  50 * yScale, left + 360 * xScale, flipy -  30 * yScale));
        segments.add(new Segment2D(this, false, left + 360 * xScale, flipy -  30 * yScale, left + 360 * xScale, flipy -  30 * yScale));

        // exterior wall, right side
        segments.add(new Segment2D(this, true, left + 360 * xScale, flipy -  30 * yScale, left + 370 * xScale, flipy -  45 * yScale));
        segments.add(new Segment2D(this, true, left + 370 * xScale, flipy -  45 * yScale, left + 360 * xScale, flipy -  75 * yScale));
        segments.add(new Segment2D(this, true, left + 360 * xScale, flipy -  75 * yScale, left + 360 * xScale, flipy - 100 * yScale));
        segments.add(new Segment2D(this, true, left + 360 * xScale, flipy - 100 * yScale, left + 360 * xScale, flipy - 160 * yScale));
        segments.add(new Segment2D(this, true, left + 360 * xScale, flipy - 160 * yScale, left + 370 * xScale, flipy - 200 * yScale));
        segments.add(new Segment2D(this, true, left + 370 * xScale, flipy - 200 * yScale, left + 395 * xScale, flipy - 250 * yScale));
        segments.add(new Segment2D(this, true, left + 395 * xScale, flipy - 250 * yScale, left + 440 * xScale, flipy - 300 * yScale));
        segments.add(new Segment2D(this, true, left + 440 * xScale, flipy - 300 * yScale, left + 470 * xScale, flipy - 330 * yScale));
        segments.add(new Segment2D(this, true, left + 470 * xScale, flipy - 330 * yScale, left + 470 * xScale, flipy - 360 * yScale));
        segments.add(new Segment2D(this, true, left + 470 * xScale, flipy - 360 * yScale, left + 500 * xScale, flipy - 360 * yScale));

        int pistonCount = 8;
        float pistonStart = left + 125 * xScale;
        float pistonStop = left + 360 * xScale;
        float pistonAreaWidth = pistonStop - pistonStart;
        float pistondx = pistonAreaWidth/pistonCount;
        for (int i = 0; i < pistonCount; i++){
            pistons.add(new Piston(pistonStart + i * pistondx, flipy - 360 * yScale, pistondx, 50 * yScale));
        }

        for (int i = 0; i < 5 ; i++){
            float dx = 200 / 4f;
            float pinLeft = 215;

            // it's too tight, skip the ones at the edge
            if (i > 0 && i < 4) {
                pins.add(new Pin(left + pinLeft + dx + dx * i, 350, Pin.Type.steel));
                pins.add(new Pin(left + pinLeft + dx + dx * i, 200, Pin.Type.steel));
            }

            if (i > 0 && i < 3){
                pins.add(new Pin(left + pinLeft + dx + dx/2f + i*dx, 275, Pin.Type.bumper));
            }
        }
    }

    public float getStructurePercent() {
        return currStructureDmg / maxStructureDmg;
    }

    public float getTemperaturePercent() {
        float total = 0;
        for (Piston p : pistons) {
            total += p.getPercentHeat();
        }
        return total/pistons.size;    }

    public void damageStructure(DamageAmount amount) {
        currStructureDmg += amount.value;
        // trigger a flash on the structural integrity meter for serious damage
        if (amount != DamageAmount.small) {
            world.gameScreen.ui.structureDamaged();
        }
        if (currStructureDmg >= maxStructureDmg) {
            currStructureDmg = maxStructureDmg;
            // TODO: trigger end game
            Gdx.app.log("END STATE", "Your tower broke, that's game over man, game over!");
        }
    }

    public void update(float dt) {
        glowAnimTime += dt;
        greenFlame.update(dt*.5f);
        for (Pin p : pins) {
            p.update(dt);
        }
        for (Piston p : pistons) {
            p.update(dt);
        }
    }

    public void render(SpriteBatch batch) {
//        TextureRegion tex = backTexture;
        // try just grabbing a single frame of the glow animation, might look better than the plain background
        TextureRegion tex = glowAnim.getKeyFrames()[3];
        batch.draw(tex, left, 0, xScale * tex.getRegionWidth(), yScale * tex.getRegionHeight());

        // the animation doesn't look quite right, sad trombone
//        tex = glowAnim.getKeyFrame(glowAnimTime);
//        batch.draw(tex, left, 0, xScale * tex.getRegionWidth(), yScale * tex.getRegionHeight());

        greenFlame.render(batch);

        for (Piston p : pistons) {
            p.render(batch);
        }
        tex = shellTexture;
        batch.draw(tex, left, 0, xScale * tex.getRegionWidth(), yScale * tex.getRegionHeight());

        for (Pin p : pins) {
            p.render(batch);
        }
    }

    public void renderDebug(SpriteBatch batch) {
        if (!DRAW_SEGMENTS) return;
        for (Segment2D segment : segments) {
            segment.debugRender(batch);
        }
    }
}
