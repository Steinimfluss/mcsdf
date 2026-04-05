package io.github.mcsdf.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.blaze3d.textures.GpuSampler;

import net.minecraft.client.renderer.texture.AbstractTexture;

@Mixin(AbstractTexture.class)
public interface AbstractTextureAccessor {
    @Accessor("sampler")
    void setSampler(GpuSampler sampler);
}