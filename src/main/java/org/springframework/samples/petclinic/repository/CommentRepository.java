package org.springframework.samples.petclinic.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Comment;

import java.util.Collection;

/**
 * Repository responsible of listing site comments.
 */
public interface CommentRepository {
    /**
     * @param itemId Item identifier.
     * @return Returns all comments for a site item.
     * @throws DataAccessException An error may occurs during data access.
     */
    Collection<Comment> findCommentsFor(String itemId) throws DataAccessException;
}
