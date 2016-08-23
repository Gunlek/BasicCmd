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

public class MuteCommand implements CommandExecutor {

    private BasicCmdSQL basicCmdSQL = new BasicCmdSQL();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender.hasPermission("BasicCmd.admin.mute")) {
            if (args.length >= 2) {
                String mutedPlayer = args[0];
                if(!mutedPlayer.equalsIgnoreCase(sender.getName())) {
                    int finalDuration = -1;
                    String reason = "";
                    String duration = "";
                    String durationSuffix="";
                    try {
                        duration = args[1];
                        Pattern pattern = Pattern.compile("[a-zA-Z]");
                        Matcher matcher = pattern.matcher(duration);
                        String durationType = "";
                        if(matcher.find())
                        {
                            durationType = matcher.group();
                        }
                        duration = duration.replace(durationType, "");
                        finalDuration = Integer.parseInt(duration);
                        durationSuffix = " secondes";
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
                        for (int i = 2; i < args.length; i++) {
                            if(i!=2)
                                reason += " "+args[i];
                            else
                                reason += args[i];
                        }
                        reason = reason.replace("&", "§");
                    } catch (Exception e) {
                        for (int i = 1; i < args.length; i++) {
                            if(i!=1)
                                reason += " "+args[i];
                            else
                                reason += args[i];
                        }
                        reason = " "+reason.replace("&", "§");
                    }
                    basicCmdSQL.mutePlayer(mutedPlayer, finalDuration, reason);
                    try {
                        Player p = Bukkit.getPlayer(mutedPlayer);
                        basicCmdSQL.addMutingUUID(p.getName(), p.getUniqueId());
                    } catch (Exception e) {

                    }
                    if (finalDuration == -1)
                        sender.sendMessage(ChatColor.DARK_GREEN + "Le joueur a été muté");
                    else
                        sender.sendMessage(ChatColor.DARK_GREEN + "Le joueur a été muté pour " + Integer.toString(finalDuration) + " secondes");
                    String groupPrefix = "";
                    if(sender instanceof Player) {
                        Player p = (Player)sender;
                        groupPrefix = BasicCmdPlugin.chat.getGroupPrefix(p.getWorld(), BasicCmdPlugin.permission.getPrimaryGroup(p));
                        groupPrefix = groupPrefix.replace("&", "§");
                    }
                    String message = "";
                    if (finalDuration == -1)
                        message = ChatColor.GOLD.toString()+ChatColor.BOLD + "["+ChatColor.RED+"Mute"+ChatColor.GOLD+"] " +ChatColor.RESET+ groupPrefix + sender.getName() + ChatColor.GOLD + " a muté " + ChatColor.RED + mutedPlayer + ChatColor.GOLD + " pour \""+ChatColor.RED + reason+ChatColor.GOLD+"\"";
                    else
                    message = ChatColor.GOLD.toString()+ChatColor.BOLD + "["+ChatColor.RED+"Mute"+ChatColor.GOLD+"] " +ChatColor.RESET+ groupPrefix + sender.getName() + ChatColor.GOLD + " a muté " + ChatColor.RED + mutedPlayer + ChatColor.GOLD + " "+duration+durationSuffix+" pour \""+ChatColor.RED + reason+ChatColor.GOLD+"\"";
                    if (BasicCmdPlugin.getPlugin(BasicCmdPlugin.class).getServer().getServerName().toLowerCase().contains("lobby")) {
                        for (Player p1 : Bukkit.getOnlinePlayers()) {
                            if (p1.hasPermission("BasicCmd.view.mute")) {
                                p1.sendMessage(message);
                            }
                        }
                    }
                    new CustomMessageSender("ALL", "BroadcastBasicCmdMessage", new String[]{message, "BasicCmd.view.mute"});
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Vous ne pouvez pas vous mute vous même");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Erreur: Syntaxe incorrecte, utilisez /mute <pseudo> <durée> <raison> ou /mute <pseudo> <raison>");
            }
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'executer cette commande");
        }
        return false;
    }
}
