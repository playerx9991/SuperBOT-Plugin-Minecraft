package com.parker.superbot;

import com.parker.superbot.files.FileManager;
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
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class DiscordBot extends ListenerAdapter {

    public static JDA jda;

    public void start() throws LoginException, InterruptedException {
        // args[0] should be the token
        // We only need 2 intents in this bot. We only respond to messages in guilds and private channels.
        // All other events will be disabled.
         JDA jdabuilder = JDABuilder.createLight("ODc4NzAyODkwMzQ3Mjg2NTM4.YSFB1g.dMDHHTod50-hKHUI1Eif4vAP5lA", GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new DiscordBot())
                .setActivity(Activity.playing("Super Earth"))
                .build();
         jda = jdabuilder;

         upsertCommands(jda);

//        jda.getTextChannelsByName("minecraft-chat", true).get(0).sendMessage("Server Is Up").queue();
    }

    public void upsertCommands(JDA jda) {
        jda.upsertCommand("time", "Set Time on The Server").addOption(OptionType.STRING, "time", "Time to set").queue();
        // .addSubcommands(new SubcommandData("set", "Set Time"))
        jda.upsertCommand("ping", "CalCulates Bots Ping").queue();
        jda.upsertCommand("ban", "Bans A Player on The Server").addOption(OptionType.STRING, "player", "Player's Name to ban").queue();
        jda.upsertCommand("pardon", "UnBans A Player on The Server").addOption(OptionType.STRING, "player", "Player's Name to unban").queue();
        jda.upsertCommand("op", "OP A Player on The Server").addOption(OptionType.STRING, "player", "Player's Name to OP").queue();
        jda.upsertCommand("deop", "DEOP A Player on The Server").addOption(OptionType.STRING, "player", "Player's Name to DEOP").queue();
        jda.upsertCommand("stop", "Stops The Server").queue();
        jda.upsertCommand("restart", "Restarts The Server").queue();
        jda.upsertCommand("weather", "Set Weather on The Server").addOption(OptionType.STRING, "weather", "Weather to set").queue();
        jda.upsertCommand("onlineplayers", "Gets All Online Players on the Server").queue();
        jda.upsertCommand("setcommandchannel", "Sets The Channel to recieve commands from").addOption(OptionType.CHANNEL, "channel", "Channel to Set").queue();
        jda.upsertCommand("setchatchannel", "Sets the channel that minecraft chat will be sent to").addOption(OptionType.CHANNEL, "channel", "Channel to Set").queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        Message msg = event.getMessage();

        if (event.getAuthor().getId().equals("878702890347286538")) return;

        if (event.getChannel().getId().equals(new FileManager().getChatChannelID())) {
            Bukkit.broadcastMessage("[§9Discord§r] <" + event.getAuthor().getName() + "> " + msg.getContentRaw());
        }
    }

    public void sendChatMessage(String ign, String msg) {
        jda.getTextChannelById(new FileManager().getChatChannelID()).sendMessage("[Minecraft] <" + ign + "> " + msg).queue();
    }


    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        if (!event.getChannel().getId().equals(new FileManager().getCommandsChannelID())) {
            replyEphemeral("Send Commands only in server-commands Channel", event);
            return;
        }

        if (event.getName().equals("time")) {
            if (event.getOption("time").getAsString().equals("")) return;
            SuperBOT.INSTANCE.runCommand("time set " + event.getOption("time").getAsString());
            reply("Time has been set", event);
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
            reply(event.getOption("player").getAsString() + " Has Been Banned", event);
        }
        if (event.getName().equals("pardon")) {
            if (event.getOption("player").getAsString().equals("")) return;
            SuperBOT.INSTANCE.runCommand("pardon " + event.getOption("player").getAsString());
            reply(event.getOption("player").getAsString() + " Has Been Pardoned", event);
        }
        if (event.getName().equals("op")) {
            if (event.getOption("player").getAsString().equals("")) return;
            SuperBOT.INSTANCE.runCommand("op " + event.getOption("player").getAsString());
            reply("Player Has Been OPed", event);
        }
        if (event.getName().equals("deop")) {
            if (event.getOption("player").getAsString().equals("")) return;
            SuperBOT.INSTANCE.runCommand("deop " + event.getOption("player").getAsString());
            reply(event.getOption("player").getAsString() + " Has Been DEOPed", event);
        }
        if (event.getName().equals("stop")) {
            reply("Server Has Been Stopped", event);
            SuperBOT.INSTANCE.runCommand("stop");
        }
        if (event.getName().equals("restart")) {
            reply("Server Is Restarting", event);
            SuperBOT.INSTANCE.runCommand("restart");
        }
        if (event.getName().equals("weather")) {
            String weather = event.getOption("weather").getAsString();
            if (weather.equals("")) return;
            if (weather.toLowerCase(Locale.ROOT).equals("clear") || weather.toLowerCase(Locale.ROOT).equals("rain") || weather.toLowerCase(Locale.ROOT).equals("thunder")) {
                SuperBOT.INSTANCE.runCommand("weather " + event.getOption("weather").getAsString().toLowerCase(Locale.ROOT));
                reply("Weather has been set", event);
            } else {
                replyEphemeral("Weather has to be set to either rain, clear, or thunder", event);
                return;
            }
        }
        if (event.getName().equals("onlineplayers")) {
            TextChannel tc = jda.getTextChannelById(new FileManager().getCommandsChannelID());
            StringBuilder sb = new StringBuilder();
            for(Player p : SuperBOT.INSTANCE.getServer().getOnlinePlayers()) {
                sb.append("Name: " + p.getDisplayName() + "\n               UUID: " + p.getUniqueId() + "\n");
            }
            reply("Onlineplayers: " + SuperBOT.INSTANCE.getServer().getOnlinePlayers().size() + "/" + SuperBOT.INSTANCE.getServer().getMaxPlayers() + "\n" + sb, event);
        }
        if (event.getName().equals("setcommandchannel")) {
            if (event.getOption("channel").getAsString().equals(null)) {
                replyEphemeral("Please Specify Channel", event);
                return;
            }
            reply(event.getOption("channel").getAsString(), event);
            try {
                new FileManager().setCommandChannel(event.getOption("channel").getAsString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (event.getName().equals("setchatchannel")) {
            if (event.getOption("channel").getAsString().equals(null)) {
                replyEphemeral("Please Specify Channel", event);
                return;
            }
            reply(event.getOption("channel").getAsString(), event);
            try {
                new FileManager().setChatChannel(event.getOption("channel").getAsString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void reply(String msg, SlashCommandEvent event) {
        event.reply(msg).queue();
    }
    public void replyEphemeral(String msg, SlashCommandEvent event) {
        event.reply(msg).setEphemeral(true).queue();
    }
}
