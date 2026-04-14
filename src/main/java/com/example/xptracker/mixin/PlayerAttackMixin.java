package com.example.xptracker.mixin;

import com.example.xptracker.XPSourceDetector;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class PlayerAttackMixin {

	@Inject(method = "attack", at = @At("HEAD"))
	private void onAttack(Player player, Entity target, CallbackInfo ci) {
		XPSourceDetector.getInstance().onAttackEntity();
	}
}
