package com.erp.modules.platform.service;

import com.erp.modules.platform.dto.CommentResponse;
import com.erp.modules.platform.entity.Comment;
import com.erp.modules.platform.repository.CommentRepository;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

  private final CommentRepository commentRepository;
  private final NotificationService notificationService;

  private static final Pattern MENTION_PATTERN = Pattern.compile("@(\\w+)");

  public CommentService(CommentRepository commentRepository,
                        NotificationService notificationService) {
    this.commentRepository = commentRepository;
    this.notificationService = notificationService;
  }

  @Transactional
  public CommentResponse addComment(String module, String recordId, String author,
                                   String body, String parentId) {
    Comment c = new Comment();
    c.setModule(module);
    c.setRecordId(recordId);
    c.setAuthor(author);
    c.setBody(body);
    c.setParentId(parentId);
    String mentions = extractMentions(body);
    c.setMentions(mentions);
    Comment saved = commentRepository.save(c);

    if (mentions != null && !mentions.isEmpty()) {
      for (String mentioned : mentions.split(",")) {
        notificationService.send(
            "You were mentioned in a comment",
            author + " mentioned you: " + body.substring(0, Math.min(body.length(), 100)),
            "INFO", "NORMAL", mentioned.trim(), module, recordId
        );
      }
    }

    return toResponse(saved);
  }

  public CommentResponse reply(String module, String recordId, String parentId,
                              String author, String body) {
    return addComment(module, recordId, author, body, parentId);
  }

  public List<CommentResponse> getComments(String module, String recordId) {
    return commentRepository.findByModuleAndRecordIdOrderByCreatedAtAsc(module, recordId)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  public List<CommentResponse> getReplies(String parentId) {
    return commentRepository.findByParentIdOrderByCreatedAtAsc(parentId)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  @Transactional
  public void deleteComment(UUID id) {
    commentRepository.deleteById(id);
  }

  private String extractMentions(String body) {
    if (body == null) return null;
    Matcher m = MENTION_PATTERN.matcher(body);
    StringBuilder sb = new StringBuilder();
    while (m.find()) {
      if (sb.length() > 0) sb.append(",");
      sb.append(m.group(1));
    }
    return sb.length() > 0 ? sb.toString() : null;
  }

  private CommentResponse toResponse(Comment c) {
    CommentResponse r = new CommentResponse();
    r.setId(c.getId());
    r.setModule(c.getModule());
    r.setRecordId(c.getRecordId());
    r.setAuthor(c.getAuthor());
    r.setBody(c.getBody());
    r.setParentId(c.getParentId());
    r.setMentions(c.getMentions());
    r.setCreatedAt(c.getCreatedAt());
    return r;
  }
}
