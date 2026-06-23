package com.gamehubstore.inventory_mscv.repositories;

import com.gamehubstore.inventory_mscv.models.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByIdProducto(Long idProducto);
    List<Inventory> findByUbicacion(String ubicacion);
}
