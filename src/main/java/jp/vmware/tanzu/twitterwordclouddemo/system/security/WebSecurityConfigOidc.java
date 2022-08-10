package jp.vmware.tanzu.twitterwordclouddemo.system.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@Profile("stateless")
public class WebSecurityConfigOidc {

	private final ClientRegistrationRepository clientRegistrationRepository;

	public WebSecurityConfigOidc(ClientRegistrationRepository clientRegistrationRepository) {
		this.clientRegistrationRepository = clientRegistrationRepository;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.formLogin(login -> login.loginProcessingUrl("/login").loginPage("/login").defaultSuccessUrl("/tweets")
						.permitAll())
				.authorizeHttpRequests(
						authz -> authz.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
								.mvcMatchers("/", "/app.js", "/api/**", "/access-denied").permitAll().anyRequest()
								.authenticated())
				.logout().logoutSuccessHandler(oidcLogoutSuccessHandler()).logoutSuccessUrl("/").and()
				.oauth2Login(withDefaults()).oauth2Client(withDefaults());
		return http.build();
	}

	private LogoutSuccessHandler oidcLogoutSuccessHandler() {
		OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(
				this.clientRegistrationRepository);

		// Sets the location that the End-User's User Agent will be redirected to
		// after the logout has been performed at the Provider
		oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");

		return oidcLogoutSuccessHandler;
	}

}
