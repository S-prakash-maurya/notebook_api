package com.micro.subject.config;

import com.generic.service.model.GenericLoggedInUserData;
import com.generic.service.util.RequestContext;
import com.micro.subject.exception.SubjectException;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Single source of truth for "who is the currently authenticated user".
 *
 * ASSUMPTION: JwtAuthenticationFilter (already deployed for this
 * microservice, same as the auth service) populates RequestContext via
 * RequestContext.setUserFromRequestContextHolder(GenericLoggedInUserData)
 * on every authenticated request, exactly as shown in the auth service's
 * JwtAuthenticationFilter.doFilterInternal. That class only showed the
 * setter - I'm assuming a matching getter named
 * RequestContext.getUserFromRequestContextHolder(). If the real getter has
 * a different name (e.g. RequestContext.getUser()), this is the ONLY file
 * that needs to change; every controller/service in this module calls
 * through here rather than touching RequestContext directly.
 */
@Component
public class CurrentUserResolver {

    public UUID getCurrentUserId() {
        GenericLoggedInUserData user = RequestContext.getUserFromRequestContextHolder();
        if (user == null || user.getUserId() == null) {
            throw SubjectException.unauthorized();
        }
        return user.getUserId();
    }
}
