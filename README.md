# 🎯 PathPilot FX — AI-Powered Career Companion

PathPilot FX is a **JavaFX desktop application** that helps you plan and track your tech career journey. It generates personalized, AI-powered 12-week learning roadmaps using a local [Ollama](https://ollama.com/) LLM, and gamifies your progress with points, streaks, and tiers.

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk)
![JavaFX](https://img.shields.io/badge/JavaFX-24-blue?logo=java)
![Ollama](https://img.shields.io/badge/AI-Ollama%20%28local%29-green)

---

## ✨ Features

- **🚀 AI Roadmap Generator** — Generates a detailed 12-week career roadmap tailored to your goal, skill level, and time commitment via a local Ollama LLM.
- **⚡ Interactive Challenges** — Curated coding challenges (Easy → Hard) linked to LeetCode, with point rewards.
- **📈 Progress Tracking** — Track your score, daily streak, and tier (Bronze → Diamond).
- **📚 Resource Library** — Quick links to 18+ learning platforms (LeetCode, Coursera, freeCodeCamp, Kaggle, etc.).
- **💾 Persistent Profiles** — Save and reload your profile data across sessions.
- **🎨 Modern UI** — Gradient backgrounds, glow animations, smooth transitions, and a dark theme.

## 📋 Prerequisites

| Requirement | Details |
|---|---|
| **Java JDK** | 17 or higher |
| **JavaFX SDK** | 24+ ([download](https://openjfx.io/)) |
| **Ollama** | Running locally on port 11434 with `llama2` model pulled |

### Setting up Ollama (for AI roadmap generation)

```bash
# Install Ollama from https://ollama.com/
# Then pull the model:
ollama pull llama2
# Start Ollama (it runs on http://localhost:11434 by default)
ollama serve
```

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/PathPilotFX.git
cd PathPilotFX
```

### 2. Set your JavaFX SDK path

Edit `runfx.bat` and update the `JAVAFX_SDK` variable to point to your JavaFX SDK `lib` folder:

```bat
set JAVAFX_SDK=C:\path\to\javafx-sdk-24\lib
```

### 3. Compile and run

```bash
# Using the batch script (Windows):
runfx.bat

# Or manually:
javac --module-path <JAVAFX_LIB_PATH> --add-modules javafx.controls -cp "lib/*" PathPilotFX.java
java --module-path <JAVAFX_LIB_PATH> --add-modules javafx.controls -cp ".;lib/*" PathPilotFX
```

## 🗂️ Project Structure

```
PathPilotFX/
├── PathPilotFX.java        # Main application source
├── lib/
│   └── json-20231013.jar   # JSON parsing library
├── runfx.bat               # Compile & run script (Windows)
├── build.bat               # Build JAR script
├── manifest.txt            # JAR manifest
├── .gitignore
└── README.md
```

## 🎮 How It Works

1. **Setup** — Enter your name, career goal (e.g., "ML Engineer"), skill level, and daily time commitment.
2. **Generate Roadmap** — Click "Generate Detailed Roadmap" to get a 12-week plan from the AI.
3. **Challenges** — Solve coding challenges to earn points and climb tiers.
4. **Track Progress** — Monitor your score, streak, and tier on the Progress tab.

## 🏆 Tier System

| Tier | Points Required |
|---|---|
| 🥉 Bronze | 0 |
| 🥈 Silver | 50 |
| 🥇 Gold | 150 |
| 💎 Platinum | 300 |
| 💠 Diamond | 500 |

## 📄 License

This project is open-source under the [MIT License](LICENSE).
