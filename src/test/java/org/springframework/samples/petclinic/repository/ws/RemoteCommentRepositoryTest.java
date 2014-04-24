package org.springframework.samples.petclinic.repository.ws;

import fr.norsys.schemas.comment.CommentsType;
import fr.norsys.schemas.comment.FindCommentBySiteAndArtifactRequest;
import fr.norsys.schemas.comment.FindCommentBySiteAndArtifactResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.samples.petclinic.model.Comment;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RemoteCommentRepositoryTest {
    private RemoteCommentRepository repository;
    @Mock fr.norsys.schemas.comment.Comment service;

    @Before
    public void setUp() throws Exception {
        repository = new RemoteCommentRepository();
        repository.setService(service);
    }

    @Test
    public void return_model_comments_from_service_comments() throws Exception {
        //  Given
        final FindCommentBySiteAndArtifactResponse findCommentBySiteAndArtifactResponse = new FindCommentBySiteAndArtifactResponse();
        final CommentsType serviceComment = new CommentsType();
        serviceComment.setPseudo("Charlie Brown");
        serviceComment.setRate(5);
        serviceComment.setComment("Excellent");
        serviceComment.setDate("today");
        findCommentBySiteAndArtifactResponse.getComment().add(serviceComment);
        when(service.findCommentBySiteAndArtifact(any(FindCommentBySiteAndArtifactRequest.class)))
                .thenReturn(findCommentBySiteAndArtifactResponse);

        //  When
        Collection<Comment> comments = repository.findCommentsFor("itemId");

        //  Then
        assertThat(comments).isNotEmpty();
        assertThat(comments).hasSize(1);
        final Comment modelComment = new Comment();
        modelComment.setPseudo("Charlie Brown");
        modelComment.setRate(5);
        modelComment.setComment("Excellent");
        modelComment.setDate("today");
        assertThat(comments.iterator().next()).isEqualTo(modelComment);
    }
}