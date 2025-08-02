<?php
// Set headers and include config
header('Content-Type: application/json');
require_once 'config.php'; // Contains Shopify credentials

// --- Pre-flight checks ---
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405); // Method Not Allowed
    echo json_encode(['status' => 'error', 'message' => 'Invalid request method.']);
    exit;
}

if (!defined('SHOPIFY_STORE_URL') || !defined('SHOPIFY_API_ACCESS_TOKEN') || strpos(SHOPIFY_STORE_URL, 'your-dev-store') !== false) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Shopify configuration is missing or not updated in config.php.']);
    exit;
}

// --- Data Processing ---
$json_data = file_get_contents('php://input');
$log_file = 'published_products_php.log';
file_put_contents($log_file, "[" . date('Y-m-d H:i:s') . "] RECEIVED: " . $json_data . "\n---\n", FILE_APPEND);

$product_data = json_decode($json_data, true);
if (json_last_error() !== JSON_ERROR_NONE) {
    http_response_code(400); // Bad Request
    echo json_encode(['status' => 'error', 'message' => 'Invalid JSON received.']);
    exit;
}

// --- Extract data from the payload ---
$title = $product_data['content_data']['product_details']['title'] ?? 'Untitled Product';
$description = $product_data['content_data']['product_details']['description'] ?? 'No description.';
$tags = implode(', ', $product_data['content_data']['product_details']['tags'] ?? []);
$artwork_path = str_replace('\\\\', DIRECTORY_SEPARATOR, $product_data['content_data']['artwork_file'] ?? '');
$mockup_path = str_replace('\\\\', DIRECTORY_SEPARATOR, $product_data['mockup_data']['mockup_url'] ?? '');

// --- Image Processing: Read and Base64 Encode ---
try {
    if (!file_exists($artwork_path) || !file_exists($mockup_path)) {
        throw new Exception('Image file not found. Artwork: ' . $artwork_path);
    }
    $artwork_base64 = base64_encode(file_get_contents($artwork_path));
    $mockup_base64 = base64_encode(file_get_contents($mockup_path));
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Failed to read image files: ' . $e->getMessage()]);
    exit;
}


// --- Construct Shopify API Payload ---
$shopify_payload = [
    'product' => [
        'title' => $title,
        'body_html' => "<p><strong>{$description}</strong></p>",
        'vendor' => 'AI Merch Maker Lite',
        'product_type' => 'T-Shirt',
        'tags' => $tags,
        'status' => 'draft', // Create product as a draft first
        'images' => [
            // First image is the main one
            ['attachment' => $artwork_base64, 'filename' => basename($artwork_path)],
            // Second image is the mockup
            ['attachment' => $mockup_base64, 'filename' => basename($mockup_path)]
        ]
    ]
];

// --- Send Data to Shopify via cURL ---
$ch = curl_init();
$api_url = rtrim(SHOPIFY_STORE_URL, '/') . '/admin/api/2024-07/products.json';

curl_setopt_array($ch, [
    CURLOPT_URL => $api_url,
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_POST => true,
    CURLOPT_POSTFIELDS => json_encode($shopify_payload),
    CURLOPT_HTTPHEADER => [
        'Content-Type: application/json',
        'X-Shopify-Access-Token: ' . SHOPIFY_API_ACCESS_TOKEN
    ],
]);

$response_body = curl_exec($ch);
$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

// --- Handle Shopify Response ---
file_put_contents($log_file, "[" . date('Y-m-d H:i:s') . "] SHOPIFY RESPONSE ($http_code): " . $response_body . "\n---\n", FILE_APPEND);

if ($http_code == 201) { // 201 Created
    $shopify_response = json_decode($response_body, true);
    $final_response = [
        'status' => 'success',
        'message' => 'Product published successfully to Shopify!',
        'product_id' => 'shopify_prod_' . $shopify_response['product']['id'],
        'product_url' => rtrim(SHOPIFY_STORE_URL, '/') . '/admin/products/' . $shopify_response['product']['id']
    ];
    http_response_code(201);
    echo json_encode($final_response);
} else {
    http_response_code($http_code);
    echo json_encode([
        'status' => 'error',
        'message' => "Failed to publish to Shopify. Server responded with HTTP $http_code.",
        'details' => json_decode($response_body)
    ]);
}
?>