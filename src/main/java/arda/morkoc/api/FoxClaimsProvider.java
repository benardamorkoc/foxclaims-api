package arda.morkoc.api;

import arda.morkoc.api.model.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * FoxClaims API Provider - Callback Sistemi ile
 */
public class FoxClaimsProvider {

    private static Plugin foxPlugin;
    private static Method getClaimAtChunkMethod;
    private static Method getClaimAtLocationMethod;
    private static Method getClaimByIdMethod;
    private static boolean initialized = false;

    // Callback sistemi
    private static final List<ClaimCallback> callbacks = new CopyOnWriteArrayList<>();

    /**
     * Claim işlemleri için callback interface
     */
    public interface ClaimCallback {
        void onClaimCreate(Claim claim, Player player);

        // İlerde başka eventler ekleyebilirsiniz:
        // void onClaimDelete(Claim claim, Player player);
        // void onClaimEnter(Claim claim, Player player);
    }

    /**
     * Provider'ı başlatır
     */
    public static boolean initialize() {
        if (initialized) return true;

        try {
            foxPlugin = Bukkit.getPluginManager().getPlugin("FoxClaims");

            if (foxPlugin == null || !foxPlugin.isEnabled()) {
                return false;
            }

            getClaimAtChunkMethod = foxPlugin.getClass()
                    .getMethod("getClaimAtChunk", String.class, int.class, int.class);

            getClaimAtLocationMethod = foxPlugin.getClass()
                    .getMethod("getClaimAtLocation", Location.class);

            getClaimByIdMethod = foxPlugin.getClass()
                    .getMethod("getClaimById", int.class);

            initialized = true;
            return true;

        } catch (Exception e) {
            System.out.println("FoxClaimsProvider initialize hatası: " + e.getMessage());
            return false;
        }
    }

    public static boolean isAvailable() {
        return initialize();
    }

    /**
     * Callback sistemi - Plugin'ler buraya listener ekler
     */
    public static void registerCallback(ClaimCallback callback) {
        callbacks.add(callback);
        System.out.println("✅ Callback kaydedildi. Toplam callback: " + callbacks.size());
    }

    public static void unregisterCallback(ClaimCallback callback) {
        callbacks.remove(callback);
    }

    /**
     * Ana plugin'den çağrılacak - Claim oluşturulduğunda
     */
    public static void notifyClaimCreate(Claim claim, Player player) {
        System.out.println("🔔 Claim create bildirimi: " + callbacks.size() + " callback var");

        for (ClaimCallback callback : callbacks) {
            try {
                callback.onClaimCreate(claim, player);
            } catch (Exception e) {
                System.out.println("Callback hatası: " + e.getMessage());
            }
        }
    }

    public static Claim getClaimAtChunk(String worldName, int chunkX, int chunkZ) {
        if (!initialize()) return null;

        try {
            Object claimObj = getClaimAtChunkMethod.invoke(foxPlugin, worldName, chunkX, chunkZ);
            return convertToClaim(claimObj);
        } catch (Exception e) {
            System.out.println("getClaimAtChunk hatası: " + e.getMessage());
            return null;
        }
    }

    public static Claim getClaimAtLocation(Location location) {
        if (!initialize()) return null;

        try {
            Object claimObj = getClaimAtLocationMethod.invoke(foxPlugin, location);
            return convertToClaim(claimObj);
        } catch (Exception e) {
            System.out.println("getClaimAtLocation hatası: " + e.getMessage());
            return null;
        }
    }

    public static Claim getClaimById(int id) {
        if (!initialize()) return null;

        try {
            Object claimObj = getClaimByIdMethod.invoke(foxPlugin, id);
            return convertToClaim(claimObj);
        } catch (Exception e) {
            System.out.println("getClaimById hatası: " + e.getMessage());
            return null;
        }
    }

    private static Claim convertToClaim(Object claimObj) {
        if (claimObj == null) return null;

        try {
            Class<?> claimClass = claimObj.getClass();

            int id = (int) claimClass.getField("id").get(claimObj);
            String name = (String) claimClass.getField("name").get(claimObj);
            String ownerName = (String) claimClass.getField("ownerName").get(claimObj);
            String ownerUUID = (String) claimClass.getField("ownerUUID").get(claimObj);
            String worldNameField = (String) claimClass.getField("worldName").get(claimObj);
            int x = (int) claimClass.getField("x").get(claimObj);
            int y = (int) claimClass.getField("y").get(claimObj);
            int z = (int) claimClass.getField("z").get(claimObj);
            int chunk_x = (int) claimClass.getField("chunk_x").get(claimObj);
            int chunk_z = (int) claimClass.getField("chunk_z").get(claimObj);
            String createdAt = (String) claimClass.getField("createdAt").get(claimObj);
            double energy = (double) claimClass.getField("energy").get(claimObj);
            double maxEnergy = (double) claimClass.getField("maxEnergy").get(claimObj);
            String logWebhook = (String) claimClass.getField("logWebhook").get(claimObj);
            boolean isMessageAlertEnabled = (boolean) claimClass.getField("isMessageAlertEnabled").get(claimObj);
            boolean isSoundAlertEnabled = (boolean) claimClass.getField("isSoundAlertEnabled").get(claimObj);
            boolean isScreenMessageEnabled = (boolean) claimClass.getField("isScreenMessageEnabled").get(claimObj);
            boolean isTimeHidden = (boolean) claimClass.getField("isTimeHidden").get(claimObj);
            boolean isStreamerModeEnabled = (boolean) claimClass.getField("isStreamerModeEnabled").get(claimObj);

            return new Claim(id, name, ownerName, ownerUUID, worldNameField, x, y, z,
                    chunk_x, chunk_z, createdAt, energy, maxEnergy, logWebhook,
                    isMessageAlertEnabled, isSoundAlertEnabled, isScreenMessageEnabled,
                    isTimeHidden, isStreamerModeEnabled);

        } catch (Exception e) {
            System.out.println("Claim dönüştürme hatası: " + e.getMessage());
            return null;
        }
    }
}