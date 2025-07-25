# AI Merch Maker Lite

A multi-language automation pipeline that generates and "publishes" AI-created product listings. This project takes a simple text theme, uses multiple AI services to generate a product title, description, tags, and artwork, creates a product mockup, and simulates publishing it to an e-commerce platform.

The final application is an interactive Java Swing GUI that features a styled live-log, a mockup image viewer, and a tab for viewing the generated product details.

![App Screenshot](https://i.ibb.co/L5ZtBqK/ai-merch-maker-lite-gui.png) 
*Above is a conceptual screenshot of the running GUI application.*

---

## Features

* **AI-Powered Content:** Generates a unique product title, description, and tags from a simple theme using the Google Gemini API.
* **AI-Powered Artwork:** Creates custom artwork for the T-shirt using the Hugging Face/Stable Diffusion API.
* **Automated Mockups:** Automatically resizes and centers the generated artwork onto a T-shirt template using Node.js and Jimp.
* **End-to-End Automation:** The entire process, from theme idea to a "published" product ID, is handled with a single click.
* **Interactive Java GUI:** A user-friendly interface built with Java Swing that lets you:
    * Enter any theme for a new product.
    * View real-time, styled progress in a live log.
    * See the final product mockup displayed directly in the app.
    * Review the generated title, description, and tags in a dedicated tab.

---

## Tech Stack

* **Orchestration & GUI:** **Java (Swing)**
* **Content & Image Generation:** **Python 3**
    * `google-generativeai` for text generation.
    * `requests`, `python-dotenv` for API communication.
* **Image Mockup Visualizer:** **JavaScript (Node.js)**
    * `Jimp` for image processing and composition.
* **Fake Publishing Endpoint:** **PHP**
    * Uses the built-in development server to simulate an API endpoint.
* **AI Services:**
    * **Text Generation:** Google Gemini API
    * **Image Generation:** Hugging Face Inference API (Stable Diffusion)

---

## Setup and Installation

Follow these steps to get the project running on your local machine.

### 1. Prerequisites

* **Python 3.8+**
* **Node.js 14+**
* **Java Development Kit (JDK) 11+**
* **PHP 7.4+**
* **API Keys:**
    * A **Google Gemini API Key** from [Google AI Studio](https://aistudio.google.com/).
    * A **Hugging Face API Key** from [huggingface.co](https://huggingface.co/settings/tokens).

### 2. Clone the Repository

```bash
git clone <your-repo-url>
cd ai_merch_maker_lite
```

### 3. Set Up API Keys

Create a file named `.env` in the root directory of the project and add your API keys:

```
GEMINI_API_KEY="AIzaSy..."
HUGGINGFACE_API_KEY="hf_..."
```

### 4. Install Dependencies

* **Python:**
    ```bash
    pip install Pillow google-generativeai python-dotenv requests
    ```
* **JavaScript:**
    ```bash
    cd mockup_visualizer
    npm install
    cd ..
    ```

---

## How to Run

This application requires two terminals running simultaneously: one for the Java server and one for the Python GUI.

---

### 4. Install Dependencies
* **Python:**
    ```bash
    pip install google-generativeai python-dotenv requests
    ```

* **JavaScript:**
    ```bash
    cd mockup_visualizer
    npm install
    cd ..
    ```

* **Java (JSON Library):**
    * Download the JSON library JAR file from here: [json-20231013.jar](https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar)
    * In the project root (`ai_merch_maker_lite/`), create a new folder named `lib`.
    * Place the downloaded `.jar` file in this folder and rename it to `json.jar`. The final path should be `ai_merch_maker_lite/lib/json.jar`.

---

## How to Run
This application requires two terminals running simultaneously: one for the PHP server and one for the Java GUI.

### Terminal 1: Start the PHP Server
Open a terminal in the project's root directory (`ai_merch_maker_lite/`) and run:

```bash
php -S 127.0.0.1:8000 -t product_publisher_php
```

Leave this terminal running. You should see the message like `Development Server (http://127.0.0.1:8000) started`

### Terminal 2: Run the Java GUI Application

Open a second terminal

```bash
# Navigate to the Java directory
cd automation_orchestrator_java

# Compile the Java code, including the JSON library in the classpath
# (For Windows)
javac -cp ".;../lib/json.jar" Orchestrator.java

# Run the application, also including the classpath
# (For Windows)
java -cp ".;../lib/json.jar" Orchestrator
```

The application window will appear. Enter a theme into the input box and click **"Generate & Publish"** to start the pipeline.

---

## Project Structure

```
ai_merch_maker_lite/
│
├── automation_orchestrator_java/   # Java Swing GUI and main pipeline logic
│   └── Orchestrator.java
│
├── content_generator/              # Python script for AI content and image generation
│   └── main.py
│
├── mockup_visualizer/              # Node.js script for creating T-shirt mockups
│   ├── main.js
│   └── tshirt_template.png
│
├── product_publisher_php/          # PHP script acting as a fake publishing API
│   └── index.php
│
├── generated_products/             # Output folder for all generated images and mockups
│
├── lib/                            # Folder for Java libraries
│   └── json.jar
│
├── .env                            # For storing API keys (local only)
└── README.md                       # This file
```