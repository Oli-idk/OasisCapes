package xyz.akiradev.oasiscosmetics.client.render;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import xyz.akiradev.oasiscosmetics.Utils.Executor;
import xyz.akiradev.oasiscosmetics.Utils.HttpUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class Capes {
    private static final String CAPE_OWNERS_URL = "http://135.181.141.7:40015/api/capeowners.json";
    private static final String CAPES_URL = "http://135.181.141.7:40015/api/capes.json";

    private static final Map<UUID, String> OWNERS = new HashMap<>();
    private static final Map<String, String> URLS = new HashMap<>();
    private static final Map<String, Cape> TEXTURES = new HashMap<>();

    private static final List<Cape> TO_REGISTER = new ArrayList<>();
    private static final List<Cape> TO_RETRY = new ArrayList<>();
    private static final List<Cape> TO_REMOVE = new ArrayList<>();
    public static void init() throws IOException {
        // Cape owners

        List<Map<String, String>> capeOwners = new Gson().<Map<String, List<Map<String, String>>>>fromJson(new BufferedReader(new InputStreamReader(new URL(CAPE_OWNERS_URL).openStream())), new TypeToken<Map<String, List<Map<String, String>>>>() {}.getType()).get("CapeOwners");
            capeOwners.forEach(s -> OWNERS.put(UUID.fromString(s.get("UUID")), s.get("CAPE")));
            capeOwners.forEach(s -> {if (!TEXTURES.containsKey(s.get("CAPE"))) TEXTURES.put(s.get("CAPE"), new Cape(s.get("CAPE")));});



        // Capes
        List<Map<String, String>> capes = new Gson().<Map<String, List<Map<String, String>>>>fromJson(new BufferedReader(new InputStreamReader(new URL(CAPES_URL).openStream())), new TypeToken<Map<String, List<Map<String, String>>>>() {}.getType()).get("Capes");
        capes.forEach(s -> {if (!URLS.containsKey(s.get("CapeName"))) URLS.put(s.get("CapeName"), s.get("CapeURL"));});
    }

    public static void reload() throws IOException {
        TEXTURES.clear();
        URLS.clear();
        Capes.init();
    }
    public static Identifier getCape(PlayerEntity player) {
        String capeName = OWNERS.get(player.getUuid());
        if (capeName != null) {
            Cape cape = TEXTURES.get(capeName);
            if (cape == null) return null;

            if (cape.isDownloaded()) return cape;

            cape.download();
            return null;
        }

        return null;
    }

    public static void tick() {
        synchronized (TO_REGISTER) {
            for (Cape cape : TO_REGISTER) cape.register();
            TO_REGISTER.clear();
        }

        synchronized (TO_RETRY) {
            TO_RETRY.removeIf(Cape::tick);
        }

        synchronized (TO_REMOVE) {
            for (Cape cape : TO_REMOVE) {
                URLS.remove(cape.name);
                TEXTURES.remove(cape.name);
                TO_REGISTER.remove(cape);
                TO_RETRY.remove(cape);
            }

            TO_REMOVE.clear();
        }
    }

    private static class Cape extends Identifier {
        private final String name;

        private boolean downloaded;
        private boolean downloading;

        private NativeImage img;

        private int retryTimer;

        public Cape(String name) {
            super("oasis-capes", "capes/" + name);

            this.name = name;
        }

        public void download() {
            if (downloaded || downloading || retryTimer > 0) return;
            downloading = true;

            Executor.execute(() -> {
                try {
                    String url = URLS.get(name);
                    if (url == null) {
                        synchronized (TO_RETRY) {
                            TO_REMOVE.add(this);
                            downloading = false;
                            return;
                        }
                    }

                    InputStream in = HttpUtils.get(url);
                    if (in == null) {
                        synchronized (TO_RETRY) {
                            TO_RETRY.add(this);
                            retryTimer = 10 * 20;
                            downloading = false;
                            return;
                        }
                    }

                    img = NativeImage.read(in);

                    synchronized (TO_REGISTER) {
                        TO_REGISTER.add(this);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        public void register() {
            MinecraftClient.getInstance().getTextureManager().registerTexture(this, new NativeImageBackedTexture(img));
            img = null;

            downloading = false;
            downloaded = true;
        }

        public boolean tick() {
            if (retryTimer > 0) {
                retryTimer--;
            } else {
                download();
                return true;
            }

            return false;
        }

        public boolean isDownloaded() {
            return downloaded;
        }
    }
}
