package study.ywork.security.config;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MySecurityContextFactory.class)
public @interface WithCustomUser {

    String username();

    String password();
}

