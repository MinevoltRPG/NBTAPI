package fr.galaxyoyo.spigot.nbtapi;

import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.IOException;

public class NBTAPI extends JavaPlugin {
    @Override
    public void onEnable() {
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            getLogger().warning("Error while sending data to Metrics :(");
            e.printStackTrace();
        }
        getLogger().info("NBTAPI enabled! Thanks for downloading! This little API was made by galaxyoyo.");
    }
}
