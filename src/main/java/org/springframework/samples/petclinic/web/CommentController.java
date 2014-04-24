package org.springframework.samples.petclinic.web;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.samples.petclinic.model.Comment;
import org.springframework.samples.petclinic.repository.CommentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

import static java.util.Collections.emptyList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class CommentController {
    private boolean propagate = true;

    @Autowired @Setter private CommentRepository commentRepository;

    @RequestMapping(method = GET, value = "/comments/{itemId}", produces = "application/json")
    @ResponseBody
    public Collection<Comment> getCommentsForItem(@PathVariable("itemId") String itemId, HttpServletResponse response) throws IOException{
        try {
            return commentRepository.findCommentsFor(itemId);
        } catch (QueryTimeoutException cause) {
            if (propagate) {
                response.sendError(HttpServletResponse.SC_GATEWAY_TIMEOUT, cause.getMessage());
                return null;
            } else {
                return emptyList();
            }
        } catch (DataAccessException cause) {
            if (propagate) {
                response.sendError(HttpServletResponse.SC_BAD_GATEWAY, cause.getMessage());
                return null;
            } else {
                return emptyList();
            }
        }
    }
}
