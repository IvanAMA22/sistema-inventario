package com.universidad.inventario.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * Clase de configuración de Spring MVC para la internacionalización (i18n).
 * Habilita la resolución y el cambio dinámico de idioma mediante un parámetro.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Define el LocaleResolver que guardará el idioma en la sesión del usuario.
     * Si no se especifica, el idioma por defecto es el Español ("es").
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(new Locale("es"));
        return slr;
    }

    /**
     * Interceptor que detecta cambios de idioma a través de un parámetro en la URL
     * (por ejemplo, "?lang=en" o "?lang=es").
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    /**
     * Registra el interceptor de cambio de idioma en el registro de interceptores de Spring.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
