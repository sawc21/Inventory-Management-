# Inventory Management System (Java, Maven Daemon)

***************************************
*******RUN WITH : mvnd exec:java*******
***************************************

## Overview

This project is a modular Java based Inventory Management System built for Fall 2025 coursework.  
It uses clean architectural layering, strict domain validation, file based persistence, and real time stock updates via multithreading.

The project uses:
- Java 17+
- Maven Daemon (mvnd)
- Immutable style domain models
- A `Result<T>` wrapper for clean success or failure handling
- A pluggable `LowStockPolicy`
- A `StockMovement` model that drives concurrent real time updates

This README reflects the current state of the project and the next planned steps.

---

## Current Status

###  Domain Models 

#### **Item**
- Immutable style model with:
  - `id`
  - `name`
  - `quantity`
  - `price`
  - `supplier`
- Validation rules:
  - `id`, `name`, `supplier` must be non null and non blank
  - `quantity` must be greater than or equal to 0  
  - `price` must be greater than or equal to 0
- Helper methods (or equivalent logic in service):
  - `withAdjustedQuantity(int delta)`
  - `withUpdatedDetails(String newName, Double newPrice, String newSupplierId)`
  - `withQuantity(int newQuantity)`

#### **Supplier**
- Immutable model  
- Fields:
  - `id`
  - `name`
- Validations:
  - `id` and `name` are required

#### **StockMovement**
- Represents a single stock change event for an item
- Fields:
  - `itemId`  
  - `quantityChange` (positive for inbound, negative for outbound)  
  - `MovementType movementType`  
  - `timestamp`  
  - `reference` (for example `"ORDER-123"`, `"SIM"`)  
  - `note` (optional description)
- Validation rules:
  - `itemId` and `movementType` must be non null and non blank  
  - `quantityChange` cannot be zero  
  - `timestamp` defaults to the current time when null
- Used by the concurrency layer to simulate real time updates

#### **MovementType** needs to be added
- Enum for the type of movement:
  - `INBOUND`  
  - `OUTBOUND`  
  - `ADJUSTMENT`

---

##  Service Layer I

### **InventoryService**

Defines the main operations for inventory:

- Item CRUD:
  - `addItem(Item item)`
  - `updateItem(Item item)`
  - `deleteById(String id)`
  - `getById(String id)`
  - `listAll()`
  - `searchByName(String query)`

- Stock updates:
  - `adjustStock(String id, int delta)`  
  - `applyMovement(StockMovement movement)`  

- Low stock:
  - `lowStock()`  
  - Uses a `LowStockPolicy` strategy interface so the low stock rule is configurable

- Support for concurrency:
  - `getAllItemIds()` so producers can pick random items without touching repositories directly

- Persistence:
  - `saveAll()`  
  - `loadAll()`

- `Result<T>` wrapper:
  - `isOk()`, `message()`, and `value()`  
  - Used across service methods to return clean success or failure information

### **InventoryServiceImpl**

Implements all business rules:

- Prevents duplicate IDs on add
- Validates items on create and update
- Prevents negative stock when adjusting
- Applies domain level stock movements via `applyMovement(StockMovement)` which internally uses `adjustStock`
- Sorting rules for low stock:
  - Sort by `quantity` ascending
  - Then by `name`
  - Then by `id`
- Delegates persistence to `InventoryFileStorage`
- Delegates storage to `InventoryRepository`
- Uses `LowStockPolicy` to decide if an item is considered low stock

---

## Persistence Layer

### **InventoryRepository**
- In memory abstraction for items
- Supports:
  - `findAll()`
  - `findById(String id)`
  - `existsById(String id)`
  - `save(Item item)`
  - `deleteById(String id)`
  - `replaceAll(List<Item> items)` for reload

### **InventoryFileStorage**
- Handles reading and writing the inventory to a file
- Uses `src/main/resources/inventory.txt` as the backing storage
- Knows how to serialize and deserialize `Item` data into a simple text format

---

## Concurrency Layer (Real Time Stock Updates)

The concurrency layer introduces real time simulation of stock movements.

### **RealTimeStockUpdater**
- Package: `com.ims.concurrency`
- Implements `Runnable`
- Consumes `StockMovement` objects from a shared `BlockingQueue<StockMovement>`
- For each movement:
  - Calls `inventoryService.applyMovement(movement)`
- Runs in its own thread to apply movements while the rest of the program continues to work

### **MovementProducer**
- Package: `com.ims.concurrency`
- Implements `Runnable`
- Uses `inventoryService.getAllItemIds()` to fetch valid item IDs
- Periodically creates random `StockMovement` objects, for example:
  - Random item
  - Random quantity between 1 and 5
  - Random `INBOUND` or `OUTBOUND`
- Puts each movement into the shared `BlockingQueue<StockMovement>`
- Runs in its own thread to simulate incoming stock changes

---

## Project Structure

```text
src/
  main/
    java/
      com/ims/
        model/
          Item.java
          Supplier.java
          StockMovement.java
          MovementType.java          (if split out)
        service/
          InventoryService.java
          InventoryServiceImpl.java
          GlobalThresholdPolicy.java (LowStockPolicy implementation)
        repository/
          InventoryRepository.java
        io/
          InventoryFileStorage.java
        concurrency/
          RealTimeStockUpdater.java  (real time consumer)
          MovementProducer.java      (real time producer)
        app/
          Main.java                  (app entry point, WIP)
  resources/
    inventory.txt

pom.xml
