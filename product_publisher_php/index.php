<?php
// Set the content type to JSON
header('Content-Type: application/json');

// Only accept POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405); // Method Not Allowed
    echo json_encode(['status' => 'error', 'message' => 'Invalid request method.']);
    exit;
}

// Get the raw JSON data from the request body
$json_data = file_get_contents('php://input');
$product_data = json_decode($json_data, true);

// Log the received data to a file
$log_file = 'published_products_php.log';
$log_entry = "[" . date('Y-m-d H:i:s') . "] " . json_encode($product_data, JSON_PRETTY_PRINT) . "\n---\n";
file_put_contents($log_file, $log_entry, FILE_APPEND);

// Respond with a success message and a PHP-specific fake product ID
$fake_product_id = 'prod_php_' . uniqid();
$response = [
    'status' => 'success',
    'message' => 'Product published successfully via PHP!',
    'product_id' => $fake_product_id,
    'product_url' => 'http://fakestore.com/products/' . $fake_product_id
];

http_response_code(201); // 201 Created
echo json_encode($response);
?>