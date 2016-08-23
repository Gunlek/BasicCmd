package com.simpleduino.basiccmd.Messaging;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.simpleduino.basiccmd.BasicCmdPlugin;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Simple-Duino on 12/06/2016.
 * Copyrights Simple-Duino, all rights reserved
 */

public class CustomMessageSender {

    public CustomMessageSender(String servers, String subchannel, String[] data)
    {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF(servers);
        out.writeUTF(subchannel);

        ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgBytes);
        for(String s : data)
        {
            try {
                msgout.writeUTF(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        out.writeShort(msgBytes.toByteArray().length);
        out.write(msgBytes.toByteArray());

        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        player.sendPluginMessage(BasicCmdPlugin.getPlugin(BasicCmdPlugin.class), "BungeeCord", out.toByteArray());
    }
}
