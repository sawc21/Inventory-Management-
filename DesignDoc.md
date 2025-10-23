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

&nbsp; class Item { String id; String name; int quantity; double price }

&nbsp; class InventoryRepository { +save(Item); +delete(String); +findAll() }

&nbsp; class InventoryService { +addItem(Item); +adjustStock(String, int) }

&nbsp; class RealTimeStockUpdater

&nbsp; InventoryService --> InventoryRepository

&nbsp; RealTimeStockUpdater --> InventoryService



