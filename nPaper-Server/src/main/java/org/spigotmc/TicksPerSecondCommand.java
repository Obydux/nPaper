package org.spigotmc;

import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TicksPerSecondCommand extends Command
{

    public TicksPerSecondCommand(String name)
    {
        super( name );
        this.description = "Gets the current ticks per second for the server";
        this.usageMessage = "/tps";
        this.setPermission( "bukkit.command.tps" );
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        Runtime runtime = Runtime.getRuntime();
        double usedMemory = runtime.totalMemory() - runtime.freeMemory();
        double freeMemory = runtime.maxMemory() - usedMemory;

        sender.sendMessage(ChatColor.GRAY + "Uptime: " + formatFullMillis((System.currentTimeMillis() - MinecraftServer.START_TIME) / 1000));
        sender.sendMessage(ChatColor.GRAY + "Memory: " + formatMemory(usedMemory) + ChatColor.GRAY + "/" + formatMemory(runtime.maxMemory()) + ChatColor.GRAY + " (" + formatMemory(freeMemory) + " free" + ChatColor.GRAY + ")");


        // PaperSpigot start - Further improve tick handling
        double[] tps = Bukkit.spigot().getTPS();
        String[] tpsAvg = new String[tps.length];

        for ( int i = 0; i < tps.length; i++) {
            tpsAvg[i] = format( tps[i] );
        }

        sender.sendMessage(ChatColor.GRAY + "TPS from last 1m, 5m, 15m: " + StringUtils.join(tpsAvg, ChatColor.GRAY + ", "));
        sender.sendMessage(ChatColor.GRAY + "Last Tick Time: " + ChatColor.LIGHT_PURPLE + (System.currentTimeMillis() - MinecraftServer.LAST_TICK_TIME_MILLIS) + ChatColor.GRAY + "ms, " + ChatColor.LIGHT_PURPLE + (((double)(System.nanoTime() - MinecraftServer.LAST_TICK_TIME_NANO)) / 1000000) +  ChatColor.GRAY + "ns -> ms");
        sender.sendMessage(ChatColor.GRAY + "Online Players: " + ChatColor.LIGHT_PURPLE + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
        // PaperSpigot end

        return true;
    }

    private static String format(double tps) {  // PaperSpigot - made static
        return ((tps > 18.0) ? ChatColor.GREEN : (tps > 16.0) ? ChatColor.YELLOW : ChatColor.RED).toString() + (( tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
    }

    private static String formatMemory(double mem) {
        return ChatColor.LIGHT_PURPLE.toString() + Math.round(mem / 1024.0D / 1024.0D) + "MB";
    }

    private static String formatFullMillis(Long millisTime) {

        long seconds = millisTime % 60;
        long minutes = (millisTime / 60) % 60;
        long hours = (millisTime / (60 * 60)) % 24;
        long days = (millisTime / (60 * 60 * 24)) % 24;

        String format = "";
        if (days >= 1.0D) {
            format += ChatColor.LIGHT_PURPLE.toString() + days + ChatColor.GRAY + "d ";
        }
        if (hours >= 1.0D) {
            format += ChatColor.LIGHT_PURPLE.toString() + hours + ChatColor.GRAY + "h ";
        }
        if (minutes >= 1.0D) {
            format += ChatColor.LIGHT_PURPLE.toString() + minutes + ChatColor.GRAY + "min ";
        }
        return format + ChatColor.LIGHT_PURPLE.toString() + seconds + ChatColor.GRAY + "s";
    }
}
