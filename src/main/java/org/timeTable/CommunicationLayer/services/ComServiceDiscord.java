package org.timeTable.communicationLayer.services;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.timeTable.communicationLayer.CommunicationLayer;
import org.timeTable.communicationLayer.CommunicationService;
import org.timeTable.communicationLayer.exceptions.MoreThanOneStudentFoundException;
import org.timeTable.communicationLayer.exceptions.NoStudentFoundException;
import org.timeTable.communicationLayer.exceptions.SubscriptionAlreadyExists;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.lesson.Lesson;
import org.timeTable.persistence.student.Student;
import org.timeTable.persistence.subscriptions.Subscription;
import org.timeTable.persistence.subscriptions.SubscriptionRepository;
import org.timeTable.persistence.subscriptions.comServiceDiscord.ComServiceDiscordRepository;
import org.timeTable.persistence.subscriptions.comServiceDiscord.ComServiceDiscordSubscription;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

@Service
public class ComServiceDiscord extends CommunicationService {

    private final Long ownerId;

    private final String discordToken;

    private final JDA jda;
    private static List<Long> verifiedUsers;

    private final ComServiceDiscordRepository comServiceDiscordRepository;

    private final Logger logger = LoggerFactory.getLogger(CommunicationLayer.class);
    private final CommunicationLayer communicationLayer;

    //type ID: 0

    @Autowired
    public ComServiceDiscord(ComServiceDiscordRepository comServiceDiscordRepository, SubscriptionRepository subscriptionRepository, @Value("${stundenplan.ownerId}") Long ownerId, @Value("${stundenplan.discordToken}") String discordToken, CommunicationLayer communicationLayer) {
        super(subscriptionRepository, communicationLayer);
        this.comServiceDiscordRepository = comServiceDiscordRepository;
        this.communicationLayer = communicationLayer;
        communicationLayer.registerCommunicationService(this);

        this.ownerId = ownerId;
        this.discordToken = discordToken;

        JDABuilder builder = JDABuilder.createDefault(this.discordToken);

        ActionListener listener = new ActionListener();
        builder.setAutoReconnect(true).enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_PRESENCES).setMemberCachePolicy(MemberCachePolicy.ALL).enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.ACTIVITY).addEventListeners(listener);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("Scraping your data"));

        jda = builder.build();
        verifiedUsers = new ArrayList<>();
        listener.createSlashCommands();
        logger.info("Bot online");
    }

    public void sendTimetableNews(Subscription subscription, ArrayList<Course> courses) {
        if (subscription instanceof ComServiceDiscordSubscription comServiceDiscordSubscription) {

            logger.info("Sending timetable news to " + subscription.getStudent().getPrename() + " " + subscription.getStudent().getSurname() + " with channel type " + comServiceDiscordSubscription.getChannelType() + " and channel id " + comServiceDiscordSubscription.getChannelId() + " and user id " + comServiceDiscordSubscription.getUserId() + " at " + LocalDateTime.now());

            ComServiceDiscord.this.sendTimetableNews(comServiceDiscordSubscription, courses);

        }
    }

    private void sendTimetableNews(ComServiceDiscordSubscription subscription, ArrayList<Course> courses) {
        MessageChannel channel = null;
        if (subscription.getChannelType() == ChannelType.TEXT) {
            channel = jda.getTextChannelById(subscription.getChannelId());
        } else if (subscription.getChannelType() == ChannelType.PRIVATE) {
            logger.info("Opening private channel with id " + subscription.getChannelId());
            channel = jda.getUserById(subscription.getUserId()).openPrivateChannel().complete();
        } else {
            throw new RuntimeException("Unknown channel type");
        }

        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Your timetable for " + courses.get(0).getLessons().iterator().next().getDay()).setAuthor(subscription.getStudent().getPrename() + " " + subscription.getStudent().getSurname()).setColor(new Color(9565856)).setFooter("Made with love by Julian Thanner", "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f1/Heart_coraz%C3%B3n.svg/1200px-Heart_coraz%C3%B3n.svg.png");

        courses.sort(Comparator.comparing(o -> o.getLessons().iterator().next().getStartTime()));

        for (Course course : courses) {
            builder.addField("Course: " + course.getName(), course.getShortSubject(), false);
            List<Lesson> lessonList = course.getLessons().stream().toList();

            for (Lesson lesson : lessonList) {
                builder.addField(lesson.getStartTime() + " - " + lesson.getEndTime(), lesson.getCellstate(), true);
            }

        }
        channel.sendMessageEmbeds(builder.build()).queue();
    }

    private void unsubscribeTimetable(Long userID, Long channelID, ChannelType channelType, long subscription_id) {

        logger.info("Unsubscribing user " + userID + " from channel " + channelID + " with type " + channelType + " and subscription id " + subscription_id);
        ComServiceDiscordSubscription subscription = comServiceDiscordRepository.findById(subscription_id).get();
        comServiceDiscordRepository.delete(subscription);

        super.unsubscribeTimetable(subscription);
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

            event.deferReply(false).queue();
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
                    break;

                case "getcurrenttimetable": {

                    sendCurrentTimetable(event);
                    break;
                }
            }
        }

        private void subscribeTimetable(SlashCommandInteractionEvent event) {
            InteractionHook hook = event.getHook();
            User user = event.getUser();

            ChannelType cType = event.getChannel().getType();

            String prename = Objects.requireNonNullElse(event.getOption("prename", OptionMapping::getAsString), "");
            String surname = Objects.requireNonNullElse(event.getOption("surname", OptionMapping::getAsString), "");
            LocalTime updateTime = getLocalTimeFromInt(Objects.requireNonNullElse(event.getOption("updatetime", OptionMapping::getAsInt), 730));

            Student student;

            try {
                student = getCommunicationLayer().getStudentByName(prename, surname);
            } catch (NoStudentFoundException e) {
                hook.sendMessage("No student found with this name").queue();
                return;
            } catch (MoreThanOneStudentFoundException e) {
                hook.sendMessage("More then one student found with this name").queue();
                return;
            }
            ComServiceDiscordSubscription subscription = null;

            int offsetDays = 0;
            if (updateTime.getHour() > 10) {
                offsetDays = 1;
            }

            try {


                if (false) {//add check
                    throw new SubscriptionAlreadyExists("You are already subscribed to this timetable");
                }

                subscription = new ComServiceDiscordSubscription(
                        student,
                        updateTime,
                        offsetDays,
                        event.getUser().getIdLong(),
                        event.getChannel().getIdLong(),
                        cType,
                        0);
                comServiceDiscordRepository.save(subscription);

            } catch (SubscriptionAlreadyExists e) {
                hook.sendMessage("You are already subscribed to the timetable of this student on this channel").queue();
                return;
            }


            if (!isVerified(user.getIdLong())) {
                sendVerificationMessage(user, prename + " " + "surname", "subverify", subscription.getId());
            } else {
                hook.sendMessage("You have successfully subscribed to your timetable, but you need verification by the Owner").queue();
                verifyTimetable(subscription.getId());
            }

        }

        private boolean isVerified(long userId) {
            logger.info(ownerId + " " + userId);

            if (Objects.equals(ownerId, userId)) {
                return true;
            }
            if (verifiedUsers.contains(userId)) {
                return true;
            }

            return comServiceDiscordRepository.findByUserId(userId).isVerified();
        }

        private void unsubscribeTimetable(SlashCommandInteractionEvent event) {

            //TODO use specified subscription
            InteractionHook hook = event.getHook();
            User user = event.getUser();

            ChannelType cType = event.getChannel().getType();

            int subscriptionId = event.getOption("subscription").getAsInt();
            ComServiceDiscord.this.unsubscribeTimetable(user.getIdLong(), event.getChannel().getIdLong(), cType, subscriptionId);
            hook.sendMessage("You have successfully unsubscribed from your timetable").queue();
        }

        private void sendCurrentTimetable(SlashCommandInteractionEvent event) {
            InteractionHook hook = event.getHook();
            User user = event.getUser();
            String prename;
            String surname;
            int offsetdays;
            try {
                prename = event.getOption("prename").getAsString();
            } catch (NullPointerException ignored) {
                prename = "";
            }
            try {
                surname = event.getOption("surname").getAsString();
            } catch (NullPointerException ignored) {
                surname = "";
            }
            try {
                offsetdays = event.getOption("offsetdays").getAsInt();
            } catch (NullPointerException ignored) {
                offsetdays = 0;
            }

            Student student;
            try {
                student = getCommunicationLayer().getStudentByName(prename, surname);
            } catch (NoStudentFoundException e) {
                hook.sendMessage("No student found with this name").queue();
                return;
            } catch (MoreThanOneStudentFoundException e) {
                hook.sendMessage("More then one student found with this name").queue();
                return;
            }

            if (!isVerified(user.getIdLong())) {
                sendVerificationMessage(user, prename + " " + "surname", "getverify", user.getIdLong());
                hook.sendMessage("You need to be verified by the owner to use this command. Please Try again later").queue();
                return;
            }

            ArrayList<Course> courses = getCommunicationLayer().getCourseDataOfStudent(student, offsetdays);


            ComServiceDiscordSubscription subscription = comServiceDiscordRepository.findAllByStudentAndChannelTypeAndChannelIdAndUserId(student, event.getChannel().getType(), event.getChannel().getIdLong(), user.getIdLong()).get(0);

            sendTimetableNews(subscription, courses);
        }

        private void sendVerificationMessage(User user, String name, String buttonPreFix, long buttonId) {

            jda.getUserById(ownerId).openPrivateChannel().queue((pChannel) -> {
                pChannel.sendMessage("Please Verify request from User: " + user.getAsMention() + " for the Timetable for " + name).addActionRow(Button.success(buttonPreFix + "_accept_" + buttonId, "Accept"), Button.danger(buttonPreFix + "_deny_" + buttonId, "Deny")).queue();
            });
        }

        private LocalTime getLocalTimeFromInt(int time) {
            int hours = time / 100;
            int minutes = time % 100;
            return LocalTime.of(hours, minutes);
        }

        public void createSlashCommands() {
            jda.updateCommands().addCommands(Commands.slash("subscribetimetable", "Subscribe to your own timetable").addOption(OptionType.STRING, "prename", "Set the prename to search for").addOption(OptionType.STRING, "surname", "Set the surname to search for").addOption(OptionType.INTEGER, "updatetime", "Set the time you want your timetable to be sent to you in the format HHmm"),
                    Commands.slash("unsubscribetimetable", "Unsubscribe to your subscribed timetable").addOption(OptionType.STRING, "subscription", "Select the subscription to unsubscribe from", true, true),
                    Commands.slash("getcurrenttimetable", "Get your current Timetable for your subscribed students").addOption(OptionType.STRING, "prename", "Set the prename to search for").addOption(OptionType.STRING, "surname", "Set the surname to search for").addOption(OptionType.INTEGER, "offsetdays", "set how many days from today you want your timetable to be offset")

            ).queue();
        }

        @Override
        public void onButtonInteraction(ButtonInteractionEvent event) {
            String eventID = event.getComponentId();
            String[] args = eventID.split("_");

            switch (args[0]) {
                case "getverify" -> {
                    switch (args[1]) {
                        case "accept" -> {
                            System.out.println(Arrays.toString(args));
                            verifiedUsers.add(Long.valueOf(args[2]));
                            event.reply("Successfully verified!").queue();
                            event.getMessage().delete().queue();
                        }
                        case "deny" -> {
                            event.reply("Successfully denied!").queue();
                            event.getMessage().delete().queue();
                        }
                    }
                }
                case "subverify" -> {
                    switch (args[1]) {
                        case "accept" -> {
                            verifyTimetable(Integer.parseInt(args[2]));
                            event.reply("Successfully verified!").queue();
                            event.getMessage().delete().queue();
                        }
                        case "deny" -> {
                            ComServiceDiscordSubscription subscription = comServiceDiscordRepository.findById(Long.valueOf(args[2])).get();

                            ComServiceDiscord.this.unsubscribeTimetable(subscription.getUserId(), subscription.getChannelId(), subscription.getChannelType(), Integer.parseInt(args[2]));


                            jda.getUserById(subscription.getUserId()).openPrivateChannel().queue((pChannel) -> {
                                pChannel.sendMessage("Sorry, but the owner couldn't accept your request").queue();
                            });
                            event.reply("You successfully denied the request from the user").queue();
                        }
                    }
                }
            }


        }

        @Override
        public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {

            if (List.of("unsubscribetimetable", "getcurrenttimetable").contains(event.getName()) && event.getFocusedOption().getName().equals("subscription")) {
                long userID = event.getUser().getIdLong();

                List<Command.Choice> options = new ArrayList<>();

                List<ComServiceDiscordSubscription> subscriptions = comServiceDiscordRepository.findAllByUserId(event.getUser().getIdLong());

                subscriptions.forEach(subscription -> {
                    options.add(new Command.Choice(subscription.getStudent().getPrename() + " " + subscription.getStudent().getSurname(), subscription.getId()));
                });
                event.replyChoices(options).queue();

            }
        }

    }
}


