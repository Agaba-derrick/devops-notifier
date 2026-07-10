package com.devassistant.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devassistant.logging.Logger;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * Integration test for {@link BuildFailureEventProcessor}.
 *
 * <p>Educational Note: Aggressive assertions are essential in a good integration test. The goal is
 * NOT just to check that the code "doesn't crash" ({@code assertDoesNotThrow}). The goal is to
 * verify the exact behaviour of the system:
 *
 * <ul>
 *   <li>What exact values were logged? (via {@code ArgumentCaptor})
 *   <li>How many times was a method called? (via {@code verify(mock, times(N))})
 *   <li>Was a method NOT called when it shouldn't be? (via {@code verify(mock, never())})
 *   <li>Does the logged output actually contain the AI response?
 * </ul>
 *
 * <p>This philosophy ensures that if a developer accidentally silences the logger or changes the
 * format of the AI prompt, the test will catch it immediately.
 */
@SpringBootTest
class BuildFailureEventProcessorIT {

  private static final String EXPECTED_AI_RESPONSE =
      "Likely Cause: NullPointerException in DAO layer\n"
          + "Confidence: 95%\n"
          + "Possible Fixes: Add null check before the DB call";

  private static final String REPO_NAME = "devops-notifier";

  @Autowired private BuildFailureEventProcessor processor;

  @MockBean private ChatClient.Builder chatClientBuilderMock;

  @MockBean private Logger logger;

  private ChatClient chatClientMock;
  private ChatClient.ChatClientRequest chatClientRequestMock;
  private ChatClient.ChatClientRequest.CallResponseSpec callResponseSpecMock;

  /**
   * Wire up the mock chain before each test. Using @BeforeEach avoids duplication and ensures every
   * test gets a fresh, predictable mock state.
   */
  @BeforeEach
  void setUpChatClientMock() {
    chatClientMock = mock(ChatClient.class);
    chatClientRequestMock = mock(ChatClient.ChatClientRequest.class);
    callResponseSpecMock = mock(ChatClient.ChatClientRequest.CallResponseSpec.class);

    when(chatClientBuilderMock.build()).thenReturn(chatClientMock);
    when(chatClientMock.prompt()).thenReturn(chatClientRequestMock);
    when(chatClientRequestMock.user(anyString())).thenReturn(chatClientRequestMock);
    when(chatClientRequestMock.call()).thenReturn(callResponseSpecMock);
    when(callResponseSpecMock.content()).thenReturn(EXPECTED_AI_RESPONSE);
  }

  @Test
  void supports_shouldReturnTrueForBuildFailureEvent() {
    assertTrue(processor.supports("build_failure"));
  }

  @Test
  void supports_shouldBeCaseInsensitive() {
    assertTrue(processor.supports("BUILD_FAILURE"));
    assertTrue(processor.supports("Build_Failure"));
  }

  @Test
  void supports_shouldReturnFalseForOtherEventTypes() {
    assertFalse(processor.supports("pull_request"));
    assertFalse(processor.supports("push"));
    assertFalse(processor.supports("unknown_event"));
    assertFalse(processor.supports(""));
  }

  @Test
  void processEvent_shouldCallAIWithPromptContainingRepoNameAndLogs() {
    String logSnippet = "ERROR: NullPointerException at com.devassistant.service.SomeService:42";
    String payload =
        """
        {
          "repository": "%s",
          "author": "Agaba-derrick",
          "logs": "%s"
        }
        """
            .formatted(REPO_NAME, logSnippet);

    processor.processEvent(payload);

    // Capture the exact prompt that was sent to the AI and assert its content
    ArgumentCaptor<String> promptCaptor = forClass(String.class);
    verify(chatClientRequestMock, times(1)).user(promptCaptor.capture());

    String capturedPrompt = promptCaptor.getValue();
    assertThat(capturedPrompt).contains(REPO_NAME);
    assertThat(capturedPrompt).contains(logSnippet);
    assertThat(capturedPrompt).containsIgnoringCase("Likely Cause");
    assertThat(capturedPrompt).containsIgnoringCase("Confidence");
    assertThat(capturedPrompt).containsIgnoringCase("Possible Fixes");
  }

  @Test
  void processEvent_shouldLogAIResponseContainingRepoNameAndAnalysis() {
    String payload =
        """
        {
          "repository": "%s",
          "author": "Agaba-derrick",
          "logs": "ERROR: NullPointerException at com.devassistant.service.SomeService:42"
        }
        """
            .formatted(REPO_NAME);

    processor.processEvent(payload);

    // Capture all info() calls and assert on their exact content
    ArgumentCaptor<String> logCaptor = forClass(String.class);
    verify(logger, times(3)).info(logCaptor.capture());

    List<String> allLoggedMessages = logCaptor.getAllValues();

    // Assert the header log line contains the repository name
    assertThat(allLoggedMessages.get(0)).contains("BUILD FAILURE DETECTED").contains(REPO_NAME);

    // Assert the AI analysis log line contains the full AI response text
    assertThat(allLoggedMessages.get(1))
        .contains("AI Analysis")
        .contains("Likely Cause: NullPointerException in DAO layer")
        .contains("Confidence: 95%")
        .contains("Possible Fixes: Add null check before the DB call");

    // Assert the footer separator was logged exactly as defined
    assertThat(allLoggedMessages.get(2)).contains("==========");
  }

  @Test
  void processEvent_withMissingLogsField_shouldWarnAndNotCallAI() {
    // A payload that is missing the required 'logs' field
    String malformedPayload =
        """
        {
          "repository": "devops-notifier"
        }
        """;

    processor.processEvent(malformedPayload);

    // Assert that a warning was logged with the correct message
    ArgumentCaptor<String> warnCaptor = forClass(String.class);
    verify(logger, times(1)).warn(warnCaptor.capture());
    assertThat(warnCaptor.getValue()).containsIgnoringCase("missing");

    // Assert the AI was never called because the payload was invalid
    verify(chatClientRequestMock, never()).user(anyString());
  }

  @Test
  void processEvent_withInvalidJson_shouldLogErrorAndNotCrash() {
    String invalidJson = "this is not valid json {{{{";

    processor.processEvent(invalidJson);

    // Assert that an error was logged — the processor must not silently swallow the exception
    ArgumentCaptor<String> errorCaptor = forClass(String.class);
    verify(logger, times(1))
        .error(errorCaptor.capture(), org.mockito.ArgumentMatchers.any(Throwable.class));
    assertThat(errorCaptor.getValue()).containsIgnoringCase("failed");

    // Assert the AI was never called since parsing failed
    verify(chatClientRequestMock, never()).user(anyString());
  }
}
