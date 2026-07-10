package com.devassistant.Notificationbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the AI Developer Assistant Platform.
 *
 * <p>Educational Note: This class uses the {@code @SpringBootApplication} annotation, which is a
 * convenience annotation that adds: - {@code @Configuration}: Tags the class as a source of bean
 * definitions for the application context. - {@code @EnableAutoConfiguration}: Tells Spring Boot to
 * start adding beans based on classpath settings, other beans, and various property settings. -
 * {@code @ComponentScan}: Tells Spring to look for other components, configurations, and services
 * in the 'com.devassistant' package, allowing it to find the controllers and processors.
 */
@SpringBootApplication
public class NotificationBotApplication {
  public static void main(String[] args) {
    SpringApplication.run(NotificationBotApplication.class, args);
  }
}
