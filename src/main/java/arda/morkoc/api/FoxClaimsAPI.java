package arda.morkoc.api;

import arda.morkoc.api.events.ClaimCreateEvent;
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

    default void triggerClaimCreateEvent(Claim claim, org.bukkit.entity.Player player) {
        ClaimCreateEvent event = new ClaimCreateEvent(claim, player);
        org.bukkit.Bukkit.getPluginManager().callEvent(event);
    }
}