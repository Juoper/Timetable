package org.timeTable.CommunicationLayer.services;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.timeTable.CommunicationLayer.CommunicationLayer;
import org.timeTable.CommunicationLayer.CommunicationService;
import org.timeTable.CommunicationLayer.exceptions.moreThenOneStudentFoundException;
import org.timeTable.CommunicationLayer.exceptions.noStudentFoundException;
import org.timeTable.LiteSQL;
import org.timeTable.models.Course;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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

    @Override
    public void sendTimetableNews(int subscription_id, ArrayList<Course> courses) {

        ResultSet set = LiteSQL.onQuery("SELECT * FROM comService_0 WHERE subscription_id = " + subscription_id);
        if (set == null) return;
        Long user_id = null;
        Long channel_id = null;
        String channel_type = null;

        try {
            user_id = set.getLong("user_id");
            channel_id = set.getLong("user_id");
            channel_type = set.getString("channel_type");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        MessageChannel channel = null;
        User user = jda.getUserById(user_id);
        if (channel_type.equals("text")){
            channel = jda.getTextChannelById(channel_id);
        } else if (channel_type.equals("private")){
            channel = jda.getPrivateChannelById(channel_id);
        }
        channel.sendMessage("Your timetable has changed!").queue();



    }

    private void subscribeTimetable(int student_id, Long userID, Long channelID, String channel_type){

        try {
            //Add null statement
            ResultSet set = LiteSQL.onQuery("INSERT INTO subscriptions (student_id, type_id, update_rate, update_time) " +
                    "VALUES (0, 0, 'daily', 800) RETURNING subscription_id");
            int id = set.getInt("subscription_id");

            LiteSQL.onUpdate("INSERT INTO comService_0 (subscription_id, student_id, user_id, channel_id, channel_type) " +
                    "VALUES (" + id +", " + student_id +", "+ userID +", "+ channelID + ", '" + channel_type + "')");
            set.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //getCommunicationLayer().subscribeTimtableNews();
    }

    @Override
    public void stopService() {
        jda.shutdown();
    }

    class SlashCommandListener extends ListenerAdapter {

        @Override
        public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
            User user = event.getUser();

            if (user.isBot()) {
                return;
            }

            event.deferReply().queue();
            InteractionHook hook = event.getHook();
            

            switch (event.getName()) {
                case "subscribetimetable":{
                    String channel_type;
                    ChannelType cType = event.getChannel().getType();

                    switch (cType){
                        case TEXT -> channel_type = "text";
                        case PRIVATE -> channel_type = "private";
                        default -> channel_type = "unknown";
                    }

                    MessageChannel channel = event.getChannel();

                    int id = -1;

                    String prename = "";
                    String surname = "";

                    try {
                        prename = event.getOption("prename").getAsString();
                    } catch (NullPointerException ignored) {}

                    try {
                        surname = event.getOption("surname").getAsString();
                    } catch (NullPointerException ignored) {}


                    try {
                         id = getCommunicationLayer().getStudentIdByName(prename, surname);
                    } catch (noStudentFoundException e) {
                        hook.sendMessage("No student found with this name").queue();
                        return;
                    } catch (moreThenOneStudentFoundException e) {
                        hook.sendMessage("More then one student found with this name").queue();
                        return;
                    }

                    subscribeTimetable(id, user.getIdLong(), event.getChannel().getIdLong(), channel_type);
                    hook.sendMessage("You have successfully subscribed to your timetable").queue();
                };
                case "unsubscribetimetable":;
                case "getsubscriptions":;
            }
        }
        public void createSlashCommands (){

            jda.updateCommands().addCommands(
                    Commands.slash("subscribetimetable", "Subscribe to your own timetable")
                            .addOption(OptionType.STRING,"prename","Set the prename to search for")
                            .addOption(OptionType.STRING, "surname", "Set the surname to search for")
            ).queue();

        }
    }
}

