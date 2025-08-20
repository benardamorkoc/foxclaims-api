package arda.morkoc.api;

import arda.morkoc.api.model.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class FoxClaimsProvider {

    private static FoxClaimsAPI api = null;

    public static FoxClaimsAPI getAPI() {
        if (api == null) {
            // Plugin kontrolü
            Plugin foxPlugin = Bukkit.getPluginManager().getPlugin("FoxClaims");
            if (foxPlugin == null || !foxPlugin.isEnabled()) {
                return null;
            }

            // Servis kaydı kontrolü
            RegisteredServiceProvider<FoxClaimsAPI> provider =
                    Bukkit.getServicesManager().getRegistration(FoxClaimsAPI.class);

            if (provider != null) {
                api = provider.getProvider();
            }
        }
        return api;
    }

    public static boolean isAvailable() {
        return getAPI() != null;
    }

    public static Claim getClaimAtChunk(String worldName, int chunkX, int chunkZ) {
        FoxClaimsAPI foxApi = getAPI();
        return foxApi != null ? foxApi.getClaimAtChunk(worldName, chunkX, chunkZ) : null;
    }

    public static Claim getClaimAtLocation(Location location) {
        FoxClaimsAPI foxApi = getAPI();
        return foxApi != null ? foxApi.getClaimAtLocation(location) : null;
    }

    public static Claim getClaimById(int id) {
        FoxClaimsAPI foxApi = getAPI();
        return foxApi != null ? foxApi.getClaimById(id) : null;
    }
}