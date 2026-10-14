package com.videoplatform.auth.service;

import com.videoplatform.auth.dto.DashboardResponse;
import com.videoplatform.auth.dto.DashboardResponse.VideoItem;
import com.videoplatform.auth.exception.ApiException;
import com.videoplatform.auth.model.User;
import com.videoplatform.auth.model.Video;
import com.videoplatform.auth.repository.UserRepository;
import com.videoplatform.auth.repository.VideoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class DashboardService {
    // Demo limits: project ke hisaab se badal sakte ho
    public static final int FREE_MONTHLY_LIMIT = 5;
    public static final int PREMIUM_MONTHLY_LIMIT = 1000;

    private final UserRepository users;
    private final VideoRepository videos;
    private final SubscriptionService subscriptions;

    public DashboardService(UserRepository users, VideoRepository videos, SubscriptionService subscriptions) {
        this.users = users;
        this.videos = videos;
        this.subscriptions = subscriptions;
    }

    public DashboardResponse get(String email) {
        User u = users.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
        subscriptions.refresh(u);

        List<Video> all = videos.findByUserIdOrderByCreatedAtDesc(u.getId());
        Instant monthStart = YearMonth.now(ZoneOffset.UTC).atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        long used = all.stream().filter(v -> v.getOutputSize() != null).mapToLong(Video::getOutputSize).sum();
        long saved = all.stream()
                .filter(v -> "COMPLETED".equals(v.getStatus()) && v.getOutputSize() != null)
                .mapToLong(v -> Math.max(0, v.getOriginalSize() - v.getOutputSize())).sum();
        int thisMonth = (int) all.stream().filter(v -> v.getCreatedAt().isAfter(monthStart)).count();

        List<VideoItem> recent = all.stream().limit(5)
                .map(v -> new VideoItem(v.getId(), v.getOriginalName(), v.getResolution(), v.getStatus(),
                        v.getOutputSize(), v.getCreatedAt()))
                .toList();

        boolean premium = subscriptions.isPremiumActive(u);
        return new DashboardResponse(u.getName(), premium ? "PREMIUM" : "FREE", u.getSubscriptionExpiry(),
                all.size(), used, saved, thisMonth,
                premium ? PREMIUM_MONTHLY_LIMIT : FREE_MONTHLY_LIMIT, recent);
    }
}
