package arda.morkoc.api.model;

public class Claim {
    public int id;
    public String name;
    public String ownerName;
    public String ownerUUID;
    public String worldName;
    public int x, y, z, chunk_x, chunk_z;
    public String createdAt;
    public double energy, maxEnergy;
    public String logWebhook;
    public boolean isMessageAlertEnabled;
    public boolean isSoundAlertEnabled;
    public boolean isScreenMessageEnabled;
    public boolean isTimeHidden;
    public boolean isStreamerModeEnabled;


    public Claim(int id, String name, String ownerName, String ownerUUID, String worldName, int x, int y, int z, int chunk_x, int chunk_z, String createdAt, double energy, double maxEnergy, String logWebhook, boolean isMessageAlertEnabled, boolean isSoundAlertEnabled, boolean isScreenMessageEnabled, boolean isTimeHidden, boolean isStreamerModeEnabled) {
        this.id = id;
        this.name = name;
        this.ownerName = ownerName;
        this.ownerUUID = ownerUUID;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.chunk_x = chunk_x;
        this.chunk_z = chunk_z;
        this.createdAt = createdAt;
        this.energy = energy;
        this.maxEnergy = maxEnergy;
        this.logWebhook = logWebhook;
        this.isMessageAlertEnabled = isMessageAlertEnabled;
        this.isSoundAlertEnabled = isSoundAlertEnabled;
        this.isScreenMessageEnabled = isScreenMessageEnabled;
        this.isTimeHidden = isTimeHidden;
        this.isStreamerModeEnabled = isStreamerModeEnabled;
    }
}
