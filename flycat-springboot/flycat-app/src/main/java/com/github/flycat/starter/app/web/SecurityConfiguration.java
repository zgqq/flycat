package com.github.flycat.starter.app.web;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration(proxyBeanMethods = false)
public class SecurityConfiguration {


    private String path(String path) {
        return path;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.securityMatcher(EndpointRequest.toAnyEndpoint());
//        http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());
////        http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());
//        return http.build();

        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
		successHandler.setTargetUrlParameter("redirectTo");
		successHandler.setDefaultTargetUrl(path("/"));
		http.authorizeHttpRequests(
				(authorizeRequests) -> authorizeRequests.requestMatchers(path("/assets/**")).permitAll() // <1>
						.requestMatchers(path("/actuator/info")).permitAll()
						.requestMatchers(path("/actuator/health")).permitAll()
                        .requestMatchers(path("/actuator/**")).authenticated()
						.requestMatchers(path("/login")).permitAll()
                        .anyRequest().permitAll() // <2>
		)
                .formLogin(
				(formLogin) ->
						formLogin.
								and()
//						formLogin.loginPage(path("/login"))
//								.successHandler(successHandler)
//						.and() // <3>
				)
//				.logout((logout) -> logout.logoutUrl(path("/logout")))
				.httpBasic(Customizer.withDefaults()) // <4>
				.csrf((csrf) -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // <5>
						.ignoringRequestMatchers(
								new AntPathRequestMatcher(path("/instances"),
										HttpMethod.POST.toString()), // <6>
								new AntPathRequestMatcher(path("/instances/*"),
										HttpMethod.DELETE.toString()), // <6>
								new AntPathRequestMatcher(path("/actuator/**")) // <7>
						))
				.rememberMe((rememberMe) -> rememberMe.key(UUID.randomUUID().toString()).tokenValiditySeconds(1209600));
        return http.build();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return new WebSecurityCustomizer() {
//
//            @Override
//            public void customize(WebSecurity web) {
////                web.ignoring().requestMatchers("/**");
//            }
//        };
//    }
}