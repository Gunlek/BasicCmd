package com.simpleduino.basiccmd.Messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.simpleduino.basiccmd.SQL.BasicCmdSQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Simple-Duino on 10/06/2016.
 * Copyrights Simple-Duino, all rights reserved
 */

public class MessageListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        if (subchannel.equals("BasicCmdKickPlayer")) {
            short len = in.readShort();
            byte[] msgBytes = new byte[len];
            in.readFully(msgBytes);

            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgBytes));
            String banned = null, reason = null;
            try {
                banned = msgin.readUTF();
                reason = msgin.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try
            {
                Player banPlayer = Bukkit.getPlayer(banned);
                banPlayer.kickPlayer(reason);
            }
            catch (Exception e)
            {
                //Le joueur bannis n'est pas sur ce serveur
            }
        }

        else if (subchannel.equals("GetPlayerUUID")) {
            short len = in.readShort();
            byte[] msgBytes = new byte[len];
            in.readFully(msgBytes);

            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgBytes));
            String bannedPlayer = null;
            try {
                bannedPlayer = msgin.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try
            {
                Player banPlayer = Bukkit.getPlayer(bannedPlayer);
                new BasicCmdSQL().addUUID(bannedPlayer, banPlayer.getUniqueId().toString());
                new BasicCmdSQL().addMutingUUID(bannedPlayer, banPlayer.getUniqueId());
                banPlayer.kickPlayer(ChatColor.RED + "Vous avez été éjecté du serveur");
            }
            catch (Exception e)
            {
                //Le joueur bannis n'est pas sur ce serveur
            }
        }

        else if (subchannel.equals("BroadcastInfoMessage")) {
            short len = in.readShort();
            byte[] msgBytes = new byte[len];
            in.readFully(msgBytes);

            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgBytes));
            String msg = null;
            try {
                msg = msgin.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }

            for(Player p : Bukkit.getOnlinePlayers())
            {
                p.sendMessage(msg);
            }
        }

        else if (subchannel.equals("BroadcastBasicCmdMessage")) {
            short len = in.readShort();
            byte[] msgBytes = new byte[len];
            in.readFully(msgBytes);

            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgBytes));
            String msg = null, perm = null;
            try {
                msg = msgin.readUTF();
                perm = msgin.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }

            for(Player p : Bukkit.getOnlinePlayers())
            {
                if(p.hasPermission(perm))
                {
                    p.sendMessage(msg);
                }
            }
        }
    }

}
