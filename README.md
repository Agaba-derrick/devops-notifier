# 🚀 Java-based CI/CD Notification Bot

A **centralized, extensible notification microservice** that integrates with GitHub Actions, Jenkins, and other CI/CD systems to keep your team informed proactively and intelligently.

---

## ✨ **Features**

✅ **Centralized Notification Engine**  
Receive events from multiple CI/CD pipelines and process them through a single service.

✅ **Multi-Channel Support**  
Send notifications to Slack, Telegram, Email, Microsoft Teams, and more via pluggable `NotificationSender` classes.

✅ **Configurable Rules Engine**  
Define who gets notified about what events, with support for conditions, channels, and teams.

✅ **Scheduled Proactive Notifications**  
Run daily checks for stale pull requests, failing builds, or other conditions, and notify responsible developers automatically.

✅ **Notification History and Auditing**  
Log all notifications sent, including timestamps, status, and target channel, for audit and debugging.

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
