package listener;

import edu.java.model.ClassResponse;
import edu.java.siteclients.BotClient;
import io.swagger.api.JdbcScheduleRepository;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class ScheduleUpdaterScheduler {

    private final BotClient client;

    private final JdbcScheduleRepository repo;

    public ScheduleUpdaterScheduler(BotClient client, JdbcScheduleRepository repo) {
        this.repo = repo;
        this.client = client;
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void update() {
        var allChanges = repo.update();
        var tonotifyUsers = repo.getForNotifications(allChanges.getLeft());
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (Map.Entry<Integer, List<ClassResponse>> p : tonotifyUsers.entrySet()) {
            executor.submit(() -> performNotificationTask(p.getKey(), p.getValue()));
        }
        repo.updateAllClasses(allChanges.getLeft(), allChanges.getRight());
    }

    @Scheduled(cron = "0 * * * * ?")
    public void mail() {
        var all = repo.mailingTimes();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (Pair<Integer, Integer> p : all) {
            executor.submit(() -> performMailingTask(p));
        }
    }

    private void performMailingTask(Pair<Integer, Integer> p) {
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Yekaterinburg");
        Calendar calendar = Calendar.getInstance(timeZone);

        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        if (p.getRight() != null && p.getRight() == currentHour) {
            var sendToUser = repo.findAll(p.getLeft(), "1");
            client.sendMailing(p.getLeft(), sendToUser.getFirst());
        }
    }

    private void performNotificationTask(Integer chatId, List<ClassResponse> updates) {
        client.sendNotification(chatId, updates);
    }
}
