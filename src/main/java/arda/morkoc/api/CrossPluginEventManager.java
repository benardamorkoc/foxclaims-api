package arda.morkoc.api;

import arda.morkoc.api.model.Claim;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CrossPluginEventManager {

    private static final List<EventListener> listeners = new ArrayList<EventListener>();

    public interface EventListener {
        void onClaimCreate(Claim claim, Player player);
        void onClaimDelete(Claim claim, Player player);
    }

    /**
     * Listener kaydet
     */
    public static void registerListener(EventListener listener) {
        listeners.add(listener);
        System.out.println("✅ CrossPlugin EventListener kaydedildi: " + listener.getClass().getName());
    }

    /**
     * Listener kaldır
     */
    public static void unregisterListener(EventListener listener) {
        listeners.remove(listener);
        System.out.println("🗑️ CrossPlugin EventListener kaldırıldı: " + listener.getClass().getName());
    }

    /**
     * Claim create event'ini tüm listener'lara bildir
     */
    public static void fireClaimCreateEvent(final Claim claim, final Player player) {
        System.out.println("📢 CrossPlugin Claim Create Event - " + listeners.size() + " listener bilgilendiriliyor");

        // Listener'ları bilgilendir
        for (final EventListener listener : listeners) {
            try {
                // Ana thread'de çalıştır
                Bukkit.getScheduler().runTask(getMainPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            listener.onClaimCreate(claim, player);
                        } catch (Exception e) {
                            System.out.println("❌ Listener error: " + e.getMessage());
                        }
                    }
                });
            } catch (Exception e) {
                System.out.println("❌ Event dispatch error: " + e.getMessage());
            }
        }

        // Ayrıca her plugin'de reflection ile event oluşturmaya çalış
        tryFireBukkitEvent("FoxClaimsCreateEvent", claim, player);
    }

    /**
     * Claim delete event'ini tüm listener'lara bildir
     */
    public static void fireClaimDeleteEvent(final Claim claim, final Player player) {
        System.out.println("📢 CrossPlugin Claim Delete Event - " + listeners.size() + " listener bilgilendiriliyor");

        // Listener'ları bilgilendir
        for (final EventListener listener : listeners) {
            try {
                // Ana thread'de çalıştır
                Bukkit.getScheduler().runTask(getMainPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            listener.onClaimDelete(claim, player);
                        } catch (Exception e) {
                            System.out.println("❌ Listener error: " + e.getMessage());
                        }
                    }
                });
            } catch (Exception e) {
                System.out.println("❌ Event dispatch error: " + e.getMessage());
            }
        }

        // Ayrıca her plugin'de reflection ile event oluşturmaya çalış
        tryFireBukkitEvent("FoxClaimsDeleteEvent", claim, player);
    }

    /**
     * Her plugin'in ClassLoader'ında event oluşturmaya çalış
     */
    private static void tryFireBukkitEvent(String eventName, Claim claim, Player player) {
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();

        for (Plugin plugin : plugins) {
            if (!plugin.isEnabled()) continue;

            try {
                // Plugin'in ClassLoader'ında event class'ını bul
                ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();
                Class<?> eventClass = Class.forName("arda.morkoc.api.events." + eventName, false, pluginClassLoader);

                // Constructor'ı bul ve event oluştur
                Constructor<?> constructor = eventClass.getConstructor(
                        Class.forName("arda.morkoc.api.model.Claim", false, pluginClassLoader),
                        Player.class
                );

                // Claim'i plugin'in ClassLoader'ında yeniden oluştur
                Object pluginClaim = recreateClaimInClassLoader(claim, pluginClassLoader);

                // Event'i oluştur
                final Object eventInstance = constructor.newInstance(pluginClaim, player);
                final Plugin currentPlugin = plugin;

                // Event'i tetikle
                Bukkit.getScheduler().runTask(currentPlugin, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bukkit.getPluginManager().callEvent((Event) eventInstance);
                            System.out.println("✅ " + eventName + " tetiklendi plugin: " + currentPlugin.getName());
                        } catch (Exception e) {
                            System.out.println("❌ Event call failed for " + currentPlugin.getName() + ": " + e.getMessage());
                        }
                    }
                });

            } catch (Exception e) {
                // Bu plugin'de event class'ı yok, sorun değil
                // System.out.println("⚠️ Plugin " + plugin.getName() + " doesn't have " + eventName);
            }
        }
    }

    /**
     * Claim'i farklı ClassLoader'da yeniden oluştur
     */
    private static Object recreateClaimInClassLoader(Claim originalClaim, ClassLoader targetClassLoader) {
        try {
            Class<?> claimClass = Class.forName("arda.morkoc.api.model.Claim", false, targetClassLoader);
            Constructor<?> constructor = claimClass.getConstructor(
                    int.class, String.class, String.class, String.class, String.class,
                    int.class, int.class, int.class, int.class, int.class, String.class,
                    java.sql.Timestamp.class, double.class, double.class, String.class,
                    boolean.class, boolean.class, boolean.class, boolean.class, boolean.class,
                    java.util.Map.class
            );

            return constructor.newInstance(
                    originalClaim.id, originalClaim.name, originalClaim.ownerName, originalClaim.ownerUUID,
                    originalClaim.worldName, originalClaim.x, originalClaim.y, originalClaim.z,
                    originalClaim.chunk_x, originalClaim.chunk_z, originalClaim.effectType,
                    originalClaim.createdAt, originalClaim.energy, originalClaim.maxEnergy, originalClaim.logWebhook,
                    originalClaim.isMessageAlertEnabled, originalClaim.isSoundAlertEnabled, originalClaim.isScreenMessageEnabled,
                    originalClaim.isTimeHidden, originalClaim.isStreamerModeEnabled, originalClaim.members
            );
        } catch (Exception e) {
            System.out.println("❌ Claim recreation failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Ana plugin'i bul
     */
    private static Plugin getMainPlugin() {
        Plugin foxClaims = Bukkit.getPluginManager().getPlugin("FoxClaims");
        return foxClaims != null ? foxClaims : Bukkit.getPluginManager().getPlugins()[0];
    }

    /**
     * Listener sayısı
     */
    public static int getListenerCount() {
        return listeners.size();
    }
}