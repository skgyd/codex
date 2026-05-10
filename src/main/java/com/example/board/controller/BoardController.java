package com.example.board.controller;

import com.example.board.service.BoardService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BoardController {
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("latestComments", boardService.getLatestComments(10));
        return "home";
    }

    @GetMapping("/posts/new")
    public String writeForm() { return "write"; }

    @PostMapping("/posts")
    public String createPost(@RequestParam @NotBlank String title,
                             @RequestParam @NotBlank String content,
                             @RequestParam @NotBlank @Size(max = 10) String password,
                             RedirectAttributes ra) {
        var post = boardService.createPost(title, content, password);
        ra.addFlashAttribute("message", "게시글이 등록되었습니다.");
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/posts/{postId}")
    public String postPage(@PathVariable long postId, Model model) {
        var postOpt = boardService.getPost(postId);
        if (postOpt.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("post", postOpt.get());
        model.addAttribute("comments", boardService.getCommentsByPost(postId));
        return "post";
    }

    @PostMapping("/posts/{postId}/edit")
    public String editPost(@PathVariable long postId, @RequestParam String title, @RequestParam String content,
                           @RequestParam @Size(max = 10) String password, RedirectAttributes ra) {
        if (!boardService.updatePost(postId, title, content, password)) {
            ra.addFlashAttribute("error", "게시글 비밀번호가 일치하지 않습니다.");
        }
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/posts/{postId}/delete")
    public String deletePost(@PathVariable long postId, @RequestParam @Size(max = 10) String password,
                             RedirectAttributes ra) {
        if (!boardService.deletePost(postId, password)) {
            ra.addFlashAttribute("error", "게시글 삭제 실패: 비밀번호를 확인하세요.");
            return "redirect:/posts/" + postId;
        }
        return "redirect:/";
    }

    @PostMapping("/posts/{postId}/comments")
    public String addComment(@PathVariable long postId, @RequestParam @NotBlank String content,
                             @RequestParam @NotBlank @Size(max = 10) String password,
                             RedirectAttributes ra) {
        if (!boardService.addComment(postId, content, password)) {
            ra.addFlashAttribute("error", "댓글 등록 실패");
        }
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/edit")
    public String editComment(@PathVariable long postId, @PathVariable long commentId,
                              @RequestParam String content, @RequestParam @Size(max = 10) String password,
                              RedirectAttributes ra) {
        if (!boardService.updateComment(postId, commentId, content, password)) {
            ra.addFlashAttribute("error", "댓글 수정 실패: 비밀번호를 확인하세요.");
        }
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable long postId, @PathVariable long commentId,
                                @RequestParam @Size(max = 10) String password, RedirectAttributes ra) {
        if (!boardService.deleteComment(postId, commentId, password)) {
            ra.addFlashAttribute("error", "댓글 삭제 실패: 비밀번호를 확인하세요.");
        }
        return "redirect:/posts/" + postId;
    }
}
