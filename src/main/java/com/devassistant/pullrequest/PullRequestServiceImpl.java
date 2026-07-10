package com.devassistant.pullrequest;

import org.springframework.stereotype.Service;

@Service
public class PullRequestServiceImpl implements PullRequestService {
  @Override
  public void commentOnPullRequest(String payload) {
    // Implementation for commenting on a pull request
  }

  @Override
  public void validatePullRequest(String payload) {
    // Implementation for validating a pull request
  }

  @Override
  public void mergePullRequest(String payload) {
    // Implementation for merging a pull request
  }

  @Override
  public String fetchPullRequestInfo(String pullRequestId) {
    return null;
  }

  @Override
  public void handlePullRequest(String action, String prTitle, String prUrl, String author) {
    // Implementation for handling a pull request from webhook
  }
}
