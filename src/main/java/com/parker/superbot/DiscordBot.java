package com.parker.superbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.security.auth.login.LoginException;
import java.nio.channels.Channel;
import java.util.concurrent.ExecutionException;

public class DiscordBot extends ListenerAdapter {

    public static JDA jda;

    public void start() throws LoginException, InterruptedException {
        // args[0] should be the token
        // We only need 2 intents in this bot. We only respond to messages in guilds and private channels.
        // All other events will be disabled.
         JDA jdabuilder = JDABuilder.createLight("ODc4NzAyODkwMzQ3Mjg2NTM4.YSFB1g.dMDHHTod50-hKHUI1Eif4vAP5lA", GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new DiscordBot())
                .setActivity(Activity.playing("Test Server"))
                .build();
         jda = jdabuilder;

         upsertCommands(jda);

        jda.getTextChannelsByName("minecraft-chat", true).get(0).sendMessage("Server Is Up").queue();
    }

    public void upsertCommands(JDA jda) {
        jda.upsertCommand("time", "Set Time in The Server").addOption(OptionType.STRING, "time", "Time to set").queue();
        // .addSubcommands(new SubcommandData("set", "Set Time"))
        jda.upsertCommand("ping", "CalCulates Bots Ping").queue();
        jda.upsertCommand("ban", "Bans A Player on The Server").addOption(OptionType.STRING, "player", "Player's Name to ban").queue();
        jda.upsertCommand("pardon", "UnBans A Player on The Server").addOption(OptionType.STRING, "player", "Player's Name to unban").queue();
        jda.upsertCommand("op", "OP A Player on The Server").addOption(OptionType.STRING, "player", "Player's Name to OP").queue();
        jda.upsertCommand("deop", "DEOP A Player on The Server").addOption(OptionType.STRING, "player", "Player's Name to DEOP").queue();
        jda.upsertCommand("stop", "Stops The Server").queue();
        jda.upsertCommand("restart", "Restarts The Server").queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        Message msg = event.getMessage();

        if (event.getAuthor().getId().equals("878702890347286538")) return;

        if (event.getChannel().getName().equals("minecraft-chat")) {
            Bukkit.broadcastMessage("[Discord] <" + event.getAuthor().getName() + "> " + msg.getContentRaw());
        }
    }

    public void sendChatMessage(String ign, String msg) {
        jda.getTextChannelsByName("minecraft-chat", true).get(0).sendMessage("[Minecraft] <" + ign + "> " + msg).queue();
    }


    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        if (event.getName().equals("time")) {
            if (event.getOption("time").getAsString().equals("")) return;
            SuperBOT.INSTANCE.runCommand("time set " + event.getOption("time").getAsString());
            event.reply("Time has been set").queue();
        }
        if(event.getName().equals("ping")) {
            long time = System.currentTimeMillis();
            event.reply("Pong!") // reply or acknowledge
                    .flatMap(v ->
                            event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time) // then edit original
                    ).queue(); // Queue both reply and edit
        }
        if (event.getName().equals("ban")) {
            if (event.getOption("player").getAsString().equals("")) return;
            SuperBOT.INSTANCE.runCommand("ban " + event.getOption("player").getAsString());
            event.reply(event.getOption("player").getAsString() + " Has Been Banned").queue();
        }
        if (event.getName().equals("pardon")) {
            if (event.getOption("player").getAsString().equals("")) return;
            SuperBOT.INSTANCE.runCommand("pardon " + event.getOption("player").getAsString());
            event.reply(event.getOption("player").getAsString() + " Has Been Pardoned").queue();
        }
        if (event.getName().equals("op")) {
            if (event.getOption("player").getAsString().equals("")) return;
            SuperBOT.INSTANCE.runCommand("op " + event.getOption("player").getAsString());
            event.reply("Player Has Been OPed").queue();
        }
        if (event.getName().equals("deop")) {
            if (event.getOption("player").getAsString().equals("")) return;
            SuperBOT.INSTANCE.runCommand("deop " + event.getOption("player").getAsString());
            event.reply(event.getOption("player").getAsString() + " Has Been DEOPed").queue();
        }
        if (event.getName().equals("stop")) {
            event.reply("Server Has Been Stopped").queue();
            SuperBOT.INSTANCE.runCommand("stop");
        }
        if (event.getName().equals("restart")) {
            event.reply("Server Is Restarting").queue();
            SuperBOT.INSTANCE.runCommand("restart");
        }
    }
}
