package com.homni.featuretoggle.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homni.generated.model.ErrorResponse;
import com.homni.generated.model.ErrorResponseAllOfPayload;
import com.homni.generated.model.ResponseMeta;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;

@Component
public class SecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper mapper;

    SecurityErrorHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String code;
        String message;

        if (isTokenExpired(authException)) {
            code = "TOKEN_EXPIRED";
            message = "Access token has expired";
        } else {
            code = "UNAUTHORIZED";
            message = "Authentication is required to access this resource";
        }

        writeError(response, HttpServletResponse.SC_UNAUTHORIZED, code, message);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        writeError(response, HttpServletResponse.SC_FORBIDDEN,
                "FORBIDDEN", "You do not have permission to access this resource");
    }

    private boolean isTokenExpired(AuthenticationException ex) {
        if (ex instanceof InvalidBearerTokenException && ex.getCause() instanceof JwtValidationException jwtEx) {
            return jwtEx.getErrors().stream()
                    .anyMatch(error -> error.getDescription() != null
                            && error.getDescription().contains("Jwt expired"));
        }
        return false;
    }

    private void writeError(HttpServletResponse response, int status,
                            String code, String message) throws IOException {
        ErrorResponseAllOfPayload payload = new ErrorResponseAllOfPayload(code, message);
        ErrorResponse body = new ErrorResponse(payload, new ResponseMeta().timestamp(OffsetDateTime.now()));

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getOutputStream(), body);
    }
}
