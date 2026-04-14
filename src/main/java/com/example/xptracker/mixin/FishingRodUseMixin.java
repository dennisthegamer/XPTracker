package com.example.xptracker.mixin;

import com.example.xptracker.XPSourceDetector;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class FishingRodUseMixin {

	@Inject(method = "useItem", at = @At("HEAD"))
	private void onUseItem(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		ItemStack stack = player.getItemInHand(hand);
		if (stack.getItem() instanceof FishingRodItem && player.fishing != null) {
			// Player is reeling in their fishing rod (bobber already exists = retrieval)
			XPSourceDetector.getInstance().onFishingRodRetrieve();
		}
	}
}
