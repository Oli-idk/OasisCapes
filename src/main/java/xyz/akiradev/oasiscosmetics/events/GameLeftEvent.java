package xyz.akiradev.oasiscosmetics.events;

public class GameLeftEvent {
    private static final GameLeftEvent INSTANCE = new GameLeftEvent();

    public static GameLeftEvent get() {
        return INSTANCE;
    }
}
