package com.videoplatform.auth.dto;

import java.time.Instant;
import java.util.List;

public record DashboardResponse(String name, String plan, Instant planExpiry,
                                int totalVideos, long storageUsedBytes, long storageSavedBytes,
                                int videosThisMonth, int monthlyLimit, List<VideoItem> recent) {

    public record VideoItem(String id, String originalName, String resolution, String status,
                            Long outputSize, Instant createdAt) {}
}
