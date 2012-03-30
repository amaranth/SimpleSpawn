package com.probablycoding.bukkit.simplespawn;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;


public class SimpleSpawnListener implements Listener
{
    private final SimpleSpawn plugin;

    public SimpleSpawnListener(SimpleSpawn instance)
    {
        plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    player.teleport(plugin.getSpawnLocation());
                }
            }, 1);

            if (plugin.getConfig().getBoolean("show-join-message")) {
                String message = plugin.getConfig().getString("join-message");
                message = message.replaceAll("%u", player.getName());
                message = ChatColor.translateAlternateColorCodes('&', message);

                if (event.getJoinMessage() != null && event.getJoinMessage().length() != 0) {
                    plugin.getServer().broadcastMessage(message);
                }
            }
        }
    }
        
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        if (!event.isBedSpawn() || !plugin.getConfig().getBoolean("allow-bed-spawn"))
            event.setRespawnLocation(plugin.getSpawnLocation());
    }
}
