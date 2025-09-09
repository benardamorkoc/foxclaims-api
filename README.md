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
    public Map<UUID, Map<String, Object>> members; // Claim members
}
```