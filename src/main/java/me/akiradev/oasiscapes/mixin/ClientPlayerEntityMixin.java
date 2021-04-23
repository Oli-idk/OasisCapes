package me.akiradev.oasiscapes.mixin;

import me.akiradev.oasiscapes.Utils.Capes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Inject(at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
    private void onSendChatMessage(String msg, CallbackInfo info) {
        if(msg.equalsIgnoreCase(".reload")){
            Capes.reload();
            MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT, Text.of("§b[OC] §cCapes Reloaded!"), UUID.randomUUID());
            info.cancel();
        }
    }

}
