package com.simpleduino.basiccmd.Commands;

import com.simpleduino.basiccmd.SQL.BasicCmdSQL;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Simple-Duino on 17/06/2016.
 * Copyrights Simple-Duino, all rights reserved
 */

public class UnbanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender.hasPermission("BasicCmd.admin.unban"))
        {
            if(args.length >= 1)
            {
                if(new BasicCmdSQL().isBanned(args[0]))
                {
                    new BasicCmdSQL().unban(args[0]);
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Le joueur est introuvable ou n'est pas bannis");
                }
                sender.sendMessage(ChatColor.DARK_GREEN + "Vous avez débanni "+args[0]);
            }
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'executer cette commande");
        }
        return false;
    }
}
