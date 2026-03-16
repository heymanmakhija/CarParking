package com.practise.parking.lot.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ApiRequestLoggingFilter extends OncePerRequestFilter {

    private static final int MAX_PAYLOAD_LENGTH = 2048;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            log.info(
                    "API request received: method={}, path={}, queryParams={}, body={}",
                    wrappedRequest.getMethod(),
                    wrappedRequest.getRequestURI(),
                    formatParameters(wrappedRequest.getParameterMap()),
                    extractPayload(wrappedRequest)
            );
            log.info(
                    "API response sent: method={}, path={}, status={}, body={}",
                    wrappedRequest.getMethod(),
                    wrappedRequest.getRequestURI(),
                    wrappedResponse.getStatus(),
                    extractPayload(wrappedResponse)
            );
            wrappedResponse.copyBodyToResponse();
        }
    }

    private String formatParameters(Map<String, String[]> parameterMap) {
        if (parameterMap == null || parameterMap.isEmpty()) {
            return "{}";
        }

        return parameterMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Arrays.toString(entry.getValue())))
                .toString();
    }

    private String extractPayload(ContentCachingRequestWrapper request) {
        return toPayloadString(request.getContentAsByteArray());
    }

    private String extractPayload(ContentCachingResponseWrapper response) {
        return toPayloadString(response.getContentAsByteArray());
    }

    private String toPayloadString(byte[] payload) {
        if (payload.length == 0) {
            return "";
        }

        int payloadLength = Math.min(payload.length, MAX_PAYLOAD_LENGTH);
        String body = new String(payload, 0, payloadLength, StandardCharsets.UTF_8);
        return StringUtils.hasText(body) ? body : "";
    }
}
