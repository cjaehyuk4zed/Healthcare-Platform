package platform.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import platform.repository.TokensRepository;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@AllArgsConstructor
@Service
@Transactional
@Slf4j
public class ScheduledService {

    private final TokensRepository tokensRepository;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // Create method to automatically clear expired refresh tokens

    // Create method to automatically clear posts that are no longer liked

    // Create method to automatically clear posts that are no longer faved


    // Temporary disabled code as there seems to be an error somewhere...

//    @Value("${spring.security.jwt.refresh-token.expiration}")
//    private Long refreshExpiration;
//
//    private Long refreshExpirationHours = (refreshExpiration / 3600000);
//
//    // Currently deleting tokens automatically at 12:15pm everyday
//    @Scheduled(cron = "0 15 12 * * ?")
//    public void deleteExpiredRefreshTokens(){
//        List<Tokens> tokens = tokensRepository.findAll();
//        for(Tokens t : tokens){
//            Duration duration = Duration.between(t.getTimestamp(), LocalDateTime.now());
//            if(duration.toHours()<refreshExpirationHours){ tokens.remove(t);}
//        }
//        tokensRepository.deleteAll(tokens);
//    }

//
//    @Bean
//    public void scheduledTaskBean(){
//        log.info("ScheduledService RunnableTask");
//        final Runnable task = () -> {
//            PostingInfoDTO postInfoDTO = new PostingInfoDTO();
//            String uuid = UUID.randomUUID().toString();
//            postInfoDTO.setPostId(uuid);
//            postInfoDTO.setUserId("admin");
//            postInfoDTO.setPostContent("__Auto-Generated Text by ScheduledExecutorService");
//            postInfoDTO.setPostSaved(false);
//            postInfoDTO.setTitle("_generatedTitle");
//            postInfoDTO.setSubtitle("_generatedSubtitle");
//            postInfoDTO.setCategory("_generatedCategory");
//            postInfoDTO.setSubcategory("_generatedSubcategory");
//
//            postInfoService.savePost(postInfoDTO);
//        };
//        // Schedule the task to run daily at 12:15:00 (PM)
//        scheduledTask(task, 12, 35, 0);
//    }
//
//    public void scheduledTask(Runnable task, int hour, int minute, int second){
//        log.info("scheduledTask method");
//        // Get the current time
//        long currentTimeMillis = System.currentTimeMillis();
//
//        // Get the target time for the task
//        long targetTimeMillis = calculateTargetTime(hour, minute, second);
//
//        // Calculate the initial delay until the target time
//        long initialDelay = targetTimeMillis - currentTimeMillis;
//
//        // If the initial delay is negative, add 24 hours to schedule it for the next day
//        if (initialDelay < 0) {
//            initialDelay += TimeUnit.DAYS.toMillis(1);
//        }
//
//        // Schedule the task to run daily at the specified time
//        scheduler.scheduleAtFixedRate(task, initialDelay, 24, TimeUnit.HOURS);
//    }
//
//    private long calculateTargetTime(int hour, int minute, int second) {
//        log.info("calculateTargetTime method");
//        // Get the current time in milliseconds
//        long currentTimeMillis = System.currentTimeMillis();
//
//        // Get the current time in hours, minutes, and seconds
//        long currentHourMillis = TimeUnit.HOURS.toMillis(hour);
//        long currentMinuteMillis = TimeUnit.MINUTES.toMillis(minute);
//        long currentSecondMillis = TimeUnit.SECONDS.toMillis(second);
//
//        // Calculate the target time for the task in milliseconds
//        return currentTimeMillis - (currentTimeMillis % TimeUnit.DAYS.toMillis(1)) +
//                currentHourMillis + currentMinuteMillis + currentSecondMillis;
//    }

}
