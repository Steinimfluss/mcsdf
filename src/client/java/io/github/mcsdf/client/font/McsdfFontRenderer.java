package io.github.mcsdf.client.font;

import io.github.mcsdf.client.render.McsdfPipelines;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class McsdfFontRenderer {
    public void renderStringScaled(GuiGraphicsExtractor graphics, McsdfFont font, String text, float x, float y, float scalePercent, int color) {
        float s = scalePercent / 100f;

        graphics.pose().pushMatrix();

        graphics.pose().translate(x, y);

        graphics.pose().scale(s, s);

        renderString(graphics, font, text, 0, 0, color);

        graphics.pose().popMatrix();
    }

    public void drawCenteredStringScaled(GuiGraphicsExtractor graphics, McsdfFont font, String text, float x, float y, float scalePercent, int color) {
        float s = scalePercent / 100f;

        float width  = getWidth(font, text, scalePercent);
        float height = getHeight(font, text, scalePercent);

        graphics.pose().pushMatrix();

        graphics.pose().translate(x, y);
        graphics.pose().scale(s, s);

        float drawX     = -width  / 2f;
        float baselineY = -height / 2f;

        renderString(graphics, font, text, drawX, baselineY, color);

        graphics.pose().popMatrix();
    }
    
    public void renderChar(GuiGraphicsExtractor graphics, McsdfFont font, char c, float x, float y, int color) {
        var glyph = font.getGlyph(c);
        if (glyph == null || glyph.atlasBounds == null || glyph.planeBounds == null) {
            return;
        }

        float scale = (float) font.metadata.atlas.size;

        float drawX = (float) (x + glyph.planeBounds.left * scale);
        float drawY = (float) (y - glyph.planeBounds.top  * scale);

        float quadWidth  = (float) ((glyph.planeBounds.right  - glyph.planeBounds.left)   * scale);
        float quadHeight = (float) ((glyph.planeBounds.top    - glyph.planeBounds.bottom) * scale);

        int u0 = (int) glyph.atlasBounds.left;
        int v0 = (int) glyph.atlasBounds.top;

        graphics.blit(
            McsdfPipelines.MSDF_GUI_TEXTURED,
            font.texture,
            (int) drawX,
            (int) drawY,
            u0,
            font.metadata.atlas.height - v0,
            (int) quadWidth,
            (int) quadHeight,
            font.metadata.atlas.width,
            font.metadata.atlas.height,
            color
        );
    }

    public void renderString(GuiGraphicsExtractor graphics, McsdfFont font, String text, float x, float y, int color) {
        float cursorX = x;
        float scale   = (float) font.metadata.atlas.size;

        float baselineY = y + getHeight(font, text);

        int prev = -1;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            var glyph = font.getGlyph(c);
            if (glyph == null) continue;

            if (prev != -1) {
                cursorX += (float) (font.getKerning(prev, c) * scale);
            }

            renderChar(graphics, font, c, cursorX, baselineY, color);

            cursorX += (float) (glyph.advance * scale);
            prev = c;
        }
    }

    public float getWidth(McsdfFont font, String text) {
        float scale = (float) font.metadata.atlas.size;
        float width = 0f;

        int prev = -1;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            var glyph = font.getGlyph(c);
            if (glyph == null) continue;

            if (prev != -1) {
                width += (float) (font.getKerning(prev, c) * scale);
            }

            width += (float) (glyph.advance * scale);
            prev = c;
        }

        return width;
    }

    public float getHeight(McsdfFont font, String text) {
        float scale = (float) font.metadata.atlas.size;

        float asc  = (float) (font.metadata.metrics.ascender  * scale);
        float desc = (float) (-font.metadata.metrics.descender * scale);

        return asc + desc;
    }

    public float getWidth(McsdfFont font, String text, float scalePercent) {
        float base = getWidth(font, text);
        return base * (scalePercent / 100f);
    }

    public float getHeight(McsdfFont font, String text, float scalePercent) {
        float base = getHeight(font, text);
        return base * (scalePercent / 100f);
    }

    public void drawCenteredString(GuiGraphicsExtractor graphics, McsdfFont font, String text, float x, float y, int color) {
        float width  = getWidth(font, text);
        float height = getHeight(font, text);

        float drawX     = (float) (x - width  / 2f);
        float baselineY = (float) (y - height / 2f);

        renderString(graphics, font, text, drawX, baselineY, color);
    }
}
