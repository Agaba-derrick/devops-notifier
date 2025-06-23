# GitHub-Actions-based-CI-CD-pipeline
# 🚀 CI Alert Bot Java API

This project is a **Java-based backend service** with a fully integrated **CI/CD pipeline using GitHub Actions**, designed to:

- ✅ Run automated builds and tests on each push or pull request
- ❌ Detect pull request, build/test failures in real-time
- 📣 Notify an external API (e.g., Slack, Discord, Telegram, custom webhooks) when a failure occurs

---

## 🔧 Tech Stack

| Layer        | Tech            |
|--------------|-----------------|
| Language     | Java 21         |
| Build Tool   | Maven           |
| Testing      | JUnit 5         |
| CI/CD        | GitHub Actions  |
| Notification | External  `API` |
---

## 📁 Project Structure

ci-alert-bot-java-api/
├── .github/
│   └── workflows/
│       └── ci.yml
├── src/
│   ├── main/java/com/derrick/app/ExampleService.java
│   └── test/java/com/derrick/app/ExampleServiceTest.java
├── pom.xml
└── README.md


---

## 🔄 CI/CD Workflow Overview

The pipeline is triggered on every push and pull request to the `main` branch. It performs the following steps:

1. ✅ **Checkout** the latest code
2. 🔧 **Set up Java 21** environment
3. 🧪 **Build and test** the application with Maven
4. 🚨 On failure, **trigger a POST request** to a configured API endpoint to notify about the build failure and the pull request Sent

### 📄 Example Failure Notification Payload

```json
{
  "repo": "derrick/ci-alert-bot-java-api",
  "branch": "refs/heads/main",
  "commit": "sha256...",
  "status": "FAILED",
  "author": "derrick",
  "url": "https://github.com/derrick/ci-alert-bot-java-api/actions/runs/1234567890"
}
