package arda.morkoc.api;

import arda.morkoc.api.model.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * FoxClaims API Provider
 * Diğer pluginlerin kolayca API'ye erişmesini sağlar
 */
public class FoxClaimsProvider {

    private static FoxClaimsAPI api = null;

    /**
     * FoxClaims API instance'ını alır
     * @return FoxClaimsAPI instance veya null
     */
    public static FoxClaimsAPI getAPI() {
        if (api == null) {
            RegisteredServiceProvider<FoxClaimsAPI> provider =
                    Bukkit.getServicesManager().getRegistration(FoxClaimsAPI.class);

            if (provider != null) {
                api = provider.getProvider();
            }
        }
        return api;
    }

    /**
     * API'nin mevcut olup olmadığını kontrol eder
     * @return API mevcut mu?
     */
    public static boolean isAvailable() {
        return getAPI() != null;
    }

    /**
     * Kolaylık metodu - Chunk'a göre claim alır
     */
    public static Claim getClaimAtChunk(String worldName, int chunkX, int chunkZ) {
        FoxClaimsAPI foxApi = getAPI();
        return foxApi != null ? foxApi.getClaimAtChunk(worldName, chunkX, chunkZ) : null;
    }

    /**
     * Kolaylık metodu - Location'a göre claim alır
     */
    public static Claim getClaimAtLocation(Location location) {
        FoxClaimsAPI foxApi = getAPI();
        return foxApi != null ? foxApi.getClaimAtLocation(location) : null;
    }

    /**
     * Kolaylık metodu - ID'ye göre claim alır
     */
    public static Claim getClaimById(int id) {
        FoxClaimsAPI foxApi = getAPI();
        return foxApi != null ? foxApi.getClaimById(id) : null;
    }
}