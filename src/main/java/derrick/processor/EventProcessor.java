package com.derick.notificationbot.processor;

/**
 * EventProcessor defines the contract for processing different types of CI/CD events.
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
`
