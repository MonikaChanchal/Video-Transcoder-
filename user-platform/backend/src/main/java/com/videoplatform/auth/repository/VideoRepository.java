package com.videoplatform.auth.repository;

import com.videoplatform.auth.model.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VideoRepository extends MongoRepository<Video, String> {
    List<Video> findByUserIdOrderByCreatedAtDesc(String userId);
}
