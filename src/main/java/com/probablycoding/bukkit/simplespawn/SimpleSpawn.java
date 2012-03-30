package com.probablycoding.bukkit.simplespawn;

import java.io.File;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class SimpleSpawn extends JavaPlugin
{
    private final SimpleSpawnListener listener = new SimpleSpawnListener(this);

    public Location getSpawnLocation()
    {
        // just in case someone edited the file
        reloadConfig();

        World world = getServer().getWorld(getConfig().getString("spawn-location.world"));
        if (world == null) {
            world = getServer().getWorlds().get(0);
        }

        return new Location(
                world,
                getConfig().getDouble("spawn-location.x"),
                getConfig().getDouble("spawn-location.y"),
                getConfig().getDouble("spawn-location.z"),
                (float)getConfig().getDouble("spawn-location.yaw"),
                (float)getConfig().getDouble("spawn-location.pitch"));
    }

    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(listener, this);

        File config = new File(getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
    {
        if (command.getName().equalsIgnoreCase("spawn")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("simplespawn.reload")) {
                    reloadConfig();
                    sender.sendMessage(ChatColor.GRAY + "Spawn configuration reloaded");
                    return true;
                }
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Console cannot teleport!");
                return true;
            }

            ((Player) sender).teleport(getSpawnLocation());
            return true;
        }

        if (command.getName().equalsIgnoreCase("setspawn")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Sorry, console cannot set spawn.");
                return true;
            }

            setSpawn((Player) sender);
            return true;
        }

        return false;
    }

    private void setSpawn(Player player)
    {
        Location loc = player.getLocation();

        getConfig().set("spawn-location.world", loc.getWorld().getName());
        getConfig().set("spawn-location.x",     loc.getX());
        getConfig().set("spawn-location.y",     loc.getY());
        getConfig().set("spawn-location.z",     loc.getZ());
        getConfig().set("spawn-location.yaw",   loc.getYaw());
        getConfig().set("spawn-location.pitch", loc.getPitch());
        saveConfig();

        loc.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        player.sendMessage(ChatColor.GRAY + "Spawn set at " + loc.getWorld().getName() + ": " +
                loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
    }
}
