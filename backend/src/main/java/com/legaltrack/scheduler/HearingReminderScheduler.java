package com.legaltrack.scheduler;

import com.legaltrack.entity.CaseHearing;
import com.legaltrack.enums.HearingStatus;
import com.legaltrack.enums.NotificationType;
import com.legaltrack.repository.CaseHearingRepository;
import com.legaltrack.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HearingReminderScheduler {

    private final CaseHearingRepository hearingRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "${hearing.reminder.cron:0 0 9 * * *}")
    public void sendHearingReminders() {
        log.info("Running automated hearing reminder scheduler...");
        LocalDate today = LocalDate.now();

        // 7 days, 3 days, 1 day reminders
        checkAndSendReminders(today.plusDays(7), "Hearing in 7 Days");
        checkAndSendReminders(today.plusDays(3), "Hearing in 3 Days");
        checkAndSendReminders(today.plusDays(1), "Hearing Tomorrow");
    }

    private void checkAndSendReminders(LocalDate targetDate, String titlePrefix) {
        List<CaseHearing> hearings = hearingRepository.findByHearingDate(targetDate);

        for (CaseHearing hearing : hearings) {
            if (hearing.getStatus() == HearingStatus.SCHEDULED) {
                String msg = "Your case " + hearing.getCaseFile().getTitle() + " has a hearing scheduled on " + hearing.getHearingDate() +
                        (hearing.getHearingTime() != null ? " at " + hearing.getHearingTime() : "") + " in " +
                        (hearing.getCourt() != null ? hearing.getCourt().getName() : "Court");

                if (hearing.getCaseFile().getClient() != null) {
                    notificationService.createNotification(
                            hearing.getCaseFile().getClient(),
                            NotificationType.HEARING_REMINDER,
                            titlePrefix,
                            msg,
                            "CASE",
                            hearing.getCaseFile().getId(),
                            "/client/cases/" + hearing.getCaseFile().getId() + "/hearings"
                    );
                }

                if (hearing.getCaseFile().getLawyer() != null) {
                    notificationService.createNotification(
                            hearing.getCaseFile().getLawyer(),
                            NotificationType.HEARING_REMINDER,
                            titlePrefix,
                            msg,
                            "CASE",
                            hearing.getCaseFile().getId(),
                            "/lawyer/cases/" + hearing.getCaseFile().getId() + "/hearings"
                    );
                }
            }
        }
    }
}
