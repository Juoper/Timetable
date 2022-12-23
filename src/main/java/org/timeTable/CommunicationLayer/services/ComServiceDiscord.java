package org.timeTable.CommunicationLayer.services;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.timeTable.CommunicationLayer.CommunicationLayer;
import org.timeTable.CommunicationLayer.CommunicationService;
import org.timeTable.LiteSQL;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ComServiceDiscord extends CommunicationService{

    private final JDA jda;

    //type ID: 0

    public ComServiceDiscord(CommunicationLayer communicationLayer) {
        super(communicationLayer);

        JDABuilder builder = JDABuilder.createDefault("MTA1NTA4NjAwNzY2NzY1ODc3Mg.GEsKOF.pKZDslT3NDXzutL-oqDWVwB-GcEok5WZnFt1aY");

        SlashCommandListener listener = new SlashCommandListener();
        builder
                .setAutoReconnect(true)
                .enableIntents(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_PRESENCES
                )
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.ACTIVITY)
                .addEventListeners(listener);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("Scraping your data"));

        jda = builder.build();
        listener.createSlashCommands();
        System.out.println("Bot online");
    }

    public void sendTimetableNews(){

        System.out.println("sending to discord");
    }

    @Override
    public void stopService() {
        
    }

    class SlashCommandListener extends ListenerAdapter {

        @Override
        public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
            User user = event.getUser();

            if (user.isBot()) {
                return;
            }
            
            switch (event.getName()) {
                case "subscribetimetable":subscribeTimetable(user.getIdLong(), event.getChannel().getIdLong());
                case "unsubscribetimetable":;
                case "getsubscriptions":;
            }
            

        }

        private void subscribeTimetable(Long userID, Long channelID){

            try {
                ResultSet set = LiteSQL.onQuery("INSERT INTO subscriptions (student_id, type_id, update_rate, update_time) " +
                        "VALUES (0, 0, daily, 800) RETURNING subscription_id");
                int id = set.getInt("subscription_id");

                LiteSQL.onUpdate("INSERT INTO comService_0 (subscription_id, student_id, user_id, channel_id) VALUES (" + id +", 0, "+ userID +", "+ channelID +")");

                set.close();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }





            //getCommunicationLayer().subscribeTimtableNews();
        }

        public void createSlashCommands (){
            jda.updateCommands().addCommands(
                    Commands.slash("subscribetimetable", "Subscribe to your own timetable")
            ).queue();

        }
    }
}

