package me.akiradev.oasiscapes.mixin;

import me.akiradev.oasiscapes.Utils.HttpUtils;
import me.akiradev.oasiscapes.client.OasiscapesClient;
import me.akiradev.oasiscapes.events.GameJoinedEvent;
import me.akiradev.oasiscapes.events.GameLeftEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Shadow
    private ClientWorld world;

    private boolean worldNotNull;

    private static String version = "1.0";

    @Inject(at = @At("HEAD"), method = "onGameJoin")
    private void onGameJoinHead(GameJoinS2CPacket packet, CallbackInfo info) {
        worldNotNull = world != null;
    }

    @Inject(at = @At("TAIL"), method = "onGameJoin")
    private void onGameJoinTail(GameJoinS2CPacket packet, CallbackInfo info) {
        HttpUtils.getLines("http://96.44.143.163:27020/api/version.txt", s -> {
            if (!s.equals(version)){
                MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT, Text.of("§b[OC] §cA new update for Oasis Capes is available!"), UUID.randomUUID());
            }
        });
    }

}
