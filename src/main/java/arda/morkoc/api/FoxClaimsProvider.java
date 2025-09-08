package arda.morkoc.api;

import arda.morkoc.api.events.FoxClaimsCreateEvent;
import arda.morkoc.api.events.FoxClaimsDeleteEvent;
import arda.morkoc.api.model.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

public class FoxClaimsProvider {

    private static Plugin foxPlugin;
    private static Plugin apiPlugin; // API plugin referansı
    private static Method getClaimAtChunkMethod;
    private static Method getClaimAtLocationMethod;
    private static Method getClaimByIdMethod;
    private static boolean initialized = false;

    public static boolean initialize() {
        return initialize(null);
    }

    public static boolean initialize(Plugin apiPluginInstance) {
        if (initialized) return true;

        try {
            foxPlugin = Bukkit.getPluginManager().getPlugin("FoxClaims");

            // API plugin referansını ayarla
            if (apiPluginInstance != null) {
                apiPlugin = apiPluginInstance;
            }

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

    public static void notifyClaimCreate(Claim claim, Player player) {
        if (!initialized) {
            System.out.println("❌ FoxClaims API henüz başlatılmadı!");
            return;
        }

        // Event'i hem ana plugin hem de API plugin üzerinden tetikle
        PluginManager pm = Bukkit.getPluginManager();
        FoxClaimsCreateEvent event = new FoxClaimsCreateEvent(claim, player);

        // Ana plugin'den tetikle
        pm.callEvent(event);

        // API plugin varsa, onun üzerinden de tetikle
        if (apiPlugin != null) {
            // Scheduler kullanarak bir sonraki tick'te tetikle
            Bukkit.getScheduler().runTask(apiPlugin, () -> {
                pm.callEvent(new FoxClaimsCreateEvent(claim, player));
            });
        }

        System.out.println("✅ FoxClaimsCreateEvent tetiklendi - Claim ID: " + claim.id);
    }

    public static void notifyClaimDelete(Claim claim, Player player) {
        if (!initialized) {
            System.out.println("❌ FoxClaims API henüz başlatılmadı!");
            return;
        }

        // Event'i hem ana plugin hem de API plugin üzerinden tetikle
        PluginManager pm = Bukkit.getPluginManager();
        FoxClaimsDeleteEvent event = new FoxClaimsDeleteEvent(claim, player);

        // Ana plugin'den tetikle
        pm.callEvent(event);

        // API plugin varsa, onun üzerinden de tetikle
        if (apiPlugin != null) {
            // Scheduler kullanarak bir sonraki tick'te tetikle
            Bukkit.getScheduler().runTask(apiPlugin, () -> {
                pm.callEvent(new FoxClaimsDeleteEvent(claim, player));
            });
        }

        System.out.println("✅ FoxClaimsDeleteEvent tetiklendi - Claim ID: " + claim.id);
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

    public static Claim convertObjectToClaim(Object claimObj) {
        return convertToClaim(claimObj);
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
            String effectType = (String) claimClass.getField("effectType").get(claimObj);
            Timestamp createdAt = (Timestamp) claimClass.getField("createdAt").get(claimObj);
            double energy = (double) claimClass.getField("energy").get(claimObj);
            double maxEnergy = (double) claimClass.getField("maxEnergy").get(claimObj);
            String logWebhook = (String) claimClass.getField("logWebhook").get(claimObj);
            boolean isMessageAlertEnabled = (boolean) claimClass.getField("isMessageAlertEnabled").get(claimObj);
            boolean isSoundAlertEnabled = (boolean) claimClass.getField("isSoundAlertEnabled").get(claimObj);
            boolean isScreenMessageEnabled = (boolean) claimClass.getField("isScreenMessageEnabled").get(claimObj);
            boolean isTimeHidden = (boolean) claimClass.getField("isTimeHidden").get(claimObj);
            boolean isStreamerModeEnabled = (boolean) claimClass.getField("isStreamerModeEnabled").get(claimObj);
            Map<UUID, Map<String, Object>> members = (Map<UUID, Map<String, Object>>) claimClass.getField("members").get(claimObj);

            return new Claim(id, name, ownerName, ownerUUID, worldNameField, x, y, z,
                    chunk_x, chunk_z, effectType, createdAt, energy, maxEnergy, logWebhook,
                    isMessageAlertEnabled, isSoundAlertEnabled, isScreenMessageEnabled,
                    isTimeHidden, isStreamerModeEnabled, members);

        } catch (Exception e) {
            System.out.println("Claim dönüştürme hatası: " + e.getMessage());
            return null;
        }
    }
}