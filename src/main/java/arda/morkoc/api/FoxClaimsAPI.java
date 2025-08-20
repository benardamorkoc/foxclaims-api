package arda.morkoc.api;

import arda.morkoc.api.model.Claim;
import org.bukkit.Location;

/**
 * FoxClaims Plugin API
 * Bu interface diğer pluginler tarafından kullanılabilir
 */
public interface FoxClaimsAPI {

    // Dünya, chunkX ve chunkZ'ye göre Claim nesnesini döndüren metod
    Claim getClaimAtChunk(String worldName, int chunkX, int chunkZ);
    Claim getClaimAtLocation(Location location);
    Claim getClaimById(int id);

}