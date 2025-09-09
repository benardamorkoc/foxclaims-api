package arda.morkoc.api;

import arda.morkoc.api.model.Claim;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
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
    }

    /**
     * Listener kaldır
     */
    public static void unregisterListener(EventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Claim create event'ini tüm listener'lara bildir
     */
    public static void fireClaimCreateEvent(final Claim claim, final Player player) {
        // Listener'ları bilgilendir
        for (final EventListener listener : listeners) {
            try {
                Bukkit.getScheduler().runTask(getMainPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            listener.onClaimCreate(claim, player);
                        } catch (Exception e) {}
                    }
                });
            } catch (Exception e) {}
        }

        tryFireBukkitEvent("FoxClaimsCreateEvent", claim, player);
    }

    public static void fireClaimDeleteEvent(final Claim claim, final Player player) {
        for (final EventListener listener : listeners) {
            try {
                Bukkit.getScheduler().runTask(getMainPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            listener.onClaimDelete(claim, player);
                        } catch (Exception e) {}
                    }
                });
            } catch (Exception e) {}
        }

        tryFireBukkitEvent("FoxClaimsDeleteEvent", claim, player);
    }

    private static void tryFireBukkitEvent(String eventName, Claim claim, Player player) {
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();

        for (Plugin plugin : plugins) {
            if (!plugin.isEnabled()) continue;

            try {
                ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();
                Class<?> eventClass = Class.forName("arda.morkoc.api.events." + eventName, false, pluginClassLoader);

                Constructor<?> constructor = eventClass.getConstructor(
                        Class.forName("arda.morkoc.api.model.Claim", false, pluginClassLoader),
                        Player.class
                );

                Object pluginClaim = recreateClaimInClassLoader(claim, pluginClassLoader);

                final Object eventInstance = constructor.newInstance(pluginClaim, player);
                final Plugin currentPlugin = plugin;

                Bukkit.getScheduler().runTask(currentPlugin, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bukkit.getPluginManager().callEvent((Event) eventInstance);
                        } catch (Exception e) {}
                    }
                });

            } catch (Exception e) {}
        }
    }

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
            return null;
        }
    }

    private static Plugin getMainPlugin() {
        Plugin foxClaims = Bukkit.getPluginManager().getPlugin("FoxClaims");
        return foxClaims != null ? foxClaims : Bukkit.getPluginManager().getPlugins()[0];
    }

    public static int getListenerCount() {
        return listeners.size();
    }
}