package io.github.mcsdf.client.font;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import io.github.mcsdf.client.font.metadata.MsdfFontMetadata;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class McsdfFont {
	private static final Minecraft mc = Minecraft.getInstance();
	private static final Gson gson = new Gson();
	
    public final Identifier texture;
    public final MsdfFontMetadata metadata;

    private McsdfFont(Identifier atlasTexture, Identifier fontMetadata) throws JsonSyntaxException, IOException {
        this.texture = atlasTexture;
        
        var resource = mc.getResourceManager().getResource(fontMetadata).orElseThrow();
        this.metadata = gson.fromJson(new String(resource.open().readAllBytes()), MsdfFontMetadata.class);
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