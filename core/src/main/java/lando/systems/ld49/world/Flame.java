package lando.systems.ld49.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld49.Main;

public class Flame {
    private ShaderProgram shader;
    public Color insideColor = new Color();
    public Color outsideColor = new Color();
    private float accum;
    public Rectangle bounds;

    public Flame(Rectangle bounds, Color insideColor, Color outsideColor) {
        this(bounds.x, bounds.y, bounds.width, bounds.height, insideColor, outsideColor);
    }

    public Flame(float x, float y, float width, float height, Color insideColor, Color outsideColor) {
        this.bounds = new Rectangle(x, y, width, height);
        this.insideColor.set(insideColor);
        this.outsideColor.set(outsideColor);
        this.shader = Main.game.assets.flameShader;
        accum = MathUtils.random(100f);
    }

    public void update(float dt) {
        accum += dt;
    }

    public void render(SpriteBatch batch) {
        boolean isDrawing = batch.isDrawing();
        if (isDrawing) batch.end();
        batch.setShader(shader);
        batch.begin();
        shader.setUniformf("u_time", accum);
        shader.setUniformf("u_color1", outsideColor);
        shader.setUniformf("u_color2", insideColor);
        batch.draw(Main.game.assets.noise, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.end();
        batch.setShader(null);
        if (isDrawing) batch.begin();
    }


}
