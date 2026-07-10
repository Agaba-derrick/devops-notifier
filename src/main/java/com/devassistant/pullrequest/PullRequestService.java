package com.devassistant.pullrequest;

public interface PullRequestService {
  void commentOnPullRequest(String payload);

  void validatePullRequest(String payload);

  void mergePullRequest(String payload);

  String fetchPullRequestInfo(String pullRequestId);

  void handlePullRequest(String action, String prTitle, String prUrl, String author);
}
