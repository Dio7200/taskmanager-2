# TaskFlow — Personal Task Manager API

A Spring Boot REST API for managing personal tasks, with an AI-powered endpoint that converts plain-language descriptions into structured task objects using OpenAI.

---

## Tech Stack

- Java 17
- Spring Boot 3.5.14
- Maven
- H2 In-Memory Database
- OpenAI API (gpt-4o-mini)

---

## Prerequisites

Before running this project, make sure you have the following installed:

- **Java 17** — [Download here](https://adoptium.net/)
- **Maven 3.9+** — [Download here](https://maven.apache.org/download.cgi)
- **An OpenAI API key** — [Get one here](https://platform.openai.com/api-keys)

Verify your Java installation:
```bash
java -version
```

---

## Setup

### 1. Clone the repository
```bash
git clone <your-repo-url>
cd taskmanager
```

### 2. Create a `.env` file in the project root
This file is required to provide your OpenAI API key. It is gitignored and will never be committed to the repository.

Create a file named `.env` in the same folder as `pom.xml` and add the following:
