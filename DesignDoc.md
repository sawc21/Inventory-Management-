\# Design Document



\## Overview

The system follows a layered architecture for clarity and scalability.



\### Layers

1\. \*\*Model:\*\* Core entities (Item, Supplier, StockMovement)

2\. \*\*Repository:\*\* Data access interfaces and implementations

3\. \*\*Service:\*\* Business logic

4\. \*\*Concurrency:\*\* Thread management

5\. \*\*I/O:\*\* File handling

6\. \*\*CLI:\*\* User interface

7\. \*\*Utility:\*\* Common helpers



\### UML (Mermaid)

```mermaid

classDiagram
direction LR

class Item {
  +id: String
  +name: String
  +quantity: int
  +price: double
  +supplierId: String
}

class Supplier {
  +id: String
  +name: String
  +contact: String
}

class StockMovement {
  +itemId: String
  +delta: int
  +timestamp: Instant
  +type: MovementType
}

class LowStockPolicy {
  <<interface>>
  +isLow(Item): boolean
}

class InventoryRepository {
  <<interface>>
  +add(Item)
  +update(Item)
  +delete(id: String)
  +findById(id: String): Item
  +findAll(): List~Item~
}

class InMemoryInventoryRepository
class FileInventoryRepository
class InventoryService {
  +addItem(Item)
  +adjustStock(id: String, delta: int)
  +findLowStock(): List~Item~
}

Item --> Supplier : supplierId
InventoryRepository <|.. InMemoryInventoryRepository
InventoryRepository <|.. FileInventoryRepository
InventoryService --> InventoryRepository : uses
LowStockPolicy <|.. DefaultLowStockPolicy


