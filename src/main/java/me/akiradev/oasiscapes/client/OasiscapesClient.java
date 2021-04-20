package me.akiradev.oasiscapes.client;

import me.akiradev.oasiscapes.events.PostTickEvent;
import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;
import me.akiradev.oasiscapes.Utils.Capes;
import me.akiradev.oasiscapes.Utils.Executor;
import net.fabricmc.api.ClientModInitializer;

public class OasiscapesClient implements ClientModInitializer, Listenable {
    public static OasiscapesClient INSTANCE;
    public static final EventBus EVENT_BUS = new EventManager();
    @Override
    public void onInitializeClient() {
        if (INSTANCE == null) {
            INSTANCE = this;
            return;
        }
        Executor.init();
        Capes.init();
        EVENT_BUS.subscribe(this);
    }
    @EventHandler
    private final Listener<PostTickEvent> onTick = new Listener<>(event -> {
        Capes.tick();
    });

    public static <T> T postEvent(T event) {
        EVENT_BUS.post(event);
        return event;
    }
}
