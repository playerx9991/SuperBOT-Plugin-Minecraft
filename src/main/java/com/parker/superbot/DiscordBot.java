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
    }

    public void upsertCommands(JDA jda) {
        jda.upsertCommand("time", "Set Time in Minecraft Server").addOption(OptionType.STRING, "args", "asdasdasdasd").queue();
        // .addSubcommands(new SubcommandData("set", "Set Time"))
        jda.upsertCommand("ping", "CalCulates Bots Ping").queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        Message msg = event.getMessage();

        if (event.getAuthor().getId().equals("878702890347286538")) return;

        if (event.getChannel().getId().equals("766410493241720865")) {
            Bukkit.broadcastMessage("[Discord] <" + event.getAuthor().getName() + "> " + msg.getContentRaw());
            SuperBOT.INSTANCE.runCommand("time set day");
        }
    }

    public void sendChatMessage(String ign, String msg) {
        jda.getTextChannelById("766410493241720865").sendMessage("[Minecraft] <" + ign + "> " + msg).queue();
    }


    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        if (event.getName().equals("time")) {

            event.reply("Time has been set").setEphemeral(true).queue();

            if (!event.getOption("args").equals("")) {
                SuperBOT.INSTANCE.runCommand("time set " + event.getOption("args").getAsString());
                event.reply("Time has been set").setEphemeral(true).queue();
            }
        } else if(event.getName().equals("ping")) {
            long time = System.currentTimeMillis();
            event.reply("Pong!").setEphemeral(true) // reply or acknowledge
                    .flatMap(v ->
                            event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time) // then edit original
                    ).queue(); // Queue both reply and edit
        } else {
            return;
        }
    }
}
