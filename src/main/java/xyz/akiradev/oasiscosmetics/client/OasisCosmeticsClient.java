package xyz.akiradev.oasiscosmetics.client;

import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;
import me.zero.alpine.listener.Listenable;
import xyz.akiradev.oasiscosmetics.client.render.Capes;
import xyz.akiradev.oasiscosmetics.Utils.Executor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.io.IOException;

public class OasisCosmeticsClient implements ClientModInitializer, Listenable {
    public static final String MOD_ID = "oasis-capes";
    public static OasisCosmeticsClient INSTANCE;
    public static final EventBus EVENT_BUS = new EventManager();
    private static int tick;
    @Override
    public void onInitializeClient() {

        System.out.println("Initializing Oasis Capes.");

        Executor.init();
        try {
            Capes.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        EVENT_BUS.subscribe(this);
        ClientTickEvents.START_CLIENT_TICK.register((player) -> {
            if(tick == 120){
                Capes.tick();
                tick = 0;
            }else{
                tick++;
            }
        });
    }
}
