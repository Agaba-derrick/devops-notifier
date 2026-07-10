package com.devassistant.processor;

/**
 * EventProcessor defines the contract for processing different types of CI/CD events.
 *
 * <p>Educational Note: This interface is the core of the Strategy Design Pattern used in this
 * platform. By defining a common interface, the main Webhook controller doesn't need to know the
 * specifics of how to handle a Pull Request versus a Build Failure. It can simply iterate through a
 * list of EventProcessors, ask each one if it {@code supports()} the event, and if so, call {@code
 * processEvent()}. This makes the system highly extensible: to add a new event type, you simply
 * create a new class implementing this interface, without modifying the core logic.
 */
public interface EventProcessor {

  /**
   * Checks if this processor supports the given event type.
   *
   * @param eventType The event type (e.g. pull_request, build_failure)
   * @return true if supported, false otherwise
   */
  boolean supports(String eventType);

  /**
   * Processes the given event payload.
   *
   * @param payload The event payload as a String (typically JSON)
   */
  void processEvent(String payload);
}
