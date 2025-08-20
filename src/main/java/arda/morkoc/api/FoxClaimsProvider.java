package arda.morkoc.api;

import arda.morkoc.api.model.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

/**
 * FoxClaims API Provider - Reflection Based
 * Classloader problemlerini çözer
 */
public class FoxClaimsProvider {

    private static Plugin foxPlugin;
    private static Method getClaimAtChunkMethod;
    private static Method getClaimAtLocationMethod;
    private static Method getClaimByIdMethod;
    private static Method triggerEventMethod;
    private static boolean initialized = false;

    /**
     * Provider'ı başlatır
     * @return Başlatma başarılı mı?
     */
    public static boolean initialize() {
        if (initialized) return true;

        try {
            foxPlugin = Bukkit.getPluginManager().getPlugin("FoxClaims");

            if (foxPlugin == null || !foxPlugin.isEnabled()) {
                return false;
            }

            // Metodları cache'le
            getClaimAtChunkMethod = foxPlugin.getClass()
                    .getMethod("getClaimAtChunk", String.class, int.class, int.class);

            getClaimAtLocationMethod = foxPlugin.getClass()
                    .getMethod("getClaimAtLocation", Location.class);

            getClaimByIdMethod = foxPlugin.getClass()
                    .getMethod("getClaimById", int.class);

            try {
                triggerEventMethod = foxPlugin.getClass()
                        .getMethod("triggerAPIEvent", String.class, Object.class, Object.class);
            } catch (NoSuchMethodException e) {
                System.out.println("Ana plugin'de triggerAPIEvent methodu bulunamadı. Event sistemi çalışmayabilir.");
            }

            initialized = true;
            return true;

        } catch (Exception e) {
            System.out.println("FoxClaimsProvider initialize hatası: " + e.getMessage());
            return false;
        }
    }

    /**
     * API'nin mevcut olup olmadığını kontrol eder
     */
    public static boolean isAvailable() {
        return initialize();
    }

    /**
     * Chunk'a göre claim alır
     */
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

    /**
     * Location'a göre claim alır
     */
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

    /**
     * ID'ye göre claim alır
     */
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

    /**
     * Event tetikleme - harici pluginler için
     */
    public static void triggerClaimCreateEvent(Claim claim, Player player) {
        if (!initialize() || triggerEventMethod == null) {
            System.out.println("Event sistemi mevcut değil!");
            return;
        }

        try {
            // Ana plugin'deki triggerAPIEvent metodunu çağır
            triggerEventMethod.invoke(foxPlugin, "ClaimCreate", claim, player);

        } catch (Exception e) {
            System.out.println("Event tetikleme hatası: " + e.getMessage());
        }
    }

    /**
     * Object'i Claim'e çevirir (mevcut kodunuz)
     */
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