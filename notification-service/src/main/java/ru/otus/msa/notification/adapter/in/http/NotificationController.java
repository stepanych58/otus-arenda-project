package ru.otus.msa.notification.adapter.in.http;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.otus.msa.notification.api.http.dto.NotificationDto;
import ru.otus.msa.notification.application.NotificationService;

@RestController
@RequestMapping("/notification-service/api/v1")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("notification/list")
    public List<NotificationDto> getNotifications(@RequestParam("userId") UUID userId,
                                                  @RequestParam("orderId") UUID orderId) {
        return notificationService.getNotifications(userId, orderId);
    }
}
