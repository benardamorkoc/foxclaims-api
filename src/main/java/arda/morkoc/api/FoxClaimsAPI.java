package arda.morkoc.api;

import org.bukkit.entity.Player;

/**
 * FoxClaims Plugin API
 * Bu interface diğer pluginler tarafından kullanılabilir
 */
public interface FoxClaimsAPI {

    /**
     * Belirli bir oyuncuya claim mesajı gönderir
     * @param playerName Oyuncu adı
     * @param message Gönderilecek mesaj
     * @return Mesaj başarıyla gönderildiyse true, aksi halde false
     */
    boolean sendClaimMessage(String playerName, String message);

    /**
     * Belirli bir oyuncuya claim mesajı gönderir (Player objesi ile)
     * @param player Oyuncu objesi
     * @param message Gönderilecek mesaj
     * @return Mesaj başarıyla gönderildiyse true, aksi halde false
     */
    boolean sendClaimMessage(Player player, String message);

    /**
     * API'nin aktif olup olmadığını kontrol eder
     * @return API aktifse true, aksi halde false
     */
    boolean isAPIEnabled();

    /**
     * API versiyonunu döndürür
     * @return API versiyonu
     */
    String getAPIVersion();
}