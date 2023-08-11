package com.corderun.cordemobreward;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CordeMobReward extends JavaPlugin implements Listener {

    private FileConfiguration config;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.config = this.getConfig();
        this.config.options().copyDefaults(true);
        this.saveConfig();
        this.getCommand("cordemobreward").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
                if (!sender.hasPermission("cordemobreward.reload")) {
                    sender.sendMessage(Objects.requireNonNull(getConfig().getString("messages.no-perm")).replace("&", "§"));
                    return true;
                }
                if(args.length == 0){
                    sender.sendMessage("/cordemobreward reload");
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    reloadConfig();
                    sender.sendMessage(getConfig().getString("messages.reload").replace("&", "§"));
                    return true;
                }
                return true;
            }
        });
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            String mobName = event.getEntityType().toString();
            if (config.contains("mobs." + mobName)) {
                String command = config.getString("mobs." + mobName + ".command");
                int amount = config.getInt("mobs." + mobName + ".amount");
                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), command.replace("%player%", event.getEntity().getKiller().getName()).replace("%amount%", String.valueOf(amount)));
                event.getEntity().getKiller().sendActionBar(getConfig().getString("messages.reward").replace("&", "§").replace("%amount%", String.valueOf(amount)));
            }
        }
    }
}
