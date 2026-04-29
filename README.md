# TaskFlow — Personal Task Manager

A Spring Boot REST API for managing personal tasks, with an AI-powered endpoint that converts plain-language descriptions into structured tasks using OpenAI.

---

## Prerequisites

- **Java 17** — [Download here](https://adoptium.net/)
- **An OpenAI API key** — [Get one here](https://platform.openai.com/api-keys)

---

## Setup

**1. Clone the repository**
```bash
git clone <your-repo-url>
cd taskmanager 2
```

**2. Create a `.env` file in the project root**
Create a file named `.env` in the same folder as `pom.xml` and add the following:

OPENAI_API_KEY=sk-your-openai-key-here

---

## Run the Application

```bash
./mvnw spring-boot:run
```

Then open your browser at `http://localhost:8080`

---

## Run the Tests

```bash
./mvnw test
```

Expected output:
