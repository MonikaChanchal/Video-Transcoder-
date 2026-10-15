package com.videoplatform.auth.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/** Transcoding feature is collection me videos save karega; dashboard sirf padhta h. */
@Getter
@Setter
@NoArgsConstructor
@Document(collection = "videos")
public class Video {
    @Id
    private String id;
    private String userId;
    private String originalName;
    private String resolution;
    private String status;        // QUEUED, PROCESSING, COMPLETED, FAILED
    private long originalSize;    // bytes
    private Long outputSize;      // bytes, complete hone par
    private Instant createdAt = Instant.now();
}
