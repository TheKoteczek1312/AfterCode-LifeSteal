package me.aftercode.lifestealsmp.other;

import me.aftercode.lifestealsmp.Main;
import me.aftercode.lifestealsmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

// From: https://www.spigotmc.org/wiki/creating-an-update-checker-that-checks-for-updates
public class UpdateChecker {

    private final JavaPlugin plugin;

    public UpdateChecker(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static void init() {
        new UpdateChecker(Main.getInstance()).getVersion(version -> {
            if (Main.getInstance().getDescription().getVersion().equals(version)) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------AfterCode-LifeSteal-" + Main.getInstance().getDescription().getVersion() + "--------");
                if (Main.getInstance().getDescription().getVersion().contains("Alpha") || Main.getInstance().getDescription().getVersion().contains("Beta")) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- DO NOT USE THIS PLUGIN IN PRODUCTION!");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- SOME FEATURES ARE NOT FINISHED YET!");
                }
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "- You are up to date.");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- Thank you for using my plugin!");
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------AfterCode-LifeSteal-" + Main.getInstance().getDescription().getVersion() + "--------");

            } else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------AfterCode-LifeSteal-" + Main.getInstance().getDescription().getVersion() + "--------");
                if (Main.getInstance().getDescription().getVersion().contains("Alpha") || Main.getInstance().getDescription().getVersion().contains("Beta")) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- DO NOT USE THIS PLUGIN IN PRODUCTION!");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- SOME FEATURES ARE NOT FINISHED YET!");
                }
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "- There is a newer version than yours! (" + version + ")");
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "- Please download new version from SpigotMC or Github.");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- Thank you for using my plugin!");
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------AfterCode-LifeSteal-" + Main.getInstance().getDescription().getVersion() + "--------");
            }
        });

        Main.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {
            new UpdateChecker(Main.getInstance()).getVersion(version -> {
                if (!Main.getInstance().getDescription().getVersion().equals(version)) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------AfterCode-LifeSteal-" + Main.getInstance().getDescription().getVersion() + "--------");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "- A NEW UPDATE HAS BEEN RELEASED! (" + version + ")");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "- Please download new version from SpigotMC or Github.");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------AfterCode-LifeSteal-" + Main.getInstance().getDescription().getVersion() + "--------");
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if (player.hasPermission("lifesteal.update") || player.isOp()) {
                            player.sendMessage("§a§lAfterCode-LifeSteal §7§l> §c§lA NEW UPDATE HAS BEEN RELEASED! §6(" + version + ")");
                        }
                    });
                }
            });
        }, 0L, 36000L);
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            // make api get request to https://api.spigotmc.org/simple/0.2/index.php?action=getResource&id=101967 then get response json
            try {
                URL url = new URL("https://api.github.com/repos/dewPrzemuS/P-LifeSteal/releases");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                String response = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                JSONParser parser = new JSONParser();
                JSONArray array = (JSONArray) parser.parse(response);
                JSONObject json = (JSONObject) array.get(0);
                consumer.accept(String.valueOf(json.get("tag_name")));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }
}
 