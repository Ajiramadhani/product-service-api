#!/bin/bash

# Create multiple products
for i in {1..300}; do
  curl -X POST http://localhost:9014/api/v1/products/createProduct \
    -H "Content-Type: application/json" \
    -d "{
      \"productName\": \"Product $i\",
      \"codeProduct\": \"PROD-$i\",
      \"price\": $((100000 * i)),
      \"categoryCode\": \"ELK-001\"
    }"
  echo ""  # newline for readability
done

# Create inventory for all products
for i in {1..300}; do
  curl -X POST http://localhost:9014/api/v1/inventory/create \
    -H "Content-Type: application/json" \
    -d "{
      \"productId\": $i,
      \"quantity\": $((50 * i)),
      \"operation\": \"SET\"
    }"
  echo ""  # newline for readability
done

# Get all inventory
curl -X GET http://localhost:9014/api/v1/inventory/all
echo ""  # newline for readability
