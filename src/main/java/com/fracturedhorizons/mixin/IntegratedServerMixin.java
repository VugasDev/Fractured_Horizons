package com.fracturedhorizons.mixin;

import com.fracturedhorizons.terrain.ApexSurfaceRulesInjector;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Single-Player-Äquivalent zu DedicatedServerMixin — injectet Surface Rules vor loadLevel().
 */
@Mixin(value = IntegratedServer.class, priority = 1001)
public class IntegratedServerMixin {

    @Inject(
        method = "initServer()Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/server/IntegratedServer;loadLevel()V"
        )
    )
    private void apex$injectSurfaceRulesBeforeLoad(CallbackInfoReturnable<Boolean> cir) {
        ApexSurfaceRulesInjector.inject((MinecraftServer) (Object) this);
    }
}
