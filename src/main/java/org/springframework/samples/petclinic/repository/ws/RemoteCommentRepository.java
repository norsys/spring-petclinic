package org.springframework.samples.petclinic.repository.ws;

import lombok.Setter;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Comment;
import org.springframework.samples.petclinic.repository.CommentRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

import static java.util.Collections.emptyList;

/**
 * Remote Web Service {@link org.springframework.samples.petclinic.repository.CommentRepository} implementation.
 */
@Repository
public class RemoteCommentRepository implements CommentRepository {
    @Setter private fr.norsys.schemas.comment.Comment service;

    @Override
    public Collection<Comment> findCommentsFor(String itemId) throws DataAccessException {
        //  Insert your code here.
        return emptyList();
    }
}
