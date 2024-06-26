package org.bukkit.command.defaults;

import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;

public class ListCommand extends VanillaCommand {
    public ListCommand() {
        super("list");
        this.description = "Lists all online players";
        this.usageMessage = "/list";
        this.setPermission("bukkit.command.list");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        StringJoiner playerJoiner = new StringJoiner(", ");

        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        for (Player player : players) {
            // If a player is hidden from the sender don't show them in the list
            if (sender instanceof Player && !((Player) sender).canSee(player)) continue;
            playerJoiner.add(player.getDisplayName());
        }

        sender.sendMessage("There are " + players.size() + "/" + Bukkit.getMaxPlayers() + " players online:\n" + playerJoiner.toString());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        return ImmutableList.of();
    }
}
