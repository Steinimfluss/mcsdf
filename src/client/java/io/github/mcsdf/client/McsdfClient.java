package io.github.mcsdf.client;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import io.github.mcsdf.Mcsdf;
import io.github.mcsdf.client.font.DefaultFonts;
import io.github.mcsdf.client.font.Font;
import io.github.mcsdf.client.render.McsdfPipelines;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.impl.resource.ResourceLoaderImpl;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public class McsdfClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ResourceLoaderImpl.get(PackType.CLIENT_RESOURCES)
        .registerReloadListener(
            Identifier.fromNamespaceAndPath(Mcsdf.MOD_ID, "font"),
            
            new PreparableReloadListener() {
            	@Override
            	public CompletableFuture<Void> reload(
            	        SharedState currentReload,
            	        Executor taskExecutor,
            	        PreparationBarrier preparationBarrier,
            	        Executor reloadExecutor
            	) {
            	    CompletableFuture<Void> prepare = CompletableFuture.runAsync(() -> {
            	    	
            	    }, taskExecutor);

            	    CompletableFuture<Void> barrier = prepare.thenCompose(preparationBarrier::wait);

            	    return barrier.thenRunAsync(() -> {
            	    	try {
            	    		McsdfPipelines.precompile();
							DefaultFonts.ARIAL = Font.builder().atlas(id("fonts/arial/atlas.png")).metadata(id("fonts/arial/atlas.json")).build();
							DefaultFonts.CALIBRI = Font.builder().atlas(id("fonts/calibri/atlas.png")).metadata(id("fonts/calibri/atlas.json")).build();
						} catch (IOException e) {
							e.printStackTrace();
						}
            	    }, reloadExecutor);
            	}
            }
        );
	}
	
	public Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(Mcsdf.MOD_ID, path);
	}
}