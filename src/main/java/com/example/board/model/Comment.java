package com.example.board.model;

import java.time.LocalDateTime;

public class Comment {
    private final long id;
    private final long postId;
    private String content;
    private String password;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Comment(long id, long postId, String content, String password) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public long getId() { return id; }
    public long getPostId() { return postId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
