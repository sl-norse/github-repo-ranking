package com.sl.redcare.gitclient;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  Query to git search repo endpoint doesn't work with '+' sign encoded, didn't find other way to do this
 */
@Configuration
public class FeignEncodingFixConfig {

    @Bean
    public RequestInterceptor plusSignFixInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                if (template.url().startsWith("/search/repositories")) {
                    String url = template.url();

                    // Feign usually encodes '+' as %2B; revert it to literal '+'
                    String fixedUrl = url.replace("%2B", "+");

                    // set the corrected URL directly
                    template.uri(fixedUrl, false);
                }
            }
        };
    }
}
