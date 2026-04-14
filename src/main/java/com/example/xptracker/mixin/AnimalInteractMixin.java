package com.example.xptracker.mixin;

import com.example.xptracker.XPSourceDetector;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class AnimalInteractMixin {

	@Inject(method = "interact", at = @At("HEAD"))
	private void onInteract(Player player, Entity target, EntityHitResult hitResult, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if (target instanceof Animal animal) {
			ItemStack stack = player.getItemInHand(hand);
			if (animal.isFood(stack)) {
				XPSourceDetector.getInstance().onBreeding();
			}
		}
	}
}
