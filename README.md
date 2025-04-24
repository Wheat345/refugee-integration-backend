# 🛠️ Refugee Integration Backend

This is the backend system for the **Refugee Language Integration App**, a platform designed to help immigrants in Canada improve their English skills through realistic, AI-powered simulations and scenario-based lessons.

---

## 🚀 Overview

This backend powers the key features of the mobile app, including:
- 📚 Simulation-based English conversation training
- 🤖 AI feedback generation using OpenAI GPT-3.5 Turbo
- 📊 Evaluation of language skills (politeness, completeness, grammar, etc.)
- 🧠 Pinecone vector storage integration for factor-based responses
- 👤 User authentication and profile tracking
- 📝 Score tracking and progress history

---

## 🔧 Technologies Used

- **Spring Boot** – Backend framework
- **Java 17** – Core language
- **PostgreSQL** – Database
- **OpenAI API** – GPT-3.5 Turbo for language evaluation and assistant replies
- **Pinecone** – Vector similarity search for factor-based simulations
- **Gradle** – Project build tool

---

## 📁 Project Structure

---

## API Reference Guide

This document explains each API endpoint, including its URL, required parameters, and expected response.

⸻

1. Login API
	•	URL: /api/user/login
	•	Method: POST
	•	Input Parameters (JSON):

{
  "email": "user@example.com",
  "password": "yourpassword"
}

	•	Output:

{
  "userId": 1,
  "username": "User Name",
  "token": "jwt-token"
}



⸻

2. Signup API
	•	URL: /api/user/signup
	•	Method: POST
	•	Input Parameters (JSON):

{
  "email": "user@example.com",
  "password": "yourpassword",
  "username": "User Name"
}

	•	Output:

{
  "message": "Signup successful",
  "userId": 2
}



⸻

3. Start Conversation (Conversation-type Simulation)
	•	URL: /api/ai/conversation/start
	•	Method: GET
	•	Query Parameter:
	•	simulationId: ID of the simulation (e.g., appointment_clinic)
	•	Output:

{
  "conversationId": "uuid-value",
  "initialPrompt": "Hello, how can I help you?"
}



⸻

4. Reply to Conversation
	•	URL: /api/ai/conversation/reply
	•	Method: POST
	•	Input Parameters (JSON):

{
  "conversationId": "uuid-value",
  "userMessage": "I want to make an appointment"
}

	•	Output:

{
  "aiMessage": "What date would you prefer?",
  "step": 2
}



⸻

5. Evaluate Conversation (Get Feedback)
	•	URL: /api/ai/conversation/evaluate
	•	Method: GET
	•	Query Parameter:
	•	conversationId
	•	Output:

{
  "scoreFeedback": "Score: 75/100. Feedback: ...",
  "completed": true
}



⸻

6. Factor-type Simulation Evaluation API
	•	URL: /api/ai/agentFeedback
	•	Method: GET
	•	Query Parameters:
	•	question: The interview question (e.g., “What kind of job are you looking for?”)
	•	userAnswer: The user’s response
	•	Output:
	•	Feedback string comparing userAnswer to ideal answer from Pinecone

⸻

7. Update User Simulation Status
	•	URL: /api/user-simulation/update
	•	Method: POST
	•	Input Parameters (JSON):

{
  "userId": 15,
  "simulationId": 13,
  "newScore": 80,
  "completed": true
}

	•	Output:

"✅ UserSimulation updated successfully"




