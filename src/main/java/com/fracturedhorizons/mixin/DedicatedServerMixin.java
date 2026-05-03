package com.fracturedhorizons.mixin;

import com.fracturedhorizons.terrain.ApexSurfaceRulesInjector;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Injiziert Apex Surface Rules direkt vor loadLevel() — nach Lithostitched (Priority 1001 > 1000).
 * Damit sind Terralith-Surface-Rules beim Start der Spawn-Chunk-Generierung bereits aktiv.
 */
@Mixin(value = DedicatedServer.class, priority = 1001)
public class DedicatedServerMixin {

    @Inject(
        method = "initServer()Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/dedicated/DedicatedServer;loadLevel()V"
        )
    )
    private void apex$injectSurfaceRulesBeforeLoad(CallbackInfoReturnable<Boolean> cir) {
        ApexSurfaceRulesInjector.inject((MinecraftServer) (Object) this);
    }
}
