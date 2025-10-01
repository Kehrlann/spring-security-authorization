#!/usr/bin/env bash

store_name="authorization-demo"

store_id=$(curl -s "http://localhost:9000/stores" |
  jq -r ".stores[] | select(.name == \"${store_name}\") | .id")

if [ -z "$store_id" ]; then
  echo "Store not found. Creating store '${store_name}'..."
  store_id=$(curl -s -X POST "http://localhost:9000/stores" \
    -H "Content-Type: application/json" \
    -d "{\"name\": \"${store_name}\"}" |
    jq -r '.id')
  echo "Created store with ID: ${store_id}"
else
  echo "Using existing store with ID: ${store_id}"
fi

echo "Loading authorization model from model.json..."
curl -s -X POST "http://localhost:9000/stores/${store_id}/authorization-models" \
  -H "Content-Type: application/json" \
  -d @model.json
echo "Authorization model loaded."

echo "Loading tuples from tuples.json..."
jq -c '.[] | .key' tuples.json | while read -r tuple; do
  echo $tuple
  curl -s -X POST "http://localhost:9000/stores/${store_id}/write" \
    -H "Content-Type: application/json" \
    -d "{\"writes\":{\"tuple_keys\":[$tuple]}}"
done
echo "Tuples loaded."

