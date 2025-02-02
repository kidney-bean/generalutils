package me.alecjensen.generalutils;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import me.alecjensen.generalutils.commands.*;
import me.alecjensen.generalutils.utils.DynamicCommands;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.*;

import static org.bukkit.Bukkit.getPluginManager;

public final class GeneralUtils extends JavaPlugin
{

    private static GeneralUtils instance;
    public PaperCommandManager commandManager;
    public FileConfiguration config = this.getConfig();
    public BanUtils banUtils = new BanUtils();
    public HashMap<UUID, Boolean> frozenPlayers = new HashMap<>();

    public static GeneralUtils getInstance()
    {
        return GeneralUtils.instance;
    }

    @Override
    public void onEnable()
    {
        GeneralUtils.instance = this;

        // bStats setup

        int pluginId = 14930;
        Metrics metrics = new Metrics(this, pluginId);

        PluginDescriptionFile pdf = getDescription();

        // TODO: Update config

        config.options().copyDefaults(true);
        saveConfig();

        // Registering commands and events with the server.

        commandManager = new PaperCommandManager(this);

        commandManager.registerCommand(new GeneralUtilsCommand(this));
        commandManager.registerCommand(new BringCommand(this));
        commandManager.registerCommand(new ToCommand(this));
        commandManager.registerCommand(new BackCommand());
        commandManager.registerCommand(new KickallCommand());
        commandManager.registerCommand(new StaffChatCommand());
        commandManager.registerCommand(new AnnounceCommand());
        commandManager.registerCommand(new BanUtils());
        commandManager.registerCommand(new SuicideCommand());
        commandManager.registerCommand(new ClearChatCommand());
        commandManager.registerCommand(new FreezeCommand(frozenPlayers));
        commandManager.registerCommand(new UnfreezeCommand(frozenPlayers));
        commandManager.registerCommand(new MuteCommand(this));
        commandManager.registerCommand(new UnmuteCommand(this));

        commandManager.getCommandCompletions().registerAsyncCompletion("frozenPlayers", c ->
        {
            List<String> frozenPlayerNames = new ArrayList<>();
            for (UUID uuid : frozenPlayers.keySet())
            {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null)
                {
                    frozenPlayerNames.add(player.getName());
                }
            }
            return frozenPlayerNames;
        });

        // Registering events

        getPluginManager().registerEvents(BackCommand.getInstance(), this);
        getPluginManager().registerEvents(BanUtils.getInstance(), this);
        getPluginManager().registerEvents(FreezeCommand.getInstance(), this);
        getPluginManager().registerEvents(MuteCommand.getInstance(), this);


        // TODO: Register or unregister the BanUtils command based on the config value

        // Checking if PAPI is installed

        if (getPluginManager().getPlugin("PlaceholderAPI") != null)
        {
            getLogger().info("PlaceholderAPI is installed! PAPI functionality will work.");
        } else
        {
            getLogger().warning("PlaceholderAPI not installed! PAPI functionality will not work.");
        }

        getLogger().info(ChatColor.GREEN + "GeneralUtils plugin by Alec Jensen");
        getLogger().info(ChatColor.GREEN + "Version: " + pdf.getVersion());
        getLogger().info(ChatColor.GREEN + getConfig().getString("config-version"));
    }

    @Override
    public void onDisable()
    {
        getLogger().info(ChatColor.GREEN + "Goodbye!");
    }
}
