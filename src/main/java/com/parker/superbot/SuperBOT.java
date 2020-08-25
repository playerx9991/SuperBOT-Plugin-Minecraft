package com.parker.superbot;

import com.parker.superbot.files.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class SuperBOT extends JavaPlugin implements Listener {

    public static SuperBOT INSTANCE;

    public SuperBOT() {
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        // Don't log disabling, Spigot does that for you automatically!
    }

    @Override
    public void onEnable() {
        try {
            new DiscordBot().start();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            new FileManager().checkFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        getCommand("example").setExecutor(new ExampleCommand(this));
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public static void playerJoinEvent(PlayerJoinEvent event) {
        event.setJoinMessage("Test Player Joined");
    }

    @EventHandler
    public static void playerChat(AsyncPlayerChatEvent event) {
        new DiscordBot().sendChatMessage(event.getPlayer().getDisplayName(), event.getMessage());
    }

    public void runCommand(String cmd) {
        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
        });
    }
}
