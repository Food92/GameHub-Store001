package com.gamehubstore.inventory_mscv.services;

import com.gamehubstore.inventory_mscv.client.ProductClient;
import com.gamehubstore.inventory_mscv.exceptions.InventoryException;
import com.gamehubstore.inventory_mscv.models.Inventory;
import com.gamehubstore.inventory_mscv.models.MovimientoInventario;
import com.gamehubstore.inventory_mscv.models.dtos.InventoryDTO;
import com.gamehubstore.inventory_mscv.models.dtos.ProductDTO;
import com.gamehubstore.inventory_mscv.repositories.InventoryRepository;
import com.gamehubstore.inventory_mscv.repositories.MovimientoInventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private MovimientoInventarioRepository movimientoInventarioRepository;

    @Autowired
    private ProductClient productClient;

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Inventory findById(Long id) {
        return inventoryRepository.findById(id).orElseThrow(
                () -> new InventoryException("Registro de inventario con ID: " + id + " no existe."));
    }

    @Override
    @Transactional
    public InventoryDTO save(InventoryDTO inventoryDTO) {
        ProductDTO productDTO = productClient.findById(inventoryDTO.getIdProducto());

        if (productDTO == null || "INACTIVO".equals(productDTO.getEstado().toString())) {
            throw new InventoryException("No existe el producto o se encuentra inactivo en el catálogo");
        }

        if (inventoryDTO.getStockDisponible() < 0) {
            throw new InventoryException("El stock inicial disponible no puede ser inferior a cero");
        }

        Inventory inventory = new Inventory();
        inventory.setIdProducto(inventoryDTO.getIdProducto());
        inventory.setStockDisponible(inventoryDTO.getStockDisponible());
        inventory.setStockReservado(inventoryDTO.getStockReservado() != null ? inventoryDTO.getStockReservado() : 0L);
        inventory.setStockMinimo(inventoryDTO.getStockMinimo() != null ? inventoryDTO.getStockMinimo() : 0L);
        inventory.setUbicacion(inventoryDTO.getUbicacion());

        Inventory saved = inventoryRepository.save(inventory);

        MovimientoInventario mov = new MovimientoInventario();
        mov.setIdProducto(saved.getIdProducto());
        mov.setTipo("ENTRADA");
        mov.setCantidad(saved.getStockDisponible());
        mov.setFecha(LocalDateTime.now());
        movimientoInventarioRepository.save(mov);

        InventoryDTO response = new InventoryDTO();
        response.setInventarioId(saved.getInventarioId());
        response.setIdProducto(saved.getIdProducto());
        response.setStockDisponible(saved.getStockDisponible());
        response.setStockReservado(saved.getStockReservado());
        response.setStockMinimo(saved.getStockMinimo());
        response.setUbicacion(saved.getUbicacion());
        return response;
    }

    @Override
    @Transactional
    public Inventory update(Long id, Inventory inventory) {
        Inventory existente = this.findById(id);

        if (inventory.getStockDisponible() < 0 || inventory.getStockReservado() < 0) {
            throw new InventoryException("El stock remanente no puede quedar en valores negativos");
        }

        existente.setUbicacion(inventory.getUbicacion());
        existente.setStockDisponible(inventory.getStockDisponible());
        existente.setStockReservado(inventory.getStockReservado());

        if (inventory.getStockMinimo() != null) {
            existente.setStockMinimo(inventory.getStockMinimo());
        }

        return inventoryRepository.save(existente);
    }

    @Override
    @Transactional
    public Inventory reservarStock(Long idProducto, Long cantidad) {
        Inventory inventory = inventoryRepository.findByIdProducto(idProducto)
                .stream().findFirst().orElseThrow(
                        () -> new InventoryException("No se encontró un registro de inventario configurado para el producto: " + idProducto));

        if (cantidad <= 0) {
            throw new InventoryException("La cantidad especificada a reservar debe ser un valor positivo mayor a cero");
        }

        if (inventory.getStockDisponible() < cantidad) {
            throw new InventoryException("Disponibilidad insuficiente en bodega para efectuar la reserva requerida");
        }

        inventory.setStockDisponible(inventory.getStockDisponible() - cantidad);
        inventory.setStockReservado(inventory.getStockReservado() + cantidad);

        MovimientoInventario mov = new MovimientoInventario();
        mov.setIdProducto(inventory.getIdProducto());
        mov.setTipo("RESERVA");
        mov.setCantidad(cantidad);
        mov.setFecha(LocalDateTime.now());
        movimientoInventarioRepository.save(mov);

        return inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public Inventory salidaStock(Long idProducto, Long cantidad) {
        Inventory inventory = inventoryRepository.findByIdProducto(idProducto)
                .stream().findFirst().orElseThrow(
                        () -> new InventoryException("No se encontró registro de inventario para el producto: " + idProducto));

        if (cantidad <= 0) {
            throw new InventoryException("La cantidad de salida definitiva debe ser superior a cero");
        }

        if (inventory.getStockReservado() < cantidad) {
            throw new InventoryException("Inconsistencia: No existen suficientes unidades reservadas asociadas a este pago");
        }

        inventory.setStockReservado(inventory.getStockReservado() - cantidad);

        MovimientoInventario mov = new MovimientoInventario();
        mov.setIdProducto(inventory.getIdProducto());
        mov.setTipo("SALIDA");
        mov.setCantidad(cantidad);
        mov.setFecha(LocalDateTime.now());
        movimientoInventarioRepository.save(mov);

        return inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new InventoryException("No es posible eliminar. El registro de inventario especificado no existe.");
        }
        inventoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findByIdProducto(Long idProducto) {
        return inventoryRepository.findByIdProducto(idProducto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findByUbicacion(String ubicacion) {
        return inventoryRepository.findByUbicacion(ubicacion);
    }
}