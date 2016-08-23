package com.simpleduino.basiccmd.Listeners;

import com.simpleduino.basiccmd.SQL.BasicCmdSQL;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Simple-Duino on 17/06/2016.
 * Copyrights Simple-Duino, all rights reserved
 */

public class PlayerListener implements Listener {

    private BasicCmdSQL sql = new BasicCmdSQL();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        final Player p = e.getPlayer();
        if(!p.isOp()) {
            this.sql.addUUID(e.getPlayer().getName(), e.getPlayer().getUniqueId().toString());
            if (this.sql.isBanned(p.getUniqueId())) {
                Date current = Calendar.getInstance().getTime();
                Date bandate = this.sql.getBanDate(e.getPlayer().getUniqueId()).getTime();
                int difference = (int) (current.getTime() - bandate.getTime()) / 1000;
                if (this.sql.getBanDuration(p.getUniqueId()) == -1) {
                    p.kickPlayer(ChatColor.RED + "Vous avez été bannis définitivement du serveur\n" + this.sql.getBanReason(p.getName()));
                } else if (difference <= this.sql.getBanDuration(p.getUniqueId())) {
                    p.kickPlayer(ChatColor.RED + "Vous avez été banni temporairement du serveur, il reste " + Integer.toString(this.sql.getBanDuration(p.getUniqueId()) - difference) + " secondes\n" + this.sql.getBanReason(p.getName()));
                } else {
                    this.sql.unban(e.getPlayer().getUniqueId());
                }
            } else if (this.sql.isBanned(p.getName())) {
                Date current = Calendar.getInstance().getTime();
                Date bandate = this.sql.getBanDate(e.getPlayer().getUniqueId()).getTime();
                int difference = (int) (current.getTime() - bandate.getTime()) / 1000;
                if (this.sql.getBanDuration(p.getUniqueId()) == -1) {
                    this.sql.addUUID(p.getName(), p.getUniqueId().toString());
                    p.kickPlayer(ChatColor.RED + "Vous avez été bannis définitivement du serveur\n" + this.sql.getBanReason(p.getName()));
                } else if (difference <= this.sql.getBanDuration(p.getUniqueId())) {
                    this.sql.addUUID(p.getName(), p.getUniqueId().toString());
                    p.kickPlayer(ChatColor.RED + "Vous avez été banni temporairement du serveur, il reste " + Integer.toString(this.sql.getBanDuration(p.getUniqueId()) - difference) + " secondes\n" + this.sql.getBanReason(p.getName()));
                } else {
                    this.sql.unban(e.getPlayer().getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e)
    {
        Player p = e.getPlayer();

        if(!p.isOp()) {
            this.sql.addMutingUUID(e.getPlayer().getName(), e.getPlayer().getUniqueId());

            if (this.sql.isMuted(p.getUniqueId())) {
                e.setCancelled(true);
                Date current = Calendar.getInstance().getTime();
                Date muteDate = new BasicCmdSQL().getMuteDate(e.getPlayer().getUniqueId()).getTime();
                int muteDif = (int) (current.getTime() - muteDate.getTime()) / 1000;
                if (this.sql.getMuteDuration(p.getUniqueId()) == -1) {
                    p.sendMessage(ChatColor.RED + "Vous avez été mute définitivement par un membre du staff");
                    p.sendMessage(ChatColor.RED + "Raison: " + this.sql.getMuteReason(p.getUniqueId()));
                } else if (muteDif <= this.sql.getMuteDuration(p.getUniqueId())) {
                    p.sendMessage(ChatColor.RED + "Vous avez été mute temporairement, il reste " + Integer.toString(this.sql.getMuteDuration(p.getUniqueId()) - muteDif) + " secondes");
                    p.sendMessage(ChatColor.RED + "Raison: " + this.sql.getMuteReason(p.getUniqueId()));
                } else {
                    this.sql.unmute(e.getPlayer().getUniqueId());
                }
            }
        }
    }
}
