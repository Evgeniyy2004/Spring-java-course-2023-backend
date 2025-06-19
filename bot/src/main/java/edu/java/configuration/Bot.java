package edu.java.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.model.ClassResponse;
import edu.java.scrapperclient.ScrapperNotificationClient;
import edu.java.scrapperclient.ScrapperScheduleClient;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings({"ReturnCount", "CyclomaticComplexity", "RegexpSinglelineJava"})
public class Bot extends TelegramBot {

    @Autowired
    private ScrapperNotificationClient chat;

    @Autowired
    private ScrapperScheduleClient schedule;

    public Bot() {
        super(System.getenv("APP_TELEGRAM_TOKEN"));
        this.setUpdatesListener(updates -> {
            for (Update update : updates) {
                if (update.message() != null && update.message().text().equals("/start")) {
                    var id = update.message().chat().id();
                    if (!chat.groupGet(id).getBody()) {
                        this.execute(new SendMessage(id, "Добро пожаловать! " +
                            "Введите свою группу для просмотра расписания занятий."));
                    }
                    continue;
                }
                if (update.callbackQuery() == null) {
                    if ("Настройки".equals(update.message().text())) {
                        settingsClick(update.message().chat().id());
                    } else if ("Расписание".equals(update.message().text())) {
                        scheduleClick(update.message().chat().id());
                    } else {
                        try {
                            this.handleGroupInput(update);
                        } catch (IOException ignored) {
                        }
                    }
                } else {
                    Set<String> variki = new HashSet<>();
                    variki.add("today");
                    variki.add("tomorrow");
                    variki.add("week");
                    String callback = update.callbackQuery().data();
                    if (variki.contains(callback)) {
                        casesOfSchedule(update.callbackQuery().from().id(), callback);
                    } else {
                        var id = update.callbackQuery().from().id();
                        switch (callback) {

                            case "group":
                                this.execute(new SendMessage(id, "Введите название группы."));
                                break;
                            case "mailing":
                                var already = chat.getMailing(id);
                                InlineKeyboardMarkup keyboard;
                                if (!Boolean.TRUE.equals(already.getBody())) {
                                    keyboard = new InlineKeyboardMarkup(
                                        new InlineKeyboardButton[][] {
                                            {
                                                new InlineKeyboardButton("1").callbackData("1"),
                                                new InlineKeyboardButton("2").callbackData("2"),
                                                new InlineKeyboardButton("3").callbackData("3"),
                                                new InlineKeyboardButton("4").callbackData("4"),
                                                new InlineKeyboardButton("5").callbackData("5")
                                            },
                                            {
                                                new InlineKeyboardButton("6").callbackData("6"),
                                                new InlineKeyboardButton("7").callbackData("7"),
                                                new InlineKeyboardButton("8").callbackData("8"),
                                                new InlineKeyboardButton("9").callbackData("9"),
                                                new InlineKeyboardButton("10").callbackData("10")
                                            },
                                            {
                                                new InlineKeyboardButton("11").callbackData("11"),
                                                new InlineKeyboardButton("12").callbackData("12"),
                                                new InlineKeyboardButton("13").callbackData("13"),
                                                new InlineKeyboardButton("14").callbackData("14"),
                                                new InlineKeyboardButton("15").callbackData("15")
                                            },
                                            {
                                                new InlineKeyboardButton("16").callbackData("16"),
                                                new InlineKeyboardButton("17").callbackData("17"),
                                                new InlineKeyboardButton("18").callbackData("18"),
                                                new InlineKeyboardButton("19").callbackData("19"),
                                                new InlineKeyboardButton("20").callbackData("20")
                                            }
                                        }
                                    );
                                    this.execute(new SendMessage(id, "Выберите время рассылки расписания " +
                                        "занятий на завтра.").replyMarkup(keyboard));
                                } else {
                                    keyboard = new InlineKeyboardMarkup(
                                        new InlineKeyboardButton[][] {
                                            {
                                                new InlineKeyboardButton("1").callbackData("1"),
                                                new InlineKeyboardButton("2").callbackData("2"),
                                                new InlineKeyboardButton("3").callbackData("3"),
                                                new InlineKeyboardButton("4").callbackData("4"),
                                                new InlineKeyboardButton("5").callbackData("5")
                                            },
                                            {
                                                new InlineKeyboardButton("6").callbackData("6"),
                                                new InlineKeyboardButton("7").callbackData("7"),
                                                new InlineKeyboardButton("8").callbackData("8"),
                                                new InlineKeyboardButton("9").callbackData("9"),
                                                new InlineKeyboardButton("10").callbackData("10")
                                            },
                                            {
                                                new InlineKeyboardButton("11").callbackData("11"),
                                                new InlineKeyboardButton("12").callbackData("12"),
                                                new InlineKeyboardButton("13").callbackData("13"),
                                                new InlineKeyboardButton("14").callbackData("14"),
                                                new InlineKeyboardButton("15").callbackData("15")
                                            },
                                            {
                                                new InlineKeyboardButton("16").callbackData("16"),
                                                new InlineKeyboardButton("17").callbackData("17"),
                                                new InlineKeyboardButton("18").callbackData("18"),
                                                new InlineKeyboardButton("19").callbackData("19"),
                                                new InlineKeyboardButton("20").callbackData("20")
                                            }, {
                                            new InlineKeyboardButton("Отписаться от рассылки").callbackData(
                                                "cancelmailing")
                                        }
                                        }
                                    );
                                    this.execute(new SendMessage(id, "Выберите время рассылки расписания " +
                                        "занятий на завтра или нажмите \"Отписаться от рассылки\".").replyMarkup(
                                        keyboard));
                                }
                                break;

                            case "notifications":
                                var noww = chat.notifyGet(id).getBody();
                                InlineKeyboardMarkup inlineKeyboardMarkup;
                                if (noww) {
                                    inlineKeyboardMarkup = new InlineKeyboardMarkup(
                                        new InlineKeyboardButton("Отключить уведомления").callbackData("cancelnotify"));
                                    this.execute(new SendMessage(id, "Нажмите, если хотите отключить" +
                                        " уведомления об изменениях в расписании.").replyMarkup(inlineKeyboardMarkup));
                                } else {
                                    inlineKeyboardMarkup = new InlineKeyboardMarkup(
                                        new InlineKeyboardButton("Включить уведомления").callbackData("returnnotify"));
                                    this.execute(new SendMessage(id, "Нажмите, если хотите включить" +
                                        " уведомления об изменениях в расписании.").replyMarkup(inlineKeyboardMarkup));
                                }
                                break;
                            case "returnnotify":
                                chat.notifyPost(id, true);
                                this.execute(new SendMessage(id, "Готово! Уведомления включены."));
                                break;
                            case "cancelNotify":
                                chat.notifyPost(id, false);
                                this.execute(new SendMessage(id, "Готово! Уведомления отключены."));
                                break;
                            case "cancelmailing":
                                chat.postMailing(id, null);
                                this.execute(new SendMessage(id, "Готово! Вы отписались от рассылки расписания."));
                                break;
                            default:
                                var value = Integer.valueOf(callback);
                                chat.postMailing(id, value);
                                this.execute(new SendMessage(id, String.format("Готово! Вы будете получать " +
                                    "расписание на завтра в %s часов.", callback)));
                        }

                    }
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            if (e.response() != null) {
                // got bad response from telegram
                e.response().errorCode();
                e.response().description();
            } else {
                // probably network error
                e.printStackTrace();
            }
        }, new GetUpdates().limit(2 * 2 * 2 * 2 * 2 * 2 + 2 * 2 * 2 * 2 * 2 + 2 * 2).offset(0).timeout(0));
    }

    public void handleGroupInput(Update update) throws IOException {
        var command = update.message().text();
        var toElFormat = command.replaceAll("\\s", "").toUpperCase();
        String groupnameToClassic = convertCyrilic(toElFormat.toLowerCase());
        var doc = Jsoup.connect("https://www.susu.ru/ru/lessons/" + groupnameToClassic)
            .get();
        var table = doc.select("table");
        String val;
        var already = chat.groupGet(update.message().chat().id()).getBody();
        if (table.isEmpty()) {
            val = "Вы ввели группу, на которую у нас ещё нет расписания, или ввели неправильно. " +
                "Пример группы: \"КЭ-301\".";
        } else {
            chat.groupPost(update.message().chat().id(), groupnameToClassic.toLowerCase());
            val = String.format("Готово! Ваша группа %s.", toElFormat);
            if (!already) {
                val = String.format("Готово! Ваша группа %s. Нажмите \"Настройки\", " +
                    " чтобы настроить рассылку и уведомления. Нажмите \"Расписание\" " +
                    "для просмотра графика учебных занятий.", toElFormat);
            }
        }

        SendMessage toSend = new SendMessage(update.message().chat().id(), val);

        if (val.charAt(0) == 'Г') {
            if (!already) {
                toSend = toSend.replyMarkup(new ReplyKeyboardMarkup(
                    new String[][] {
                        {"Настройки"},
                        {"Расписание"}
                    }
                ).resizeKeyboard(true).oneTimeKeyboard(false));
            }
        }
        this.execute(toSend);
    }

    public void settingsClick(Long id) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
            new InlineKeyboardButton[][] {
                {new InlineKeyboardButton("Изменить группу").callbackData("group")},
                {new InlineKeyboardButton("Уведомления").callbackData("notifications")},
                {new InlineKeyboardButton("Рассылка").callbackData("mailing")}
            }
        );
        SendMessage msg = new SendMessage(
            id,
            "Воспользуйтесь кнопками \uD83D\uDC47 для настройки параметров"
        ).replyMarkup(keyboard);
        this.execute(msg);
    }

    public void scheduleClick(Long id) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
            new InlineKeyboardButton[][] {
                {new InlineKeyboardButton("Сегодня").callbackData("today")},
                {new InlineKeyboardButton("Завтра").callbackData("tomorrow")},
                {new InlineKeyboardButton("Текущая неделя").callbackData("week")}
            }
        );
        SendMessage msg = new SendMessage(
            id,
            "Выберите день для получения расписания занятий"
        ).replyMarkup(keyboard);
        this.execute(msg);
    }

    public void casesOfSchedule(Long id, String callback) {
        List<ClassResponse> send = switch (callback) {
            case "today" -> schedule.get(id, "0").getBody();
            case "tomorrow" -> schedule.get(id, "1").getBody();
            default -> schedule.get(id, "7").getBody();
        };

        try {
            sendSchedule(id, send, false);
        } catch (Exception ignored) {
        }
    }

    public void sendSchedule(Long chatId, List<ClassResponse> r, boolean update)
        throws IOException, TemplateException {
        StringBuilder res = new StringBuilder();

        if(r.isEmpty()) {
            res.append("<b>Занятий нет!</b>\n\n");
        }
        r.sort((r1, r2) -> {
            String date1 = r1.getDay();
            String date2 = r2.getDay();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // Parse the string into a LocalDate object
            LocalDate one = LocalDate.parse(date1, formatter);
            LocalDate two = LocalDate.parse(date2, formatter);
            return one.compareTo(two);
        });
        for (int z = 0; z < r.size(); z++) {
            var daily = r.get(z);
            // Создаем конфигурацию Freemarker
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);
            cfg.setClassForTemplateLoading(Bot.class, "/");
            Template template = cfg.getTemplate("outputtemplate.ftl");

            Map<String, Object> data = new HashMap<>();
            data.put("day", daily.getDay());
            data.put("time", daily.getTime());
            data.put("classrooms", daily.getClassrooms());
            data.put("subjects", daily.getSubjects());
            data.put("someCondition", daily.getSubjects().isEmpty());
            data.put("update",update);
            // Создаем контекст данных
            StringWriter writer = new StringWriter();
            template.process(data, writer);
            res.append(writer);
        }

        var str = res.toString();
        var toSend = new SendMessage(chatId,str).parseMode(ParseMode.HTML);
        var resp = this.execute(toSend);
        resp = resp;
    }

    public String convertCyrilic(String message) {
        char[] abcCyr =
            {'а', 'б', 'в', 'г', 'д', 'е', 'ж', 'з',
                'и', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'x', 'э', 'ю', 'я',
                '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        String[] abcLat =
            {"a", "b", "v", "g", "d", "e", "zh", "z",
                "i", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f", "kh", "e", "yu", "ya",
                "-", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            for (int x = 0; x < abcCyr.length; x++) {
                if (message.charAt(i) == abcCyr[x]) {
                    builder.append(abcLat[x]);
                }
            }
        }
        return builder.toString();
    }
}

