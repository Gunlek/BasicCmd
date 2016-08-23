package com.simpleduino.basiccmd;

import com.simpleduino.basiccmd.Commands.*;
import com.simpleduino.basiccmd.Listeners.PlayerListener;
import com.simpleduino.basiccmd.Messaging.MessageListener;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Created by Simple-Duino on 17/06/2016.
 * Copyrights Simple-Duino, all rights reserved
 */

public class BasicCmdPlugin extends JavaPlugin {

    public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;
    private File cfgFile = new File("plugins/BasicCmd/config.yml");

    public void onEnable()
    {
        if(!cfgFile.exists()) {
            cfgFile.getParentFile().mkdirs();
            try {
                cfgFile.createNewFile();
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(cfgFile);
                cfg.set("sql.hostname", "localhost");
                cfg.set("sql.database", "relationship");
                cfg.set("sql.username", "user");
                cfg.set("sql.password", "password");
                cfg.set("basiccmd.info.prefix", "[Information]");
                try {
                    cfg.save(cfgFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.getServer().getPluginCommand("ban").setExecutor(new BanCommand());
        this.getServer().getPluginCommand("tempban").setExecutor(new TempBanCommand());
        this.getServer().getPluginCommand("mute").setExecutor(new MuteCommand());
        this.getServer().getPluginCommand("unmute").setExecutor(new UnmuteCommand());
        this.getServer().getPluginCommand("unban").setExecutor(new UnbanCommand());
        this.getServer().getPluginCommand("info").setExecutor(new InfoCommand());

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener());

        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        this.setupPermissions();
        this.setupChat();
        this.setupEconomy();
    }

    public void onDisable()
    {

    }

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private boolean setupChat()
    {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
}
