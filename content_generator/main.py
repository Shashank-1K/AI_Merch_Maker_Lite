# content_generator/main.py
import os
import sys
import json
import datetime
import requests
from dotenv import load_dotenv
from pathlib import Path

# AI Library Imports
import google.generativeai as genai

# --- API Key and Configuration Loading ---
env_path = Path(__file__).parent.parent / '.env'
load_dotenv(dotenv_path=env_path)

GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")
if GEMINI_API_KEY:
    genai.configure(api_key=GEMINI_API_KEY)

# Load the specific key for Hugging Face
HUGGINGFACE_API_KEY = os.getenv("HUGGINGFACE_API_KEY")
MODEL_URL = "https://api-inference.huggingface.co/models/stabilityai/stable-diffusion-xl-base-1.0"

# --- Main Functions ---

def generate_product_content(theme):
    """Generates product title, description, and tags using Google Gemini."""
    print("Generating product content...", file=sys.stderr)
    if not GEMINI_API_KEY:
        raise Exception("Google Gemini API key is not set in .env file.")
    
    model = genai.GenerativeModel('gemini-1.5-flash')
    prompt_text = f"""
    Generate content for a t-shirt based on the theme: '{theme}'.
    Provide your response as a valid JSON object with three keys: "title", "description", and "tags".
    - The title should be short and catchy.
    - The description should be about 40-50 words.
    - The tags should be a list of 5-7 relevant keywords.
    """
    try:
        response = model.generate_content(prompt_text)
        json_text = response.text.strip().replace('```json', '').replace('```', '')
        print("Content generated successfully!", file=sys.stderr)
        return json.loads(json_text)
    except Exception as e:
        raise Exception(f"Error generating content with Gemini: {e}")

def generate_product_image(prompt, product_title):
    """Generates a product image using the Hugging Face Inference API."""
    print("Generating product image with Hugging Face...", file=sys.stderr)
    if not HUGGINGFACE_API_KEY:
        raise Exception("Hugging Face API key is not set in .env file.")

    headers = {"Authorization": f"Bearer {HUGGINGFACE_API_KEY}"}
    payload = { "inputs": f"A clean, high-resolution graphic for a t-shirt, featuring: {prompt}. Centered, vector art style, on a white background." }

    response = requests.post(MODEL_URL, headers=headers, json=payload, timeout=300)

    if response.status_code != 200:
        raise Exception(f"Hugging Face API Error: {response.status_code} - {response.text}")

    image_bytes = response.content
    timestamp = datetime.datetime.now().strftime("%Y%m%d_%H%M%S")
    safe_filename = "".join(c for c in product_title if c.isalnum() or c in (' ', '_')).rstrip().replace(' ', '_')
    
    # --- PATH FIX ---
    # Get the project's root directory (two levels up from this script)
    project_root = Path(__file__).parent.parent
    # Create an absolute path for the output file
    image_path = project_root / f"generated_products/{safe_filename}_{timestamp}_artwork.png"
    
    
    # Create the directory if it doesn't exist
    image_path.parent.mkdir(parents=True, exist_ok=True)
    
    with open(image_path, 'wb') as f:
        f.write(image_bytes)
    
    print(f"Image saved to: {image_path}", file=sys.stderr)
    # Return the absolute path as a string
    return str(image_path).replace('\\', '\\\\')



# --- Main execution block (CRITICAL) ---
if __name__ == '__main__':
    try:
        theme = sys.argv[1] if len(sys.argv) > 1 else "A default theme"
        
        product_content = generate_product_content(theme)
        if not product_content:
            print(json.dumps({"error": "Failed to generate content"}))
            sys.exit(1)
            
        title = product_content.get("title", "default_title")
        image_prompt = product_content.get("description", theme)
        artwork_path = generate_product_image(image_prompt, title)
        if not artwork_path:
            print(json.dumps({"error": "Failed to generate image"}))
            sys.exit(1)
            
        final_output = {
            "product_details": product_content,
            "artwork_file": artwork_path
        }
        print(json.dumps(final_output))

    except Exception as e:
        print(json.dumps({"error": str(e)}), file=sys.stdout)
        sys.exit(1)