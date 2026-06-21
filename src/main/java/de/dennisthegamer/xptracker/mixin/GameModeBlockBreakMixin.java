package de.dennisthegamer.xptracker.mixin;

import de.dennisthegamer.xptracker.XPSourceDetector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class GameModeBlockBreakMixin {

	@Inject(method = "destroyBlock", at = @At("HEAD"))
	private void onDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null) {
			BlockState state = mc.level.getBlockState(pos);
			XPSourceDetector.getInstance().onBlockBroken(state);
		}
	}
}
