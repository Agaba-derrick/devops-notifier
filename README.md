# 🚀 AI Developer Assistant Platform

[![Java CI with Maven](https://github.com/Agaba-derrick/devops-notifier/actions/workflows/ci.yml/badge.svg)](https://github.com/Agaba-derrick/devops-notifier/actions/workflows/ci.yml)
An **intelligent, extensible platform** that integrates with GitHub Actions, Jenkins, and other CI/CD systems to assist developers proactively. Powered by Spring AI, it analyzes Pull Requests, explains build failures, and automates workflows.

---

## ✨ **Features**

✅ **AI PR Summarization**  
Automatically analyzes Pull Requests using LLMs to provide structured summaries, risk assessments, and key file changes.

✅ **Centralized Notification Engine**  
Receive events from multiple CI/CD pipelines and process them through a single service.

✅ **Multi-Channel Support**  
Send AI-enhanced notifications to Slack, Telegram, Email, Microsoft Teams, and more via pluggable `NotificationSender` classes.

✅ **Extensible Event Processing Architecture**  
Add new event processors for Jira, SonarQube, deployment tools, and any custom event source seamlessly.

---

## 🏗️ **Architecture Overview**

- **EventProcessor Interface**  
  Converts incoming event payloads to formatted human-readable messages.

- **NotificationSender Interface**  
  Sends messages to a specific channel (Slack, Telegram, etc).

- **NotificationService**  
  Orchestrates processing and sending notifications.

- **Webhook Controller**  
  Receives webhooks from GitHub or other systems via REST API.

- **Rules Engine**  
  Defines notification logic per event type, channel, and target users.

- **Scheduler**  
  Runs automated checks for proactive notifications (e.g. stale PR reminders).

- **Notification History Logger**  
  Stores notification metadata in a database for audit.

---

## ⚡ **Getting Started**

### 1. **Clone the repository**
```bashgit clone https://github.com/Agaba-derrick/GitHub-Actions-based-CI-CD-pipeline.git
cd GitHub-Actions-based-CI-CD-pipeline
