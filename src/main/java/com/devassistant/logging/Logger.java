package com.devassistant.logging;

public interface Logger {
  void info(String message);

  void warn(String message);

  void error(String message, Throwable t);
}
