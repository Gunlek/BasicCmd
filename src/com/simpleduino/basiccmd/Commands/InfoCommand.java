package com.simpleduino.basiccmd.Commands;

import com.simpleduino.basiccmd.Messaging.CustomMessageSender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * Created by Simple-Duino on 07/07/2016.
 * Copyrights Simple-Duino, all rights reserved
 */

public class InfoCommand implements CommandExecutor {

    private File cfgFile = new File("plugins/BasicCmd/config.yml");
    private YamlConfiguration cfg = YamlConfiguration.loadConfiguration(cfgFile);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender.hasPermission("BasicCmd.info"))
        {
            if(args.length >= 1)
            {
                String msg = "";
                for(int i=0;i<args.length;i++)
                {
                    if(i!=0)
                        msg+=" "+args[i];
                    else
                        msg+=args[i];
                }

                msg=msg.replace("&", "§");
                String message = cfg.get("basiccmd.info.prefix").toString().replace("&", "§")+" "+ChatColor.RESET+msg;

                for(Player p : Bukkit.getOnlinePlayers())
                {
                    p.sendMessage(message);
                }
                new CustomMessageSender("ALL", "BroadcastInfoMessage", new String[]{message});
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "Erreur: Syntaxe incorrecte, utilisez /info <information>");
            }
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'executer cette commande");
        }
        return false;
    }
}
