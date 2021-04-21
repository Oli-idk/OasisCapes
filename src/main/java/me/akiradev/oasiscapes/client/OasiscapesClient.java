package me.akiradev.oasiscapes.client;

import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;
import me.zero.alpine.listener.Listenable;
import me.akiradev.oasiscapes.Utils.Capes;
import me.akiradev.oasiscapes.Utils.Executor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class OasiscapesClient implements ClientModInitializer, Listenable {
    public static OasiscapesClient INSTANCE;
    public static final EventBus EVENT_BUS = new EventManager();
    private static int tick;
    @Override
    public void onInitializeClient() {

        System.out.println("Initializing Oasis Capes.");

        Executor.init();
        Capes.init();

        EVENT_BUS.subscribe(this);

        ClientTickEvents.START_CLIENT_TICK.register((player) -> {
            if(tick == 20){
                Capes.tick();
                tick = 0;
            }else{
                tick++;
            }
        });

    }
}
