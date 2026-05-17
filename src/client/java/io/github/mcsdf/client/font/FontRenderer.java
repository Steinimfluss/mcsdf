package io.github.mcsdf.client.font;

import io.github.mcsdf.client.render.McsdfPipelines;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class FontRenderer {
	private Font font;
	private float size;
	
    public FontRenderer(Font font, int size) {
		this.font = font;
		this.size = size;
	}

	private void rawText(GuiGraphicsExtractor graphics, String text, float x, float y, int color) {
        float cursorX = x;
        float scale   = (float) font.metadata.atlas.size;

        float baselineY = y;

        int prev = -1;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            var glyph = font.getGlyph(c);
            if (glyph == null) continue;

            if (prev != -1) {
                cursorX += (float) (font.getKerning(prev, c) * scale);
            }

            character(graphics, c, (int) cursorX, (int) baselineY, color);

            cursorX += (float) (glyph.advance * scale);
            prev = c;
        }
    }
	
	public void text(GuiGraphicsExtractor graphics, String text, float x, float y, int color) {
        float s = size / 100f;
        y += getBaseline();

        graphics.pose().pushMatrix();

        graphics.pose().translate(x, y);

        graphics.pose().scale(s, s);
        
        rawText(graphics, text, 0, 0, color);
        
        graphics.pose().popMatrix();
	}

	public void text(GuiGraphicsExtractor graphics, Component component, float x, float y, int color) {
		text(graphics, component.getString(), x, y, color);
	}

	public void centeredTextWH(GuiGraphicsExtractor graphics, Component component, float x, float y, int color) {
	    centeredTextWH(graphics, component.getString(), x, y, color);
	}

	public void centeredTextWH(GuiGraphicsExtractor graphics, String text, float x, float y, int color) {
	    text(graphics, text, x - getWidth(text), y - getHeight(), color);
	}

	public void centeredTextW(GuiGraphicsExtractor graphics, Component component, float x, float y, int color) {
	    centeredTextW(graphics, component.getString(), x, y, color);
	}

	public void centeredTextW(GuiGraphicsExtractor graphics, String text, float x, float y, int color) {
	    text(graphics, text, x - getWidth(text), y, color);
	}

	public void centeredTextH(GuiGraphicsExtractor graphics, Component component, float x, float y, int color) {
	    centeredTextH(graphics, component.getString(), x, y, color);
	}

	public void centeredTextH(GuiGraphicsExtractor graphics, String text, float x, float y, int color) {
	    text(graphics, text, x, y - getHeight(), color);
	}

	public void character(GuiGraphicsExtractor graphics, char c, float x, float y, int color) {
	    var glyph = font.getGlyph(c);
	    if (glyph == null || glyph.atlasBounds == null || glyph.planeBounds == null) {
	        return;
	    }

	    float scale = (float) font.metadata.atlas.size;

	    double drawXd = x + glyph.planeBounds.left * scale;
	    double drawYd = y - glyph.planeBounds.top  * scale;

	    float quadWidth  = (float) ((glyph.planeBounds.right  - glyph.planeBounds.left)   * scale);
	    float quadHeight = (float) ((glyph.planeBounds.top    - glyph.planeBounds.bottom) * scale);

	    int drawX = Math.round((float) drawXd);
	    int drawY = Math.round((float) drawYd);

	    float u0 = (float) glyph.atlasBounds.left;
	    float v0 = (float) glyph.atlasBounds.top;

	    graphics.blit(
	        McsdfPipelines.MSDF_GUI_TEXTURED,
	        font.texture,
	        drawX,
	        drawY,
	        u0,
	        font.metadata.atlas.height - v0,
	        (int) quadWidth,
	        (int) quadHeight,
	        font.metadata.atlas.width,
	        font.metadata.atlas.height,
	        color
	    );
	}

    public float getWidth(String text) {
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

    public float getHeight() {
        float s = size / 100f;
        
        float scale = (float) font.metadata.atlas.size;
        float asc  = (float) font.metadata.metrics.ascender * scale;
        float desc = (float) font.metadata.metrics.descender * scale;
        return (asc + desc) * s;
    }

    public float getBaseline() {
        float s = size / 100f;
        float scale = (float) font.metadata.atlas.size;
        float asc = (float) (font.metadata.metrics.ascender * scale);
        return asc * s;
    }
}