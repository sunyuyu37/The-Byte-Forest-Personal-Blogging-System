package com.blog.repository;

import com.blog.entity.CommentLike;
import com.blog.entity.Comment;
import com.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    Optional<CommentLike> findByCommentIdAndUserId(Long commentId, Long userId);

    @Modifying
    @Query("DELETE FROM CommentLike cl WHERE cl.comment.id = :commentId AND cl.user.id = :userId")
    void deleteByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

    long countByUserId(Long userId);

    long countByCommentId(Long commentId);
}