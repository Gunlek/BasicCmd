package com.simpleduino.basiccmd.Commands;

import com.simpleduino.basiccmd.BasicCmdPlugin;
import com.simpleduino.basiccmd.Messaging.CustomMessageSender;
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

public class BanCommand implements CommandExecutor {

    private BasicCmdSQL basicCmdSQL = new BasicCmdSQL();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender.hasPermission("BasicCmd.admin.ban")) {
            if (args.length >= 2) {
                String bannedPlayer = args[0];
                if(!bannedPlayer.equalsIgnoreCase(sender.getName())) {
                    String reason = "";
                    for (int i = 1; i < args.length; i++) {
                        if(i!=1)
                            reason += " "+args[i];
                        else
                            reason+=args[i];
                    }
                    reason = reason.replace("&", "§");
                    basicCmdSQL.banPlayer(bannedPlayer, -1, reason);
                    //Bukkit.getServer().getBanList(BanList.Type.NAME).addBan(bannedPlayer, reason, null, sender.getName());
                    try {
                        Player p = Bukkit.getPlayer(bannedPlayer);
                        basicCmdSQL.addUUID(bannedPlayer, p.getUniqueId().toString());
                        p.kickPlayer(org.bukkit.ChatColor.RED + "Vous avez été banni définitivement du serveur\n"+reason);
                    } catch (Exception e) {
                        new CustomMessageSender("ALL", "GetPlayerUUID", new String[]{bannedPlayer});
                        new CustomMessageSender("ALL", "BasicCmdKickPlayer", new String[]{bannedPlayer, "Vous avez été banni définitivement du serveur\n"+reason});
                    }
                    String groupPrefix = "";
                    if(sender instanceof Player) {
                        Player p = (Player)sender;
                        groupPrefix = BasicCmdPlugin.chat.getGroupPrefix(p.getWorld(), BasicCmdPlugin.permission.getPrimaryGroup(p));
                        groupPrefix = groupPrefix.replace("&", "§");
                    }
                    String message = ChatColor.GOLD.toString()+ChatColor.BOLD + "["+ChatColor.RED+"Ban"+ChatColor.GOLD+"] " +ChatColor.RESET+ groupPrefix + sender.getName() + ChatColor.GOLD + " a banni " + ChatColor.RED + bannedPlayer + ChatColor.GOLD + " pour \""+ChatColor.RED + reason+ChatColor.GOLD+"\"";
                    if (BasicCmdPlugin.getPlugin(BasicCmdPlugin.class).getServer().getServerName().toLowerCase().contains("lobby")) {
                        for (Player p1 : Bukkit.getOnlinePlayers()) {
                            if (p1.hasPermission("BasicCmd.view.ban")) {
                                p1.sendMessage(message);
                            }
                        }
                    }
                    new CustomMessageSender("ALL", "BroadcastBasicCmdMessage", new String[]{message, "BasicCmd.view.ban"});
                    sender.sendMessage(ChatColor.DARK_GREEN + "Le joueur a été banni");
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Vous ne pouvez pas vous bannir vous-même");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Erreur: Syntaxe incorrecte, utilisez /ban <pseudo> <raison>");
            }
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'executer cette commande");
        }
        return false;
    }
}
