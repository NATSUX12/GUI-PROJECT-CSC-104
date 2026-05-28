#!/bin/bash
# Cashier System - Unix/Mac launcher

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

JAR="$SCRIPT_DIR/lib/mysql-connector.jar"
SRC="$SCRIPT_DIR/src"
BIN="$SCRIPT_DIR/bin"

if [ ! -f "$JAR" ]; then
    echo "Error: mysql-connector.jar not found in lib/"
    echo "Download it and place it in the lib/ folder."
    exit 1
fi

mkdir -p "$BIN"

javac -cp "$JAR" -d "$BIN" "$SRC"/db/DBConnection.java "$SRC"/model/Product.java "$SRC"/ui/CashierUI.java

java -cp "$BIN:$JAR" ui.CashierUI