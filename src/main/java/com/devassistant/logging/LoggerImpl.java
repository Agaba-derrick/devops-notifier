package com.devassistant.logging;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggerImpl implements Logger {
  private static final org.slf4j.Logger log = LoggerFactory.getLogger(LoggerImpl.class);

  @Override
  public void info(String message) {
    log.info(message);
  }

  @Override
  public void warn(String message) {
    log.warn(message);
  }

  @Override
  public void error(String message, Throwable t) {
    log.error(message, t);
  }
}
