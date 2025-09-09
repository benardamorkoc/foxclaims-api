# FoxClaims API

Comprehensive API library developed for the FoxClaims plugin. This API allows other plugins to interact with FoxClaims.

-----

## 📦 Maven Setup

### Add Repository

Add the following repository to the `<repositories>` section of your `pom.xml` file:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

### Add Dependency

Add the following dependency to the `<dependencies>` section of your `pom.xml` file:

```xml
<dependencies>
    <dependency>
        <groupId>com.github.benardamorkoc</groupId>
        <artifactId>foxclaims-api</artifactId>
        <version>v1.3.6</version>
    </dependency>
</dependencies>
```

### Plugin.yml Dependency

Add FoxClaims as a dependency to your `plugin.yml` file:

```yaml
name: YourPlugin
version: 1.0.0
main: your.package.MainClass
depend: [FoxClaims]
# or you can use soft-depend
# soft-depend: [FoxClaims]
```

-----

## 🚀 API Usage

### 1\. Claim Query

#### Find Claim by Chunk Coordinates

```java
import arda.morkoc.api.FoxClaimsProvider;
import arda.morkoc.api.model.Claim;

public class ExampleUsage {
    
    public void findClaimByChunk(String worldName, int chunkX, int chunkZ) {
        // Check if there is a claim in a specific chunk
        Claim claim = FoxClaimsProvider.getClaimAtChunk(worldName, chunkX, chunkZ);
        
        if (claim != null) {
            System.out.println("Claim found: " + claim.name);
            System.out.println("Owner: " + claim.ownerName);
            System.out.println("Energy: " + claim.energy + "/" + claim.maxEnergy);
        } else {
            System.out.println("No claim found in this chunk");
        }
    }
}
```

#### Find Claim by Location

```java
import org.bukkit.Location;
import org.bukkit.entity.Player;

public void findClaimByLocation(Player player) {
    Location loc = player.getLocation();
    Claim claim = FoxClaimsProvider.getClaimAtLocation(loc);
    
    if (claim != null) {
        player.sendMessage("You are currently in the claim: " + claim.name + "!");
    } else {
        player.sendMessage("There is no claim here.");
    }
}
```

#### Find Claim by ID

```java
public void findClaimById(int claimId) {
    Claim claim = FoxClaimsProvider.getClaimById(claimId);
    
    if (claim != null) {
        System.out.println("Claim with ID " + claimId + " found: " + claim.name);
    }
}
```

-----

### 2\. Event Listening

The FoxClaims API allows you to listen for claim creation and deletion events.

#### Create an Event Handler

```java
import arda.morkoc.api.events.FoxClaimsCreateEvent;
import arda.morkoc.api.events.FoxClaimsDeleteEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FoxClaimsListener implements Listener {
    
    @EventHandler
    public void onClaimCreate(FoxClaimsCreateEvent event) {
        Claim claim = event.getClaim();
        Player player = event.getPlayer();
        
        // Actions to perform when a claim is created
        player.sendMessage("✅ New claim created: " + claim.name);
    }
    
    @EventHandler
    public void onClaimDelete(FoxClaimsDeleteEvent event) {
        Claim claim = event.getClaim();
        Player player = event.getPlayer();
        
        // Actions to perform when a claim is deleted
        player.sendMessage("❌ Claim deleted: " + claim.name);
    }
}
```

-----

### 3\. Working with Claim Members and Permissions

The `members` property in the Claim class contains detailed permission information for each member. Here's how to work with this data:

#### Checking Member Permissions

```java
import java.util.Map;
import java.util.UUID;

public void checkMemberPermissions(Claim claim, UUID playerUUID) {
    if (claim.members.containsKey(playerUUID)) {
        Map<String, Object> permissions = claim.members.get(playerUUID);
        
        // Check role
        String role = (String) permissions.get("role");
        System.out.println("Player role: " + role);
        
        // Check specific permissions
        boolean canBreakBlocks = (Boolean) permissions.getOrDefault("break-blocks", false);
        boolean canPlaceBlocks = (Boolean) permissions.getOrDefault("place-blocks", false);
        boolean hasSetheHome = (Boolean) permissions.getOrDefault("sethome", false);
        
        System.out.println("Can break blocks: " + canBreakBlocks);
        System.out.println("Can place blocks: " + canPlaceBlocks);
        System.out.println("Has sethome permission: " + hasSetheHome);
    }
}
```

#### Listing All Members with Their Roles

```java
public void listClaimMembers(Claim claim) {
    System.out.println("=== Claim Members for: " + claim.name + " ===");
    
    for (Map.Entry<UUID, Map<String, Object>> entry : claim.members.entrySet()) {
        UUID memberUUID = entry.getKey();
        Map<String, Object> permissions = entry.getValue();
        String role = (String) permissions.get("role");
        
        String playerName = Bukkit.getOfflinePlayer(memberUUID).getName();
        System.out.println("- " + playerName + " (" + role + ")");
    }
}
```

#### Finding Members with Specific Permissions

```java
public void findMembersWithPermission(Claim claim, String permission) {
    System.out.println("Members with '" + permission + "' permission:");
    
    for (Map.Entry<UUID, Map<String, Object>> entry : claim.members.entrySet()) {
        UUID memberUUID = entry.getKey();
        Map<String, Object> permissions = entry.getValue();
        
        boolean hasPermission = (Boolean) permissions.getOrDefault(permission, false);
        if (hasPermission) {
            String playerName = Bukkit.getOfflinePlayer(memberUUID).getName();
            String role = (String) permissions.get("role");
            System.out.println("- " + playerName + " (" + role + ")");
        }
    }
}
```

#### Getting Members by Role

```java
import java.util.List;
import java.util.ArrayList;

public List<UUID> getMembersByRole(Claim claim, String targetRole) {
    List<UUID> membersWithRole = new ArrayList<>();
    
    for (Map.Entry<UUID, Map<String, Object>> entry : claim.members.entrySet()) {
        UUID memberUUID = entry.getKey();
        Map<String, Object> permissions = entry.getValue();
        String role = (String) permissions.get("role");
        
        if (targetRole.equals(role)) {
            membersWithRole.add(memberUUID);
        }
    }
    
    return membersWithRole;
}
```

#### Example: Integration with Other Systems

```java
// Example: Add members with farmer-storage permission to a farming system
public void syncWithFarmingSystem(Claim claim, FarmingSystem farmingSystem) {
    for (Map.Entry<UUID, Map<String, Object>> entry : claim.members.entrySet()) {
        UUID memberUUID = entry.getKey();
        Map<String, Object> permissions = entry.getValue();
        
        String role = (String) permissions.get("role");
        boolean hasFarmerStorage = (Boolean) permissions.getOrDefault("farmer-storage", false);
        
        // Add members with farmer-storage permission to farming system
        if ("member".equals(role) && hasFarmerStorage) {
            String playerName = Bukkit.getOfflinePlayer(memberUUID).getName();
            farmingSystem.addMember(memberUUID, playerName);
        }
    }
}
```

-----

## 📊 Claim Model Properties

The `Claim` class has the following properties:

```java
public class Claim {
    public int id;                     // Claim ID
    public String name;                // Claim name
    public String ownerName;           // Owner's name
    public String ownerUUID;           // Owner's UUID
    public String worldName;           // World name
    public int x, y, z;                // Coordinates
    public int chunk_x, chunk_z;       // Chunk coordinates
    public String effectType;          // Effect type
    public Timestamp createdAt;        // Creation date
    public double energy;              // Current energy
    public double maxEnergy;           // Maximum energy
    public String logWebhook;          // Discord webhook URL
    public boolean isMessageAlertEnabled;    // Is message alert active
    public boolean isSoundAlertEnabled;      // Is sound alert active
    public boolean isScreenMessageEnabled;   // Is screen message active
    public boolean isTimeHidden;             // Is time hidden
    public boolean isStreamerModeEnabled;    // Is streamer mode active
    public Map<UUID, Map<String, Object>> members; // Claim members with permissions
}
```

## 🔐 Available Permissions

The following permissions are available in the members' permission map:

### Roles
- `"role"` - Player's role in the claim (`"owner"`, `"member"`, etc.)

### Block Operations
- `"place-blocks"` - Place blocks
- `"break-blocks"` - Break blocks

### Spawner Management
- `"place-spawners"` - Place spawners
- `"break-spawners"` - Break spawners

### Lighting
- `"place-torches"` - Place torches
- `"break-torches"` - Break torches
- `"interact-beacon-effects"` - Interact with beacon effects

### Fluid Operations
- `"pour-water"` - Pour water
- `"pour-lava"` - Pour lava

### Storage and Inventory
- `"open-inventory-blocks"` - Open inventory blocks (chests, furnaces, etc.)
- `"break-inventory-blocks"` - Break inventory blocks
- `"place-hoppers"` - Place hoppers
- `"break-hoppers"` - Break hoppers
- `"farmer-storage"` - Access farmer storage

### Transportation
- `"place-minecarts"` - Place minecarts
- `"break-minecarts"` - Break minecarts
- `"place-boats"` - Place boats
- `"break-boats"` - Break boats

### Interaction
- `"open-doors"` - Open doors
- `"close-doors"` - Close doors
- `"interact-mobs"` - Interact with mobs
- `"interact-frames"` - Interact with item frames
- `"interact-triggers"` - Interact with triggers (buttons, pressure plates, etc.)
- `"interact-armor-stands"` - Interact with armor stands

### Player Management
- `"teleport-players"` - Teleport players
- `"sethome"` - Set home location

### Information
- `"see-time"` - View time information
