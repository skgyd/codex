package com.example.board.service;

import com.example.board.model.Comment;
import com.example.board.model.Post;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BoardService {
    private final Map<Long, Post> posts = new ConcurrentHashMap<>();
    private final AtomicLong postSeq = new AtomicLong(1);
    private final AtomicLong commentSeq = new AtomicLong(1);

    public Post createPost(String title, String content, String password) {
        Post post = new Post(postSeq.getAndIncrement(), title, content, password);
        posts.put(post.getId(), post);
        return post;
    }

    public Optional<Post> getPost(long id) {
        return Optional.ofNullable(posts.get(id));
    }

    public List<Comment> getLatestComments(int limit) {
        return posts.values().stream()
                .flatMap(post -> post.getComments().stream())
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .limit(limit)
                .toList();
    }

    public List<Comment> getCommentsByPost(long postId) {
        Post post = posts.get(postId);
        if (post == null) {
            return List.of();
        }
        List<Comment> comments = new ArrayList<>(post.getComments());
        comments.sort(Comparator.comparing(Comment::getCreatedAt).reversed());
        return comments;
    }

    public boolean updatePost(long postId, String title, String content, String password) {
        Post post = posts.get(postId);
        if (post == null || !post.getPassword().equals(password)) {
            return false;
        }
        post.setTitle(title);
        post.setContent(content);
        post.setUpdatedAt(LocalDateTime.now());
        return true;
    }

    public boolean deletePost(long postId, String password) {
        Post post = posts.get(postId);
        if (post == null || !post.getPassword().equals(password)) {
            return false;
        }
        posts.remove(postId);
        return true;
    }

    public boolean addComment(long postId, String content, String password) {
        Post post = posts.get(postId);
        if (post == null) {
            return false;
        }
        post.getComments().add(new Comment(commentSeq.getAndIncrement(), postId, content, password));
        return true;
    }

    public boolean updateComment(long postId, long commentId, String content, String password) {
        Post post = posts.get(postId);
        if (post == null) {
            return false;
        }
        return post.getComments().stream()
                .filter(c -> c.getId() == commentId)
                .findFirst()
                .map(c -> {
                    if (!c.getPassword().equals(password)) {
                        return false;
                    }
                    c.setContent(content);
                    c.setUpdatedAt(LocalDateTime.now());
                    return true;
                }).orElse(false);
    }

    public boolean deleteComment(long postId, long commentId, String password) {
        Post post = posts.get(postId);
        if (post == null) {
            return false;
        }
        return post.getComments().removeIf(c -> c.getId() == commentId && c.getPassword().equals(password));
    }
}
