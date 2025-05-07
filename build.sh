#!/bin/bash

# Exit on error
set -e

echo "Building frontend..."
cd frontend
npm install
npm run build

echo "Building backend..."
cd ../backend
mvn clean install

echo "Build completed successfully!" 