package com.lizi.security;

//<<<<<<< HEAD
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//=======
import org.springframework.context.annotation.Configuration;
//>>>>>>> 2a12183304e6c036bb77613997c8e586d60251cd
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//<<<<<<< HEAD
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import com.lizi.security.oauth.CustomerOAuth2UserService;
//import com.lizi.security.oauth.OAuth2LoginSuccessHandler;
//=======
//>>>>>>> 2a12183304e6c036bb77613997c8e586d60251cd

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//<<<<<<< HEAD
//	@Autowired private CustomerOAuth2UserService oAuth2UserService;
//	@Autowired private OAuth2LoginSuccessHandler oauth2LoginHandler;
//	@Autowired private DatabaseLoginSuccessHandler databaseLoginHandler;
//	
//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
//	
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http.authorizeRequests()
//			.antMatchers("/account_details", "/update_account_details", "/orders/**",
//					"/cart", "/address_book/**", "/checkout", "/place_order", "/reviews/**", 
//					"/process_paypal_order", "/write_review/**", "/post_review").authenticated()
//			.anyRequest().permitAll()
//			.and()
//			.formLogin()
//				.loginPage("/login")
//				.usernameParameter("email")
//				.successHandler(databaseLoginHandler)
//				.permitAll()
//			.and()
//			.oauth2Login()
//				.loginPage("/login")
//				.userInfoEndpoint()
//				.userService(oAuth2UserService)
//				.and()
//				.successHandler(oauth2LoginHandler)
//			.and()
//			.logout().permitAll()
//			.and()
//			.rememberMe()
//				.key("1234567890_aBcDeFgHiJkLmNoPqRsTuVwXyZ")
//				.tokenValiditySeconds(14 * 24 * 60 * 60)
//			.and()
//				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
//			;			
//=======
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().permitAll();
			
//>>>>>>> 2a12183304e6c036bb77613997c8e586d60251cd
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
	}

//<<<<<<< HEAD
//	@Bean
//	public UserDetailsService userDetailsService() {
//		return new CustomerUserDetailsService();
//	}
//	
//	@Bean
//	public DaoAuthenticationProvider authenticationProvider() {
//		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//
//		authProvider.setUserDetailsService(userDetailsService());
//		authProvider.setPasswordEncoder(passwordEncoder());
//
//		return authProvider;
//	}	
//=======
//>>>>>>> 2a12183304e6c036bb77613997c8e586d60251cd
}
