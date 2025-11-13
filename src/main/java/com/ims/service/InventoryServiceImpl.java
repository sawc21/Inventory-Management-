package com.ims.service;

import com.ims.model.Item;
import com.ims.model.StockMovement;
import com.ims.repository.InventoryRepository;
import com.ims.io.InventoryFileStorage;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository repo;
    private final InventoryFileStorage storage;
    private final LowStockPolicy lowStockPolicy;

    public InventoryServiceImpl(InventoryRepository repo,
            InventoryFileStorage storage,
            LowStockPolicy lowStockPolicy) {
        this.repo = Objects.requireNonNull(repo, "repo");
        this.storage = Objects.requireNonNull(storage, "storage");
        this.lowStockPolicy = Objects.requireNonNull(lowStockPolicy, "lowStockPolicy");
    }

    // ---------- Public API ----------

    @Override
    public Result<Item> addItem(Item item) {
        Result<Void> vr = validateItemForCreate(item);
        if (!vr.isOk())
            return Result.fail(vr.message());

        if (repo.existsById(item.getId())) {
            return Result.fail("Item id already exists: " + item.getId());
        }
        repo.save(item);
        return Result.ok(item);
    }

    @Override
    public Result<Item> updateItem(Item item) {
        Result<Void> vr = validateItemForUpdate(item);
        if (!vr.isOk())
            return Result.fail(vr.message());

        if (!repo.existsById(item.getId())) {
            return Result.fail("Item not found: " + item.getId());
        }
        repo.save(item);
        return Result.ok(item);
    }

    @Override
    public Result<Void> deleteById(String id) {
        if (isBlank(id))
            return Result.fail("id is required");
        if (!repo.existsById(id))
            return Result.fail("Item not found: " + id);
        repo.deleteById(id);
        return Result.ok();
    }

    @Override
    public Optional<Item> getById(String id) {
        if (isBlank(id))
            return Optional.empty();
        return repo.findById(id);
    }

    @Override
    public List<Item> listAll() {
        // Return an immutable snapshot to avoid accidental external mutation
        return Collections.unmodifiableList(new ArrayList<>(repo.findAll()));
    }

    @Override
    public List<String> getAllItemIds() {
        List<String> ids = repo.findAll().stream()
                .map(Item::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return Collections.unmodifiableList(ids);
    }

    @Override
    public List<Item> searchByName(String query) {
        if (isBlank(query))
            return List.of();
        String q = query.toLowerCase(Locale.ROOT);
        return repo.findAll().stream()
                .filter(it -> safeString(it.getName()).toLowerCase(Locale.ROOT).contains(q))
                .sorted(Comparator
                        .comparing(Item::getName, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(Item::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Result<Item> adjustStock(String id, int delta) {
        if (isBlank(id))
            return Result.fail("id is required");
        Optional<Item> opt = repo.findById(id);
        if (opt.isEmpty())
            return Result.fail("Item not found: " + id);

        Item current = opt.get();
        long newQty = (long) current.getQuantity() + delta;
        if (newQty < 0)
            return Result.fail("Adjustment would produce negative quantity");

        Item updated = cloneWithQuantity(current, (int) newQty);
        repo.save(updated);
        return Result.ok(updated);
    }

    @Override
    public Result<Item> applyMovement(StockMovement movement) {
        if (movement == null) {
            return Result.fail("movement is required");
        }
        // Reuse the same validation and logic as adjustStock
        return adjustStock(movement.itemId(), movement.quantityChange());
    }

    @Override
    public List<Item> lowStock() {
        return repo.findAll().stream()
                .filter(lowStockPolicy::isLow)
                .sorted(Comparator
                        .comparingInt(Item::getQuantity)
                        .thenComparing(Item::getName, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(Item::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Result<Void> saveAll() throws IOException {
        storage.saveAll(repo.findAll());
        return Result.ok();
    }

    @Override
    public Result<Void> loadAll() throws IOException {
        List<Item> items = storage.loadAll();
        // Replace current contents with loaded snapshot
        repo.replaceAll(items);
        return Result.ok();
    }

    // ---------- Validation ----------

    private Result<Void> validateItemForCreate(Item item) {
        Result<Void> base = validateCommon(item);
        if (!base.isOk())
            return base;
        if (isBlank(item.getId()))
            return Result.fail("id is required");
        return Result.ok();
    }

    private Result<Void> validateItemForUpdate(Item item) {
        Result<Void> base = validateCommon(item);
        if (!base.isOk())
            return base;
        if (isBlank(item.getId()))
            return Result.fail("id is required");
        return Result.ok();
    }

    private Result<Void> validateCommon(Item item) {
        if (item == null)
            return Result.fail("item is required");
        if (isBlank(item.getName()))
            return Result.fail("name is required");
        if (item.getQuantity() < 0)
            return Result.fail("quantity must be >= 0");
        if (isNegative(item.getPrice()))
            return Result.fail("price must be >= 0");
        return Result.ok();
    }

    // ---------- Helpers ----------

    private static boolean isNegative(double v) {
        return v < 0;
    }

    private static String safeString(String s) {
        return s == null ? "" : s;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static Item cloneWithQuantity(Item src, int qty) {
        // Adjust to your Item API. This assumes a withQuantity or builder is not
        // available.
        return new Item(
                src.getId(),
                src.getName(),
                qty,
                src.getPrice(),
                src.getSupplier());
    }
}
