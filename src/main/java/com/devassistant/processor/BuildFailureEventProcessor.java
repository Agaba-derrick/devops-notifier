package com.devassistant.processor;

import com.devassistant.logging.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Processes incoming Build Failure events and uses AI to explain the failure.
 *
 * <p>Educational Note: This processor extracts the raw CI logs from the webhook payload. It
 * leverages the LLM as an expert systems engineer to analyze the stack trace or output, identify
 * the most likely root cause, assign a confidence score, and suggest fixes.
 */
@Component
public class BuildFailureEventProcessor implements EventProcessor {

  private static final String SUPPORTED_EVENT = "build_failure";

  private final ObjectMapper objectMapper;
  private final Logger logger;
  private final ChatClient chatClient;

  public BuildFailureEventProcessor(Logger logger, ChatClient.Builder chatClientBuilder) {
    this.objectMapper = new ObjectMapper();
    this.logger = logger;
    this.chatClient = chatClientBuilder.build();
  }

  @Override
  public boolean supports(String eventType) {
    return SUPPORTED_EVENT.equalsIgnoreCase(eventType);
  }

  @Override
  public void processEvent(String payload) {
    try {
      JsonNode root = objectMapper.readTree(payload);
      JsonNode repoNode = root.path("repository");
      JsonNode logsNode = root.path("logs");

      if (logsNode.isMissingNode() || repoNode.isMissingNode()) {
        logger.warn("Missing logs or repository data in build failure payload.");
        return;
      }

      String repoName = repoNode.asText();
      String logs = logsNode.asText();

      // Truncate logs to avoid exceeding LLM context limits (e.g., last 2000 chars)
      if (logs.length() > 2000) {
        logs = logs.substring(logs.length() - 2000);
      }

      String aiPrompt =
          String.format(
              "You are a Senior DevOps Engineer. Analyze the following CI build failure logs for repository '%s'.\n"
                  + "Please provide a structured response with exactly these three sections:\n"
                  + "Likely Cause:\n"
                  + "Confidence:\n"
                  + "Possible Fixes:\n\n"
                  + "Logs:\n%s",
              repoName, logs);

      String aiExplanation = "AI Explanation not available";
      try {
        aiExplanation = chatClient.prompt().user(aiPrompt).call().content();
      } catch (Exception aiException) {
        logger.error("Failed to generate AI explanation for build failure", aiException);
      }

      logger.info("=== BUILD FAILURE DETECTED in " + repoName + " ===");
      logger.info("AI Analysis:\n" + aiExplanation);
      logger.info("===========================================");

    } catch (Exception e) {
      logger.error("Failed to process build failure event", e);
    }
  }
}
