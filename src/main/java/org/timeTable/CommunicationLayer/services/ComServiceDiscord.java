package org.timeTable.CommunicationLayer.services;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.timeTable.CommunicationLayer.CommunicationLayer;
import org.timeTable.CommunicationLayer.CommunicationService;
import org.timeTable.CommunicationLayer.exceptions.moreThenOneStudentFoundException;
import org.timeTable.CommunicationLayer.exceptions.noStudentFoundException;
import org.timeTable.CommunicationLayer.exceptions.subscriptionAlreadyExists;
import org.timeTable.Config;
import org.timeTable.LiteSQL;
import org.timeTable.models.Course;
import org.timeTable.models.Lesson;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComServiceDiscord extends CommunicationService {

    private final JDA jda;

    //type ID: 0

    public ComServiceDiscord(CommunicationLayer communicationLayer) {
        super(communicationLayer);

        JDABuilder builder = JDABuilder.createDefault(Config.token);

        ActionListener listener = new ActionListener();
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
    protected void sendTimetableNews(int subscription_id, ArrayList<Course> courses) {
        ResultSet set = LiteSQL.onQuery("SELECT * FROM comService_0 INNER JOIN student ON comService_0.student_id = student.id WHERE subscription_id = " + subscription_id);
        if (set == null) return;
        long user_id;
        long channel_id;
        String channel_type = "";
        String prename = null;
        String surname = null;

        try {
            user_id = set.getLong("user_id");
            channel_id = set.getLong("channel_id");
            channel_type = set.getString("channel_type");
            prename = set.getString("prename");
            surname = set.getString("surname");
            set.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        MessageChannel channel = null;
        if (channel_type.equals("text")) {
            channel = jda.getTextChannelById(channel_id);
        } else if (channel_type.equals("private")) {
            channel = jda.openPrivateChannelById(user_id).complete();
        } else {
            throw new RuntimeException("Unknown channel type");
        }

        EmbedBuilder builder = new EmbedBuilder();

        builder
                .setTitle("Your timetable for " + courses.get(0).getLessons().get(0).getDay())
                .setAuthor(prename + " " + surname)
                .setColor(new Color(9565856))
                .setFooter("Made with love by Julian Thanner", "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f1/Heart_coraz%C3%B3n.svg/1200px-Heart_coraz%C3%B3n.svg.png");

        courses.sort((o1, o2) -> {
            Lesson lesson1 = o1.getLessons().get(0);
            Lesson lesson2 = o2.getLessons().get(0);
            return Integer.compare(lesson1.getStartTime(), lesson2.getStartTime());
        });

        for (Course course : courses) {
            builder.addField("Course: " + course.getName(), course.getShortSubject(), false);
            List<Lesson> lessonList = course.getLessons();
            for (Lesson lesson : lessonList) {
                builder.addField(lesson.getStartTime() + " - " + lesson.getEndTime(), lesson.getCellstate(), true);
            }

        }
        channel.sendMessageEmbeds(builder.build()).queue();
    }

    private void unsubscribeTimetable(Long userID, Long channelID, String channel_type) {

        ResultSet set = LiteSQL.onQuery("SELECT subscription_id FROM comService_0 WHERE user_id = " + userID + " AND channel_id = " + channelID + " AND channel_type = '" + channel_type + "'");

        try {
            if (!set.next()) {

                return;
            }
            super.unsubscribeTimetable(set.getInt("subscription_id"), 0);
            set.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void stopService() {
        jda.shutdown();
    }

    class ActionListener extends ListenerAdapter {

        @Override
        public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
            User user = event.getUser();

            if (user.isBot()) {
                return;
            }

            event.deferReply().queue();
            InteractionHook hook = event.getHook();


            switch (event.getName()) {
                case "subscribetimetable": {
                    subscribeTimetable(event);
                }
                break;
                case "unsubscribetimetable": {

                    unsubscribeTimetable(event);
                }
                break;
                case "getsubscriptions":
                    ;

                case "getcurrenttimetable": {
                    sendCurrentTimetable(event);
                }
            }
        }

        private void subscribeTimetable(SlashCommandInteractionEvent event) {
            InteractionHook hook = event.getHook();
            User user = event.getUser();

            String channel_type;
            ChannelType cType = event.getChannel().getType();

            switch (cType) {
                case TEXT -> channel_type = "text";
                case PRIVATE -> channel_type = "private";
                default -> channel_type = "unknown";
            }

            String prename = Objects.requireNonNullElse(event.getOption("prename", OptionMapping::getAsString), "");
            String surname = Objects.requireNonNullElse(event.getOption("surname", OptionMapping::getAsString), "");
            int updateTime = Objects.requireNonNullElse(event.getOption("updatetime", OptionMapping::getAsInt), 730);

            int id;

            try {
                id = getCommunicationLayer().getStudentIdByName(prename, surname);
            } catch (noStudentFoundException e) {
                hook.sendMessage("No student found with this name").queue();
                return;
            } catch (moreThenOneStudentFoundException e) {
                hook.sendMessage("More then one student found with this name").queue();
                return;
            }
            int subscription_id = -1;
            try {
                subscription_id = ComServiceDiscord.this.subscribeTimetable(id, user.getIdLong(), event.getChannel().getIdLong(), channel_type, updateTime, 0);

            } catch (subscriptionAlreadyExists e) {
                hook.sendMessage("You are already subscribed to the timetable of this student on this channel").queue();
                return;
            }
            hook.sendMessage("You have successfully subscribed to your timetable, but you need verification by the Owner").queue();
            int finalSubscription_id = subscription_id;
            jda.getUserById(Config.ownerId).openPrivateChannel().queue((pChannel) ->
            {
                pChannel.sendMessage("Please Verify request from User: " + event.getUser().getAsMention() + " for the Timetable for " + prename + " " + surname)
                        .addActionRow(Button.success("verify_accept_" + finalSubscription_id, "Accept"), Button.danger("verify_deny_" + finalSubscription_id, "Deny"))
                        .queue();
            });
        }

        private void unsubscribeTimetable(SlashCommandInteractionEvent event) {
            String channel_type;
            InteractionHook hook = event.getHook();
            User user = event.getUser();

            ChannelType cType = event.getChannel().getType();

            switch (cType) {
                case TEXT -> channel_type = "text";
                case PRIVATE -> channel_type = "private";
                default -> channel_type = "unknown";
            }
            ComServiceDiscord.this.unsubscribeTimetable(user.getIdLong(), event.getChannel().getIdLong(), channel_type);
            hook.sendMessage("You have successfully unsubscribed from your timetable").queue();
        }

        private void sendCurrentTimetable(SlashCommandInteractionEvent event) {
            InteractionHook hook = event.getHook();

            String prename = "";
            String surname = "";
            try {
                prename = event.getOption("prename").getAsString();
            } catch (NullPointerException ignored) {
            }
            try {
                surname = event.getOption("surname").getAsString();
            } catch (NullPointerException ignored) {
            }

            int id = -1;
            try {
                id = getCommunicationLayer().getStudentIdByName(prename, surname);
            } catch (noStudentFoundException e) {
                hook.sendMessage("No student found with this name").queue();
                return;
            } catch (moreThenOneStudentFoundException e) {
                hook.sendMessage("More then one student found with this name").queue();
                return;
            }
            getCommunicationLayer().sendTimetableNews(id);

        }

        public void createSlashCommands() {
            jda.updateCommands().addCommands(
                    Commands.slash("subscribetimetable", "Subscribe to your own timetable")
                            .addOption(OptionType.STRING, "prename", "Set the prename to search for")
                            .addOption(OptionType.STRING, "surname", "Set the surname to search for")
                            .addOption(OptionType.INTEGER, "updatetime", "Set the time you want your timetable to be sent to you in the format HHmm"),
                    Commands.slash("unsubscribetimetable", "Unsubscribe to your subscribed timetable")
                            .addOption(OptionType.STRING, "subscription", "Select the subscription to unsubscribe from", true, true)
                    //,Commands.slash("getcurrenttimetable","Get your current Timetable")
            ).queue();
        }

        @Override
        public void onButtonInteraction(ButtonInteractionEvent event) {
            String eventID = event.getComponentId();
            String action = eventID.substring(0, eventID.lastIndexOf("_"));
            String subscription_id = eventID.substring(eventID.lastIndexOf("_") + 1);

            switch (action) {
                case "verify_accept" -> {
                    verifyTimetable(Integer.parseInt(subscription_id));
                    event.reply("Successfully verified!").queue();
                    event.getMessage().delete().queue();
                }
                case "verify_deny" -> {
                    ResultSet set = LiteSQL.onQuery("SELECT * FROM comService_0 WHERE subscription_id = " + subscription_id);
                    try {
                        set.next();
                        long userID = set.getLong("user_id");
                        long channelID = set.getLong("channel_id");
                        String channel_type = set.getString("channel_type");
                        ComServiceDiscord.this.unsubscribeTimetable(userID, channelID, channel_type);

                        jda.getUserById(userID).openPrivateChannel().queue((pChannel) ->
                        {
                            pChannel.sendMessage("Sorry, but the owner couldn't accept your request")
                                    .queue();
                        });
                        event.reply("You successfully denied the request from the user").queue();

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        @Override
        public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {

            if (event.getName().equals("unsubscribetimetable") && event.getFocusedOption().getName().equals("subscription")) {
                long userID = event.getUser().getIdLong();
                ResultSet set = LiteSQL.onQuery("SELECT * FROM comService_0 INNER JOIN student ON comService_0.student_id = student.id WHERE user_id = " + userID);

                List<Command.Choice> options = new ArrayList<>();

                try {
                    while (set.next()) {
                        options.add(new Command.Choice(set.getString("Prename") + " " + set.getString("Surname"), set.getInt("subscription_id")));
                    }

                } catch (SQLException ignored) {}

                event.replyChoices(options).queue();
            }
        }

    }
}


