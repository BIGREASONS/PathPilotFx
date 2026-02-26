<div align="center">

# 🎯 PathPilot FX
### Your AI-Powered Career Architect & Companion

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-24-1565C0.svg?style=for-the-badge&logo=java&logoColor=white)](https://openjfx.io/)
[![Ollama](https://img.shields.io/badge/AI-Ollama_Local-000000.svg?style=for-the-badge&logo=ollama&logoColor=white)](https://ollama.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-10B981.svg?style=for-the-badge)](#)

*PathPilot FX is a sleek, gamified JavaFX desktop application that architects your tech career. Powered by local LLMs, it generates hyper-personalized 12-week learning roadmaps tailored to your specific goals, integrating real-world projects and structured milestones.*

</div>

<br>

## 🌟 The Philosophy: Build *Good* Projects

Generic bootcamps and standard tutorials often leave you stuck in "tutorial hell," building trivial To-Do lists or simple calculators. **PathPilot FX** is designed to steer you away from "bad" starter projects and explicitly focuses on **Industry-Standard, Portfolio-Worthy Projects**. 

Whether you are targeting Software Engineering, DevOps, or Data Science, our AI generates roadmaps that challenge you to build **meaningful, production-ready applications**—such as scalable APIs, full-stack microservices, cloud-native deployments, and real-time data pipelines—that actual recruiters and hiring managers look for.

---

## ✨ Core Features

| Feature | Description |
| :--- | :--- |
| 🧠 **Smart AI Roadmaps** | Generates an intensive 12-week plan tailored to your target role, skill level, and daily schedule using Ollama (LLaMA2/3). |
| 🏗️ **Real-World Projects** | Emphasizes architecting complex, standout portfolio projects over generic exercises. |
| ⚡ **Algorithmic Challenges** | Integrated continuous learning with curated, progressive coding challenges. |
| 🎮 **Gamified Progression** | Stay motivated with our Tier System. Earn points, hit milestones, maintain your streak, and climb from **Bronze** to **Diamond**. |
| 📚 **Resource Library** | Instant, organized access to 18+ top-tier learning platforms (Coursera, Kaggle, freeCodeCamp, LeetCode, etc.). |
| 💾 **Persistent Profiles** | Securely saves and reloads your profile data, scores, and roadmap goals locally across sessions. |
| 🎨 **Premium UI/UX** | Built with JavaFX featuring modern gradient backgrounds, glow animations, smooth transitions, and a gorgeous dark theme. |

---

## 📋 Prerequisites

Ensure your development environment meets the following requirements:

- **Java JDK** 17 or higher
- **JavaFX SDK** 24+ ([Download Here](https://openjfx.io/))
- **Ollama** running locally on port `11434` with `llama2` or your preferred model pulled.

### Setting up the local AI Engine (Ollama)

```bash
# 1. Install Ollama from https://ollama.com/
# 2. Pull the default model:
ollama pull llama2

# 3. Ensure the service is running (runs on http://localhost:11434 by default)
ollama serve
```

---

## 🚀 Installation & Usage

### 1. Clone the repository

```bash
git clone https://github.com/BIGREASONS/PathPilotFx.git
cd PathPilotFx
```

### 2. Configure JavaFX

Edit `runfx.bat` and `build.bat` in the root directory and update the `JAVAFX_SDK` variable to point to your JavaFX SDK `lib` folder:

```bat
set JAVAFX_SDK=C:\path\to\your\javafx-sdk-24\lib
```

### 3. Compile and Run

For Windows users, we provide convenient batch scripts:

```bash
# To cleanly build the JAR file:
build.bat

# To compile and run the application immediately:
runfx.bat
```

*Alternatively, you can compile and run manually from the command line ensuring you include `lib/*` in your classpath.*

---

## 🏆 The Tier System

Your consistency and technical milestones are rewarded. Climb the ranks as you progress through your personalized AI roadmap:

| Rank | Badge | Points Required |
| :--- | :---: | :--- |
| **Bronze** | 🥉 | 0 pts |
| **Silver** | 🥈 | 50 pts |
| **Gold** | 🥇 | 150 pts |
| **Platinum** | 💎 | 300 pts |
| **Diamond** | 💠 | 500+ pts |

---

## 🗂️ Project Architecture

```text
PathPilotFX/
├── PathPilotFX.java        # Core JavaFX UI and Application Logic
├── lib/                    
│   └── json-20231013.jar   # Robust JSON parsing utility
├── runfx.bat               # Windows runtime execution script
├── build.bat               # Windows JAR build script
├── manifest.txt            # Application Manifest
├── .gitignore              # VCS exclusions
└── README.md               # Project documentation
```

---

<div align="center">
  <b>Architect your future. Skip the tutorial hell. Build what matters.</b><br>
  <i>Developed with ❤️ for aspiring engineers.</i>
</div>
