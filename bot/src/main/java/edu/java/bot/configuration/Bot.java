package edu.java.bot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import lombok.extern.java.Log;

@Log
public class Bot extends TelegramBot {
    int condition = -1;
    static Set<String> allForAll = ConcurrentHashMap.newKeySet();

    public Bot(ApplicationConfig app) {
        super(app.telegramToken());
    }

    public SendMessage handle(Update update) {
        var command = update.message().text();
        var id = update.message().chat().id();
        Pattern pattern1 = Pattern.compile("( *)(/list)( *)");
        Pattern pattern2 = Pattern.compile("( *)(/start)( *)");
        Pattern pattern3 = Pattern.compile("( *)(/track)( *)");
        Pattern pattern4 = Pattern.compile("( *)(/untrack)( *)");
        Pattern pattern5 = Pattern.compile("( *)(/help)( *)");
        if (condition == -1) {
            var res = new SendMessage(update.message().chat().id(), "");
            if (pattern5.matcher(command).find()) {
                log.info(help());
                res = new SendMessage(update.message().chat().id(), help());
            } else if (pattern2.matcher(command).find()) {
                var text = "Вы успешно зарегистрировались";
                log.info(text);
                condition = 0;
                res = new SendMessage(id, text);
            } else {
                var text = "Для доступа ко всем командам зарегистрируйтесь с помощью команды /start.";
                log.info(text);
                res = new SendMessage(id, text);
            }
            return res;
        }
        if (condition == 0) {
            String text;
            var res = new SendMessage(update.message().chat().id(), "");
            if (!pattern1.matcher(command).find() && !pattern2.matcher(command).find()
                && !pattern3.matcher(command).find() && !pattern4.matcher(command).find()
                && !pattern5.matcher(command).find()) {
                text = "Команда не распознана."
                    + "Введите /help, чтобы ознакомиться с допустимыми командами.";
                log.info(text);
                res = new SendMessage(update.message().chat().id(), text);
            } else if (pattern2.matcher(command).find()) {
                text = "Вы уже зарегистрированы";
                log.info(text);
                res = new SendMessage(update.message().chat().id(), text);
            } else {
                if (pattern1.matcher(command).find()) {
                    var str = list();
                    log.info(str);
                    res = new SendMessage(update.message().chat().id(), str);
                } else {
                    if (pattern3.matcher(command).find()) {
                        var answer = "Введите ссылку на ресурс, который хотите отслеживать";
                        condition = 1;
                        log.info(answer);
                        res = new SendMessage(update.message().chat().id(), answer);
                    } else {
                        if (pattern4.matcher(command).find()) {
                            condition = 2;
                            text = "Введите ссылку на ресурс, который хотите перестать отслеживать";
                            log.info(text);
                            res = new SendMessage(
                                update.message().chat().id(), text);
                        } else {
                            log.info(help());
                            res = new SendMessage(update.message().chat().id(), help());
                        }
                    }
                }

            }
            return res;
        }
        if (condition == 1) {
            var result = track(command);
            condition = 0;
            log.info(result);
            return new SendMessage(id, result);
        }
        condition = 0;
        var t = untrack(command);
        log.info(t);
        return new SendMessage(id, t);

    }

    public static String list() {
        if (allForAll.isEmpty()) {
            return "Список отслеживаемых ресурсов пуст";
        } else {
            StringBuilder start = new StringBuilder("Текущий список отслеживаемых ресурсов:\n");
            for (String link : allForAll) {
                start.append(link + "\n");
            }
            return start.toString();
        }
    }

    public static String track(String link) {
        try {
            var url = new URI(link).toURL();
            if (!allForAll.contains(link)) {
                allForAll.add(link);
                return ("Ресурс добавлен");
            } else {
                return "Ресурс уже находится в списке отслеживаемых";
            }
        } catch (MalformedURLException | IllegalArgumentException | URISyntaxException e) {
            return ("Не удалось подключиться к заданному ресурсу."
                + "Проверьте корректность ссылки.");
        }
    }

    public static String untrack(String link) {
        if (allForAll.contains(link)) {
            allForAll.remove(link);
            return ("Ресурс удален");
        } else {
            return "Ресурс ранее вами не отслеживался.";
        }
    }

    public static String help() {
        String res = "/start - регистрация в боте\n"
            + "/help - список доступных команд\n"
            + "/track - добавление ресурса в отслеживаемые\n"
            + "/untrack - прекращение отслеживания ресурса\n"
            + "/list - список отслеживаемых ресурсов\n";
        return res;
    }
}
