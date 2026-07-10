package com.devassistant.processor;

import com.devassistant.logging.Logger;
import com.devassistant.pullrequest.PullRequestService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Processes incoming Pull Request events and utilizes AI to summarize the changes.
 *
 * <p>Educational Note: This class implements the {@link EventProcessor} interface. We use Spring
 * AI's {@link ChatClient} to interact with the configured LLM (e.g., OpenAI). By injecting {@code
 * ChatClient.Builder}, we can create a client that sends the PR title and author to the AI to
 * generate a risk assessment and summary.
 */
@Component
public class PullRequestEventProcessor implements EventProcessor {

  private static final String SUPPORTED_EVENT = "pull_request";

  private final ObjectMapper objectMapper;
  private final PullRequestService pullRequestService;
  private final Logger logger;
  private final ChatClient chatClient;

  public PullRequestEventProcessor(
      PullRequestService pullRequestService, Logger logger, ChatClient.Builder chatClientBuilder) {
    this.objectMapper = new ObjectMapper();
    this.pullRequestService = pullRequestService;
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
      JsonNode actionNode = root.path("action");
      JsonNode pullRequestNode = root.path("pull_request");

      if (actionNode.isMissingNode() || pullRequestNode.isMissingNode()) {
        logger.warn("Missing pull request action or data in payload.");
        return;
      }

      String action = actionNode.asText();
      String prTitle = pullRequestNode.path("title").asText();
      String prUrl = pullRequestNode.path("html_url").asText();
      String author = pullRequestNode.path("user").path("login").asText();

      // AI Integration: Generate PR Summary
      String aiPrompt =
          String.format(
              "Analyze this PR title and provide a 2 sentence summary and risk assessment (Low/Medium/High). Title: '%s', Author: '%s'",
              prTitle, author);

      String aiSummary = "AI Summary not available";
      try {
        aiSummary = chatClient.prompt().user(aiPrompt).call().content();
      } catch (Exception aiException) {
        logger.error("Failed to generate AI summary for PR", aiException);
      }

      pullRequestService.handlePullRequest(
          action, prTitle + " - AI Analysis: " + aiSummary, prUrl, author);

      logger.info(
          String.format(
              "Processed pull request event: action=%s, title=%s, author=%s",
              action, prTitle, author));

    } catch (Exception e) {
      logger.error("Failed to process pull request event", e);
    }
  }
}
