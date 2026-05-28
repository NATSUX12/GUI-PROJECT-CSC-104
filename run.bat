@echo off
REM Cashier System - Windows launcher

set "JAR=lib\mysql-connector-j-9.6.0.jar"
set "SRC=src"
set "BIN=bin"

if not exist "%JAR%" (
    echo Error: mysql-connector.jar not found in lib/
    echo Download it and place it in the lib/ folder.
    pause
    exit /b 1
)

if not exist "%BIN%" mkdir "%BIN%"

javac -cp "%JAR%" -d "%BIN%" "%SRC%\db\DBConnection.java" "%SRC%\model\Product.java" "%SRC%\ui\CashierUI.java"

java -cp "%BIN%;%JAR%" ui.CashierUI
pause
