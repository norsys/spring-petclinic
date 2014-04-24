package fr.norsys.schemas.comment;

import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

/**
 * Provide to Spring framework a way to create instances of {@link fr.norsys.schemas.comment.Comment}.
 */
public class CommentServicePortFactoryBean implements FactoryBean<Comment>, InitializingBean {
    /** Required WSDL Location. */
    @Setter private Resource wsdlLocation;

    /** Optional endpoint address. If null, the service port address defined in WSDL is used -NOT RECOMMENDED-. */
    @Setter private URL endPointUrl;

    /** Optional connection timeout in ms. */
    @Setter private int connectionTimeout;

    /** Optional request timeout in ms. */
    @Setter private int requestTimeout;

    /** Optional add some handlers to the stub. */
    @Setter private List<Handler> handlers = newArrayList();

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(wsdlLocation, "WSDL Location is required!");
    }

    @Override
    public Class<?> getObjectType() {
        return Comment.class;
    }

    /**
     * @return false, each call to {@link #getObject()} will create
     * new instances of {@link fr.norsys.schemas.comment.Comment}.
     */
    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public Comment getObject() throws Exception {
        @SuppressWarnings("unchecked")
        Comment comment = (Comment) initialize((BindingProvider) new CommentsService(wsdlLocation.getURL()).getCommentSoap11());
        return comment;
    }

    private BindingProvider initialize(final BindingProvider bindingProvider) {
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        if (endPointUrl != null) {
            requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endPointUrl.toExternalForm());
        }
        if (connectionTimeout > 0) {
            requestContext.put("com.sun.xml.internal.ws.connect.timeout", connectionTimeout);
            requestContext.put("com.sun.xml.ws.connect.timeout", connectionTimeout);
        }
        if (requestTimeout > 0) {
            requestContext.put("com.sun.xml.internal.ws.request.timeout", requestTimeout);
            requestContext.put("com.sun.xml.ws.request.timeout", requestTimeout);
        }
        addHandlers(bindingProvider);
        return bindingProvider;
    }

    private void addHandlers(final BindingProvider bindingProvider) {
        Binding binding = bindingProvider.getBinding();
        for (Handler handler : handlers) {
            addHandler(binding, handler);
        }
    }

    private void addHandler(final Binding binding, final Handler handler) {
        List<Handler> handlerChain = binding.getHandlerChain();
        // Prevents multiple inclusion
        for (Handler chainedHandler : handlerChain) {
            if (chainedHandler.getClass().equals(handler.getClass())) {
                return;
            }
        }
        handlerChain.add(handler);
        binding.setHandlerChain(handlerChain);
    }
}
