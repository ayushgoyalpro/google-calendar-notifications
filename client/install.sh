#!/bin/bash

set -e

INSTALL_DIR="$HOME/.gcn-client"
BIN_DIR="/usr/local/bin"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "=== GCN Client Installer ==="
echo ""

# --- Check for Java 21+ ---
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed."
    echo "Please install Java 21 or later and re-run this installer."
    echo "Recommended: brew install --cask temurin@21"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ] 2>/dev/null; then
    echo "Error: Java 21 or later is required (found version $JAVA_VERSION)."
    echo "Recommended: brew install --cask temurin@21"
    exit 1
fi

echo "Java $JAVA_VERSION detected."
echo ""

# --- Create install directory ---
mkdir -p "$INSTALL_DIR"

# --- Copy JAR ---
JAR_FILE=$(ls "$SCRIPT_DIR"/gcn-client*.jar 2>/dev/null | head -n 1)
if [ -z "$JAR_FILE" ]; then
    echo "Error: No gcn-client JAR file found in the same directory as install.sh."
    exit 1
fi
cp "$JAR_FILE" "$INSTALL_DIR/gcn-client.jar"
echo "Installed JAR to $INSTALL_DIR/gcn-client.jar"

# --- Prompt for configuration ---
echo ""
echo "=== Configuration ==="
echo ""
echo "You need a personal User Key to connect to the GCN server."
echo "Contact the administrator if you don't have one."
echo ""
read -rp "Enter your User Key: " USER_KEY_INPUT

if [ -z "$USER_KEY_INPUT" ]; then
    echo "Error: User Key cannot be empty."
    exit 1
fi

# --- Write .env ---
cat > "$INSTALL_DIR/.env" <<EOF
USER_KEY=$USER_KEY_INPUT
EOF
echo "Configuration saved to $INSTALL_DIR/.env"

# --- Install gcn script ---
echo ""
if [ -w "$BIN_DIR" ]; then
    cp "$SCRIPT_DIR/gcn" "$BIN_DIR/gcn"
    chmod +x "$BIN_DIR/gcn"
    echo "Installed gcn command to $BIN_DIR/gcn"
else
    echo "Installing to $BIN_DIR requires admin privileges."
    sudo cp "$SCRIPT_DIR/gcn" "$BIN_DIR/gcn"
    sudo chmod +x "$BIN_DIR/gcn"
    echo "Installed gcn command to $BIN_DIR/gcn"
fi

echo ""
echo "=== Done ==="
echo ""
echo "Run 'gcn start' to start the client."
echo "Run 'gcn stop' to stop it."
echo "Run 'gcn status' to check if it is running."
echo ""
echo "Logs are written to /tmp/gcn-client.log"