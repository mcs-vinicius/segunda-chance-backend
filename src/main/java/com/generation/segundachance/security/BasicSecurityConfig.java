package com.generation.segundachance.security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Arrays; // Importação adicionada

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Importações adicionadas para o CORS
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class BasicSecurityConfig {

	@Autowired
	private JwtAuthFilter authFilter;

	@Bean
	UserDetailsService userDetailsService() {

		return new UserDetailsServiceImpl();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	/**
	 * Novo Bean para configurar o CORS globalmente.
	 * O método filterChain() irá usar este Bean automaticamente
	 * por causa da chamada .cors(withDefaults()).
	 */
	@Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        
        // Define as origens permitidas (seu frontend no Vercel e localhost)
        configuration.setAllowedOrigins(Arrays.asList(
            "https://segunda-chance-pi.vercel.app", 
            "http://localhost:3000",
            "http://localhost:5173" 
        ));
        
        // Define os métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        
        // Define os cabeçalhos permitidos (importante para Autorização/JWT)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Origin", "Accept"));
        
        // Permite o envio de credenciais (como cookies ou tokens)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        // Aplica esta configuração a TODAS as rotas (/**)
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.csrf(csrf -> csrf.disable())
				.cors(withDefaults()); // Esta linha agora usará o Bean corsConfigurationSource

		http.authorizeHttpRequests((auth) -> auth.requestMatchers("/usuarios/logar", "/usuarios/cadastrar", "/error/**").permitAll()
				.requestMatchers("/categorias").permitAll()
				.requestMatchers("/categorias/{id}").permitAll()
				.requestMatchers("/categorias/tipo/{tipo}").permitAll()
				.requestMatchers("/categorias/nomeCategoria/{nomeCategoria}").permitAll()
				.requestMatchers("/produto").permitAll()
				.requestMatchers("/produto/{id}").permitAll()
				.requestMatchers("/produto/nomeProduto/{nomeProduto}").permitAll()
				.requestMatchers(HttpMethod.OPTIONS).permitAll().anyRequest().authenticated())
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class).httpBasic(withDefaults());

		return http.build();
	}
}
