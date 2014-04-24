package org.springframework.samples.petclinic.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Comment;

import java.util.Collection;

public interface CommentRepository {
    Collection<Comment> findCommentsFor(String itemId) throws DataAccessException;
}
