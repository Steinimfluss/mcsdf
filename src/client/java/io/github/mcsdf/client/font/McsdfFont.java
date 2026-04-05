package io.github.mcsdf.client.font;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;

import io.github.mcsdf.client.font.metadata.MsdfFontMetadata;
import io.github.mcsdf.client.font.metadata.MsdfGlyph;
import io.github.mcsdf.client.mixin.AbstractTextureAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public class McsdfFont {
	private static final Minecraft mc = Minecraft.getInstance();
	private static final Gson gson = new Gson();
	
    public final Identifier texture;
    public final MsdfFontMetadata metadata;

    private final java.util.Map<Long, Double> kerningMap = new java.util.HashMap<>();
    
    private McsdfFont(Identifier atlasTexture, Identifier fontMetadata) throws JsonSyntaxException, IOException {
        this.texture = atlasTexture;
        
        var resource = mc.getResourceManager().getResource(fontMetadata).orElseThrow();
        this.metadata = gson.fromJson(new String(resource.open().readAllBytes()), MsdfFontMetadata.class);
        
        if (metadata.kerning != null) {
            for (var k : metadata.kerning) {
                long key = (((long) k.unicode1) << 32) | (k.unicode2 & 0xffffffffL);
                kerningMap.put(key, k.advance);
            }
        }
        
        NativeImage image = NativeImage.read(mc.getResourceManager().getResource(texture).get().open());

    	DynamicTexture dynamic = new DynamicTexture(() -> "msdf", image);

    	((AbstractTextureAccessor)dynamic).setSampler(RenderSystem.getSamplerCache().getSampler(
    	    AddressMode.CLAMP_TO_EDGE,
    	    AddressMode.CLAMP_TO_EDGE,
    	    FilterMode.LINEAR,
    	    FilterMode.LINEAR,
    	    false
    	));

    	mc.getTextureManager().register(texture, dynamic);
    }
    
    public MsdfGlyph getGlyph(char c) {
        int codepoint = c;
        for (MsdfGlyph g : metadata.glyphs) {
            if (g.unicode == codepoint) return g;
        }
        return null;
    }
    
    public double getKerning(int left, int right) {
        long key = (((long) left) << 32) | (right & 0xffffffffL);
        return kerningMap.getOrDefault(key, 0.0);
    }
    
    public static class McsdfFontBuilder {
        private Identifier atlasTexture;
        private Identifier fontMetadata;

        public McsdfFontBuilder atlas(Identifier atlasTexture) {
            this.atlasTexture = atlasTexture;
            return this;
        }

        public McsdfFontBuilder metadata(Identifier fontMetadata) {
            this.fontMetadata = fontMetadata;
            return this;
        }

        public McsdfFont build() throws JsonSyntaxException, IOException {
            if (atlasTexture == null)
                throw new IllegalStateException("McsdfFontBuilder: atlas texture is required");

            if (fontMetadata == null)
                throw new IllegalStateException("McsdfFontBuilder: font metadata is required");

            return new McsdfFont(atlasTexture, fontMetadata);
        }
    }

    public static McsdfFontBuilder builder() {
        return new McsdfFontBuilder();
    }
}