package com.simpleduino.basiccmd.Commands;

import com.simpleduino.basiccmd.SQL.BasicCmdSQL;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Simple-Duino on 17/06/2016.
 * Copyrights Simple-Duino, all rights reserved
 */

public class UnmuteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender.hasPermission("BasicCmd.admin.unmute")) {
            if(args.length >= 1)
            {
                new BasicCmdSQL().unmute(args[0]);
                try
                {
                    Player p = Bukkit.getPlayer(args[0]);
                    new BasicCmdSQL().unmute(p.getUniqueId());
                }
                catch(Exception e)
                {

                }
                sender.sendMessage(ChatColor.DARK_GREEN + "Le joueur a été démute");
            }
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'executer cette commande");
        }
        return false;
    }
}
