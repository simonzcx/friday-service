package com.friday.flowable.compont;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomPasswordEncoder implements PasswordEncoder {
    private static final Logger logger = LoggerFactory.getLogger(CustomPasswordEncoder.class);

    @Override
    public String encode(CharSequence charSequence) {
        logger.info("CustomPasswordEncoder:encode: {}", charSequence);
        return charSequence.toString();
    }

    @Override
    public boolean matches(CharSequence frontPsw, String sourcePsw) {
        logger.info("CustomPasswordEncoder:matches, frontPsw: {}; sourcePsw: {}", frontPsw, sourcePsw);
        return true;
    }
}
