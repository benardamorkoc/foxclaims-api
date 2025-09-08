package arda.morkoc.api;

import arda.morkoc.api.events.FoxClaimsCreateEvent;
import arda.morkoc.api.events.FoxClaimsDeleteEvent;
import arda.morkoc.api.model.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

/**
 * FoxClaims API Provider - Event Bazlı Sistem
 */
public class FoxClaimsProvider {

    private static Plugin foxPlugin;
    private static Method getClaimAtChunkMethod;
    private static Method getClaimAtLocationMethod;
    private static Method getClaimByIdMethod;
    private static Method registerCallbackMethod;
    private static Method notifyCallbackMethod;
    private static boolean initialized = false;

    /**
     * Provider'ı başlatır
     */
    public static boolean initialize() {
        if (initialized) return true;

        System.out.println("🔧 FoxClaimsProvider initialize ediliyor...");

        try {
            foxPlugin = Bukkit.getPluginManager().getPlugin("FoxClaims");

            if (foxPlugin == null || !foxPlugin.isEnabled()) {
                System.out.println("❌ FoxClaims plugin bulunamadı!");
                return false;
            }

            System.out.println("✅ FoxClaims plugin bulundu!");

            // API metodları
            getClaimAtChunkMethod = foxPlugin.getClass()
                    .getMethod("getClaimAtChunk", String.class, int.class, int.class);

            getClaimAtLocationMethod = foxPlugin.getClass()
                    .getMethod("getClaimAtLocation", Location.class);

            getClaimByIdMethod = foxPlugin.getClass()
                    .getMethod("getClaimById", int.class);

            System.out.println("✅ Temel API metodları yüklendi!");

            // Callback metodları - Event sistemi için
            try {
                registerCallbackMethod = foxPlugin.getClass()
                        .getMethod("registerAPICallback", Object.class);

                notifyCallbackMethod = foxPlugin.getClass()
                        .getMethod("notifyAPICallbacks", String.class, Object.class, Object.class);

                // Event handler'ı kaydet
                registerEventHandler();

            } catch (NoSuchMethodException e) {
                System.out.println("⚠️ Ana plugin'de callback metodları bulunamadı!");
            }

            initialized = true;
            System.out.println("✅ FoxClaimsProvider initialize tamamlandı!");
            return true;

        } catch (Exception e) {
            System.out.println("❌ FoxClaimsProvider initialize hatası: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Event handler'ı ana plugin'e kaydet
     */
    private static void registerEventHandler() {
        if (registerCallbackMethod == null) return;

        try {
            // Event handler objesi
            Object eventHandler = new Object() {
                @SuppressWarnings("unused")
                public void onClaimCreate(Object claimObject, Object playerObject) {
                    System.out.println("🎯 Callback onClaimCreate tetiklendi!");
                    Claim claim = convertToClaim(claimObject);
                    Player player = (Player) playerObject;

                    if (claim != null && player != null) {
                        System.out.println("🚀 FoxClaimsCreateEvent oluşturuluyor...");
                        FoxClaimsCreateEvent event = new FoxClaimsCreateEvent(claim, player);
                        Bukkit.getPluginManager().callEvent(event);
                        System.out.println("✅ Event tetiklendi!");
                    } else {
                        System.out.println("❌ Claim veya Player null!");
                    }
                }

                @SuppressWarnings("unused")
                public void onClaimDelete(Object claimObject, Object playerObject) {
                    System.out.println("🎯 Callback onClaimDelete tetiklendi!");
                    Claim claim = convertToClaim(claimObject);
                    Player player = (Player) playerObject;

                    if (claim != null && player != null) {
                        FoxClaimsDeleteEvent event = new FoxClaimsDeleteEvent(claim, player);
                        Bukkit.getPluginManager().callEvent(event);
                    }
                }
            };

            registerCallbackMethod.invoke(foxPlugin, eventHandler);
            System.out.println("✅ FoxClaims event handler kaydedildi!");

        } catch (Exception e) {
            System.out.println("❌ Event handler kaydetme hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean isAvailable() {
        return initialize();
    }

    /**
     * Ana pluginden çağrılır - Claim oluşturulduğunda
     * Bu metodu ana plugininizden claim oluşturulduğunda çağırın
     */
    public static void notifyClaimCreate(Claim claim, Player player) {
        System.out.println("📢 notifyClaimCreate (Claim) çağrıldı!");
        System.out.println("   - Claim ID: " + (claim != null ? claim.id : "null"));
        System.out.println("   - Player: " + (player != null ? player.getName() : "null"));

        if (claim != null && player != null) {
            System.out.println("🎯 Event oluşturuluyor ve tetikleniyor...");
            FoxClaimsCreateEvent event = new FoxClaimsCreateEvent(claim, player);
            Bukkit.getPluginManager().callEvent(event);
            System.out.println("✅ Event başarıyla tetiklendi!");
        } else {
            System.out.println("❌ Claim veya Player null - event tetiklenmedi!");
        }
    }

    /**
     * Ana pluginden çağrılır - Claim silindiğinde
     * Bu metodu ana plugininizden claim silindiğinde çağırın
     */
    public static void notifyClaimDelete(Claim claim, Player player) {
        System.out.println("📢 notifyClaimDelete (Claim) çağrıldı!");

        if (claim != null && player != null) {
            FoxClaimsDeleteEvent event = new FoxClaimsDeleteEvent(claim, player);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    /**
     * Ana pluginden çağrılır - Object ile claim oluşturulduğunda
     * Bu metodu ana plugininizden direkt object ile çağırabilirsiniz
     */
    public static void notifyClaimCreate(Object claimObject, Player player) {
        System.out.println("📢 notifyClaimCreate (Object) çağrıldı!");
        System.out.println("   - ClaimObject: " + (claimObject != null ? claimObject.getClass().getSimpleName() : "null"));
        System.out.println("   - Player: " + (player != null ? player.getName() : "null"));

        if (claimObject == null) {
            System.out.println("❌ ClaimObject null - işlem durduruluyor!");
            return;
        }

        System.out.println("🔄 Object -> Claim dönüştürülüyor...");
        Claim claim = convertToClaim(claimObject);

        if (claim == null) {
            System.out.println("❌ Claim dönüştürme başarısız!");
            return;
        }

        System.out.println("✅ Claim başarıyla dönüştürüldü - ID: " + claim.id);
        notifyClaimCreate(claim, player);
    }

    /**
     * Ana pluginden çağrılır - Object ile claim silindiğinde
     * Bu metodu ana plugininizden direkt object ile çağırabilirsiniz
     */
    public static void notifyClaimDelete(Object claimObject, Player player) {
        System.out.println("📢 notifyClaimDelete (Object) çağrıldı!");
        Claim claim = convertToClaim(claimObject);
        notifyClaimDelete(claim, player);
    }

    // API metodları - değişiklik yok
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

    /**
     * Object'i Claim'e çevirir - Helper method
     */
    public static Claim convertObjectToClaim(Object claimObj) {
        return convertToClaim(claimObj);
    }

    private static Claim convertToClaim(Object claimObj) {
        if (claimObj == null) {
            System.out.println("❌ convertToClaim: claimObj null!");
            return null;
        }

        try {
            System.out.println("🔄 Claim dönüştürülüyor: " + claimObj.getClass().getSimpleName());
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

            Claim claim = new Claim(id, name, ownerName, ownerUUID, worldNameField, x, y, z,
                    chunk_x, chunk_z, effectType, createdAt, energy, maxEnergy, logWebhook,
                    isMessageAlertEnabled, isSoundAlertEnabled, isScreenMessageEnabled,
                    isTimeHidden, isStreamerModeEnabled, members);

            System.out.println("✅ Claim başarıyla dönüştürüldü - ID: " + claim.id + ", Name: " + claim.name);
            return claim;

        } catch (Exception e) {
            System.out.println("❌ Claim dönüştürme hatası: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}