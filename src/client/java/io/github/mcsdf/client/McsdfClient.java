package io.github.mcsdf.client;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import io.github.mcsdf.Mcsdf;
import io.github.mcsdf.client.font.McsdfFont;
import io.github.mcsdf.client.font.McsdfFonts;
import io.github.mcsdf.client.render.McsdfPipelines;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.impl.resource.ResourceLoaderImpl;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public class McsdfClient implements ClientModInitializer {
	private boolean compiled;
	
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
            	    		if(!compiled) {
	            	    		McsdfPipelines.precompile();
	            	    		compiled = true;
            	    		}
            	    		
							McsdfFonts.arial = McsdfFont.builder().atlas(id("msdffont/arial/atlas.png")).metadata(id("msdffont/arial/atlas.json")).build();
							McsdfFonts.calibri = McsdfFont.builder().atlas(id("msdffont/calibri/atlas.png")).metadata(id("msdffont/calibri/atlas.json")).build();
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