AI Merch Maker Lite

_Your Personal, Automated Print-on-Demand Factory._

AI Merch Maker Lite bridges the gap between raw creativity and e-commerce. It is a desktop application designed to eliminate the manual labor of creating and listing print-on-demand products. By leveraging a suite of modern AI tools and a polyglot architecture, this project transforms a simple text-based theme into a fully-realized, market-ready product in a Shopify store, all with a single click.

* * *

## Core Philosophy

To empower creators by **automating the mundane**, freeing them to focus on high-level ideas, not repetitive execution. This tool acts as a tireless digital assistant, handling the entire product creation workflow from concept to publication.

* * *

## Key Features

* *   ✨ **AI-Powered Creativity**: Instantly generates compelling product titles, engaging descriptions, and SEO-friendly tags from a simple theme using Google's Gemini Pro.
*     
* *   🎨 **Unique Artwork on Demand**: Produces original, high-quality artwork using the Stable Diffusion model via the Hugging Face API, ensuring every product is visually distinct.
*     
* *   👕 **Automated Mockup Generation**: Dynamically composites the generated artwork onto a t-shirt template, providing an instant, realistic product mockup for your storefront.
*     
* *   🚀 **Direct-to-Store Publishing**: Seamlessly creates a new product in your Shopify store via API, complete with all generated text, artwork, mockup images, and metadata.
*     
* *   🖥️ **Unified Command Center**: A robust, multi-threaded Java Swing GUI acts as the central orchestrator, providing a clear user interface and a real-time log of the entire process.
*     
* *   🔗 **Interactive Feedback Loop**: The live log provides status updates for each stage and concludes with a clickable link that takes you directly to the newly created product in your Shopify admin panel.
*     

* * *

## The Digital Assembly Line: How It Works

The application functions as a sophisticated, multi-language assembly line, where each component performs a specialized task before passing the product to the next stage.

Plaintext

```
 [ User Input: Creative Theme ]
              |
              V
[ 1. Java GUI Command Center ]
     (Initiates Pipeline)
              |
              V
[ 2. Python Content & Art Forge ] --- (Gemini & Hugging Face APIs)
     (Generates Text & Artwork)
              |
              V
[ 3. Node.js Mockup Studio ]
     (Overlays Art on Template)
              |
              V
[ 4. PHP Publishing Gateway ]
     (Sends Final Product to Shopify)
              |
              V
[ 5. Shopify Store ] --- (Shopify Admin API)
     (New Product Appears as Draft)
```

* * *

## Deep Dive: The Shopify Integration

The final and most critical step is handled by the **PHP Publishing Gateway**. This lightweight local server (`index.php`) acts as the bridge between the application and your Shopify store.

1. 1.  **Receives Finalized Data**: The Java orchestrator sends a complete JSON package containing all text and image file paths to the PHP endpoint.
1.     
1. 2.  **Authenticates Securely**: It loads your private Shopify credentials from a local `config.php` file, ensuring they remain secure.
1.     
1. 3.  **Prepares Media for Upload**: It reads the artwork and mockup image files from your disk and encodes them into Base64 format. This is the standard method for embedding images directly into an API call without needing to host them publicly first.
1.     
1. 4.  **Constructs API Payload**: It meticulously builds a JSON object that adheres to the strict structure required by the Shopify Products API endpoint.
1.     
1. 5.  **Executes the API Call**: Using PHP's cURL library, it sends the payload to your store's unique `/admin/api/products.json` URL.
1.     
1. 6.  **Confirms Creation**: It waits for a `201 Created` HTTP status from Shopify, confirming the product was successfully created. It then relays this success, along with a direct admin URL, back to the Java GUI.
1.     

* * *

## Getting Started: A Step-by-Step Guide

### 1\. Prerequisites

Ensure the following software is installed on your system:

* *   Java Development Kit (JDK 11+)
*     
* *   Python (3.8+)
*     
* *   Node.js & npm (14+)
*     
* *   PHP (8.0+)
*     

### 2\. Configuration: The Keys to the Factory

You must provide API keys for the AI services and Shopify.

#### **A. AI Service Keys**

1. 1.  In the project's root directory, create a file named `.env`.
1.     
1. 2.  Add your keys from Google and Hugging Face:
1.     
1.     Ini, TOML
1.     
1.     ```
1.     GEMINI_API_KEY="your-google-gemini-api-key"
1.     HUGGINGFACE_API_KEY="your-hugging-face-api-key"
1.     ```
1.     

#### **B. Shopify Credentials**

1. 1.  **Generate Shopify API Access**:
1.     
1.     * *   In your Shopify admin, go to **Settings > Apps and sales channels > Develop apps**.
1.     *     
1.     * *   Create an app, go to the **Configuration** tab, and configure **Admin API integration**.
1.     *     
1.     * *   Grant it **`write_products`** and **`read_products`** permissions.
1.     *     
1.     * *   Install the app and copy the **Admin API access token** (it starts with `shpat_`).
1.     *     
1. 2.  **Create `config.php`**:
1.     
1.     * *   In the `product_publisher_php/` directory, create a file named `config.php`.
1.     *     
1.     * *   Add your store URL and the access token you just copied:
1.     *     
1.     *     PHP
1.     *     
1.     *     ```
1.     *     <?php
1.     *     define('SHOPIFY_STORE_URL', 'https://your-store-name.myshopify.com');
1.     *     define('SHOPIFY_API_ACCESS_TOKEN', 'shpat_xxxxxxxxxxxxxxxxxxxxxxxxxxxxx');
1.     *     ?>
1.     *     ```
1.     *     

### 3\. Install Dependencies

**Python:**

PowerShell

```
pip install google-generativeai python-dotenv requests
```

**Node.js:**

PowerShell

```
cd mockup_visualizer
npm install
cd ..
```

* * *

## Launch Sequence

The application requires two processes running simultaneously in two separate terminals from the project root.

#### **Terminal 1: Activate the Publishing Gateway**

This command starts the local PHP server that waits to receive the final product data.

PowerShell

```
php -S 127.0.0.1:8000 -t product_publisher_php
```

_Leave this terminal running._

#### **Terminal 2: Launch the Command Center**

This command compiles and runs the main Java application.

PowerShell

```
# Navigate to the Java source directory
cd automation_orchestrator_java

# Compile the Java code
javac -cp ".;../lib/json.jar" Orchestrator.java

# Run the application
java -cp ".;../lib/json.jar" Orchestrator
```

The GUI will now launch. Enter your creative theme and click "Generate & Publish" to start the magic.

* * *

## Technical Blueprint

* *   **GUI & Orchestration**: Java (Swing)
*     
* *   **Core Backend Logic**: Python 3, Node.js, PHP 8
*     
* *   **Libraries**:
*     
*     * *   Java: `org.json`
*     *     
*     * *   Python: `google-generativeai`, `python-dotenv`, `requests`
*     *     
*     * *   Node.js: `jimp`
*     *     
* *   **APIs & Services**:
*     
*     * *   Google Gemini API
*     *     
*     * *   Hugging Face Inference API (for Stable Diffusion)
*     *     
*     * *   Shopify Admin API
*     *     

* * *

## Project Structure

```
ai_merch_maker_lite/
├── automation_orchestrator_java/   # Main Java GUI and pipeline controller
│   └── Orchestrator.java
├── content_generator/              # Python script for AI content and art
│   └── main.py
├── generated_products/             # Output folder for all generated images
│   └── (artworks and mockups appear here)
├── lib/                            # Java dependencies
│   └── json.jar
├── mockup_visualizer/              # Node.js script for creating mockups
│   ├── main.js
│   └── tshirt_template.png
├── product_publisher_php/          # PHP script for publishing to Shopify
│   ├── index.php
│   └── config.php (must be created)
├── .env                            # Stores API keys for AI services
├── .gitignore
└── README.md
```
