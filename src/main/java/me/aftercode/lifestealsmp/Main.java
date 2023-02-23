package me.aftercode.lifestealsmp;

import com.samjakob.spigui.SpiGUI;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import me.aftercode.lifestealsmp.bans.BanStorageUtil;
import me.aftercode.lifestealsmp.events.*;
import me.aftercode.lifestealsmp.other.*;
import me.aftercode.lifestealsmp.other.expansions.LSExpansion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

import static me.aftercode.lifestealsmp.commands.CommandsManager.loadCommands;
import static me.aftercode.lifestealsmp.other.Items.Recipes.registerRecipes;
import static me.aftercode.lifestealsmp.other.Items.Recipes.unregisterRecipes;

public final class Main extends JavaPlugin {

    public static SpiGUI spiGUI;
    private static Main instance;

    public static Main getInstance() {
        return Main.instance;
    }

    @Override
    public void onLoad() {
        Main.instance = this;
        CommandAPI.onLoad(new CommandAPIConfig().silentLogs(false));
        try {
            BanStorageUtil.loadBans();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadCommands();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        spiGUI = new SpiGUI(this);
        CommandAPI.onEnable(this);
        Metrics metrics = new Metrics(this, 15176);

        getConfig().options().copyDefaults();
        saveDefaultConfig();
        Config.Update();

        this.getServer().getPluginManager().registerEvents(new PlayerDeath(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
        this.getServer().getPluginManager().registerEvents(new AsyncPlayerPreLogin(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerQuit(), this);

        if (Config.getBoolean("loot.enabled")) {
            for (World world : Bukkit.getServer().getWorlds()) {
                world.getPopulators().removeIf(blockPopulator -> blockPopulator instanceof LootPopulator);
                world.getPopulators().add(new LootPopulator(this));
            }
        }

        registerRecipes();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new LSExpansion(this).register();
        }
        UpdateChecker.init();
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        unregisterRecipes();
        for (World world : Bukkit.getServer().getWorlds()) {
            world.getPopulators().removeIf(blockPopulator -> blockPopulator instanceof LootPopulator);
        }
    }
}
