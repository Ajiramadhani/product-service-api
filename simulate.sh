#!/bin/bash
echo "=== Starting Inventory Test ==="

# Test product creation
echo "1. Creating product..."
PRODUCT_RESPONSE=$(curl -s -X POST http://localhost:9014/api/v1/products/createProduct \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Test Product",
    "codeProduct": "TESTS-001",
    "price": 100000,
    "categoryCode": "ELK-001"
  }')

echo "Product Response: $PRODUCT_RESPONSE"

# Extract product ID from response (jika response berhasil)
PRODUCT_ID=$(echo $PRODUCT_RESPONSE | grep -o '"idProduct":[0-9]*' | cut -d: -f2)

if [ -z "$PRODUCT_ID" ]; then
    echo "❌ Failed to create product or get product ID"
    echo "=== Test Failed ==="
    exit 1
fi

echo "Created product ID: $PRODUCT_ID"

# Tunggu sebentar untuk memastikan proses selesai
sleep 2

# Test inventory creation  
echo "2. Creating inventory for product ID: $PRODUCT_ID..."
INVENTORY_RESPONSE=$(curl -s -X POST http://localhost:9014/api/v1/inventory/create \
  -H "Content-Type: application/json" \
  -d '{
    "productId": '$PRODUCT_ID',
    "quantity": 100,
    "operation": "SET"
  }')

echo "Inventory Response: $INVENTORY_RESPONSE"

# Test get inventory
echo "3. Getting inventory for product ID: $PRODUCT_ID..."
GET_INVENTORY_RESPONSE=$(curl -s -X GET http://localhost:9014/api/v1/inventory/product/$PRODUCT_ID)
echo "Get Inventory Response: $GET_INVENTORY_RESPONSE"

echo "=== Test Completed ==="