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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Simple-Duino on 17/06/2016.
 * Copyrights Simple-Duino, all rights reserved
 */

public class TempBanCommand implements CommandExecutor {

    private BasicCmdSQL basicCmdSQL = new BasicCmdSQL();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender.hasPermission("BasicCmd.admin.tempban")) {
            if (args.length >= 3) {
                String bannedPlayer = args[0];
                if(!bannedPlayer.equalsIgnoreCase(sender.getName())) {
                    String duration = args[1];
                    Pattern pattern = Pattern.compile("[a-zA-Z]");
                    Matcher matcher = pattern.matcher(duration);
                    String durationType = "";
                    if(matcher.find())
                    {
                        durationType = matcher.group();
                    }
                    duration = duration.replace(durationType, "");
                    int finalDuration = Integer.parseInt(duration);
                    String durationSuffix = " secondes";
                    switch(durationType)
                    {
                        case "s":
                            finalDuration=Integer.parseInt(duration);
                            durationSuffix = " seconde(s)";
                            break;
                        case "m":
                            finalDuration=Integer.parseInt(duration)*60;
                            durationSuffix = " minute(s)";
                            break;
                        case "h":
                            finalDuration=Integer.parseInt(duration)*3600;
                            durationSuffix = " heure(s)";
                            break;
                        case "D":
                            finalDuration=Integer.parseInt(duration)*86400;
                            durationSuffix = " jour(s)";
                            break;
                        case "M":
                            finalDuration=Integer.parseInt(duration)*2678400;
                            durationSuffix = " mois";
                            break;
                        case "Y":
                            finalDuration=Integer.parseInt(duration)*31536000;
                            durationSuffix = " année(s)";
                            break;
                    }
                    String reason = "";
                    for (int i = 2; i < args.length; i++) {
                        if(i!=2)
                            reason += " "+args[i];
                        else
                            reason+=args[i];
                    }
                    reason = reason.replace("&", "§");
                    String groupPrefix = "";
                    if(sender instanceof Player) {
                        Player p = (Player)sender;
                        groupPrefix = BasicCmdPlugin.chat.getGroupPrefix(p.getWorld(), BasicCmdPlugin.permission.getPrimaryGroup(p));
                        groupPrefix = groupPrefix.replace("&", "§");
                    }
                    basicCmdSQL.banPlayer(bannedPlayer, finalDuration, reason);
                    String message = ChatColor.GOLD.toString()+ChatColor.BOLD + "["+ChatColor.RED+"Tempban"+ChatColor.GOLD+"] " +ChatColor.RESET+ groupPrefix + sender.getName() + ChatColor.GOLD + " a banni " + ChatColor.RED + bannedPlayer + ChatColor.GOLD + " "+duration+durationSuffix+" pour \""+ChatColor.RED + reason+ChatColor.GOLD+"\"";
                    if (BasicCmdPlugin.getPlugin(BasicCmdPlugin.class).getServer().getServerName().toLowerCase().contains("lobby")) {
                        for (Player p1 : Bukkit.getOnlinePlayers()) {
                            if (p1.hasPermission("BasicCmd.view.tempban")) {
                                p1.sendMessage(message);
                            }
                        }
                    }
                    new CustomMessageSender("ALL", "BroadcastBasicCmdMessage", new String[]{message, "BasicCmd.view.tempban"});
                    try {
                        Player p = Bukkit.getPlayer(bannedPlayer);
                        basicCmdSQL.addUUID(bannedPlayer, p.getUniqueId().toString());
                        p.kickPlayer(org.bukkit.ChatColor.RED + "Vous avez été banni temporairement du serveur pour " + duration+durationSuffix+"\n"+reason);
                    } catch (Exception e) {
                        new CustomMessageSender("ALL", "GetPlayerUUID", new String[]{bannedPlayer});
                        new CustomMessageSender("ALL", "BasicCmdKickPlayer", new String[]{bannedPlayer, "Vous avez été banni temporairement du serveur pour " + duration+durationSuffix+"\n"+reason});
                    }
                    sender.sendMessage(ChatColor.DARK_GREEN + "Le joueur a été banni");
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Vous ne pouvez pas vous bannir vous même");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Erreur: Syntaxe incorrecte, utilisez /tempban <pseudo> <durée[s:m:h:D:M:Y]> <raison>");
            }
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'executer cette commande");
        }
        return false;
    }
}
