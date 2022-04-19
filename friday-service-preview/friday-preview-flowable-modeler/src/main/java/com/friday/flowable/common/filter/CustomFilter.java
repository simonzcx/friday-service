package com.friday.flowable.common.filter;

import org.flowable.ui.common.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@WebFilter(urlPatterns = {"/app/**", "/api/**"})
public class CustomFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(CustomFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        if (skipAuthenticationCheck(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        logger.debug("MyFilter:doFilterInternal:" + request.getRequestURL());
        if (StringUtils.isEmpty(SecurityUtils.getCurrentUserId())) {
            logger.debug("MyFilter:doFilterInternal:校验......");
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("admin", "");
            SecurityContextHolder.getContext().setAuthentication(token);
        } else {
            logger.debug("MyFilter:doFilterInternal:校验通过.......");
        }

        filterChain.doFilter(request, response);
    }

    protected boolean skipAuthenticationCheck(HttpServletRequest request) {
        return request.getRequestURI().endsWith(".css") ||
                request.getRequestURI().endsWith(".js") ||
                request.getRequestURI().endsWith(".html") ||
                request.getRequestURI().endsWith(".map") ||
                request.getRequestURI().endsWith(".woff") ||
                request.getRequestURI().endsWith(".png") ||
                request.getRequestURI().endsWith(".jpg") ||
                request.getRequestURI().endsWith(".jpeg") ||
                request.getRequestURI().endsWith(".tif") ||
                request.getRequestURI().endsWith(".tiff");
    }
}
