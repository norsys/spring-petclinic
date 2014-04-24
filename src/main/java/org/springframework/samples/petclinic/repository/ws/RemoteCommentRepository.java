package org.springframework.samples.petclinic.repository.ws;

import com.google.common.base.Function;
import fr.norsys.schemas.comment.CommentsType;
import fr.norsys.schemas.comment.FindCommentBySiteAndArtifactRequest;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.samples.petclinic.model.Comment;
import org.springframework.samples.petclinic.repository.CommentRepository;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;
import java.net.SocketTimeoutException;
import java.util.Collection;

import static com.google.common.collect.Collections2.transform;
import static java.util.Collections.emptyList;

@Repository
public class RemoteCommentRepository implements CommentRepository {
    @Autowired @Setter private fr.norsys.schemas.comment.Comment service;

    @Override
    public Collection<Comment> findCommentsFor(String itemId) throws DataAccessException {
        FindCommentBySiteAndArtifactRequest request = new FindCommentBySiteAndArtifactRequest();
        request.setSiteId("spring-pet-clinic");
        request.setArtifactId(itemId);
        try {
            return transform(service.findCommentBySiteAndArtifact(request).getComment(), new Function<CommentsType, Comment>() {
                @Override
                public Comment apply(final CommentsType input) {
                    Comment comment = new Comment();
                    comment.setComment(input.getComment());
                    comment.setDate(input.getDate().toString());
                    comment.setPseudo(input.getPseudo());
                    comment.setRate(input.getRate());
                    return comment;
                }
            });
        } catch (WebServiceException cause) {
            try {
                throw cause.getCause();
            } catch (SocketTimeoutException timeout) {
                throw new QueryTimeoutException(timeout.getMessage(), timeout);
            } catch (Throwable unknownCause) {
                return emptyList();
            }
        }
    }
}
