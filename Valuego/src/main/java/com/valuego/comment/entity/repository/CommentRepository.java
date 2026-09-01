package com.valuego.comment.entity.repository;

import com.valuego.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.travelPlace.id = :travelPlaceId ORDER BY c.createdAt ASC")
    List<Comment> findAllByTravelPlaceIdWithUser(@Param("travelPlaceId") Long travelPlaceId);

}
