package com.gamehubstore.order_mscv.services;

import com.gamehubstore.order_mscv.client.InventoryClient;
import com.gamehubstore.order_mscv.client.UserClient;
import com.gamehubstore.order_mscv.models.DetailOrder;
import com.gamehubstore.order_mscv.models.Order;
import com.gamehubstore.order_mscv.models.dtos.OrderDTO;
import com.gamehubstore.order_mscv.models.dtos.UserClientDTO; // Asegúrate de importar tu DTO espejo
import com.gamehubstore.order_mscv.repositories.OrderRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final UserClient userClient;

    // Inyección limpia por constructor
    public OrderServiceImpl(OrderRepository orderRepository,
                            InventoryClient inventoryClient,
                            UserClient userClient) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
        this.userClient = userClient;
    }

    @Override
    @Transactional
    public OrderDTO save(OrderDTO orderDTO) {

        // 1. VALIDACIÓN CRUCIAL: Verificar que el usuario realmente existe usando el UserClient
        try {
            UserClientDTO usuarioRemoto = userClient.getUserById(orderDTO.getIdUsuario());
            if (usuarioRemoto == null) {
                throw new RuntimeException("El usuario con ID " + orderDTO.getIdUsuario() + " no existe.");
            }
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("El usuario comprador no existe en el sistema.");
        }

        // Instanciamos la entidad a partir del DTO recibido
        Order orden = new Order();
        orden.setUserId(orderDTO.getIdUsuario());
        orden.setSubtotal(orderDTO.getTotal());
        orden.setDescuento(0.0);
        orden.setTotal(orderDTO.getTotal());
        orden.setEstado("CREADA"); // Regla: Estado inicial

        // 2. Validación obligatoria de stock con inventario
        if (orden.getDetails() != null && !orden.getDetails().isEmpty()) {
            for (DetailOrder detalle : orden.getDetails()) {
                inventoryClient.reservarStock(detalle.getIdProducto(), detalle.getCantidad().longValue());
            }
        }

        // JPA ejecuta automáticamente @PrePersist
        Order ordenGuardada = orderRepository.save(orden);

        // ❌ SE REMOVIÓ: userClient.iniciarPago(paymentPayload);
        // Lógica: La orden se guarda en estado CREADA. El cliente de Frontend o Postman
        // debe tomar el 'orderId' resultante y enviarlo al endpoint POST de payment-mscv para pagar.

        // Mapeamos el resultado de vuelta al DTO de salida
        orderDTO.setOrderId(ordenGuardada.getOrderId());
        orderDTO.setEstado(ordenGuardada.getEstado());
        orderDTO.setFechaCreacion(ordenGuardada.getFecha());

        return orderDTO;
    }

    @Override
    @Transactional
    public Order update(OrderDTO dto, Long orderId) {
        Order ordenExistente = this.findById(orderId);

        // REGLA MÍNIMA: No modificar una orden que ya fue pagada o despachada
        if ("PAGADA".equalsIgnoreCase(ordenExistente.getEstado()) || "EN_DESPACHO".equalsIgnoreCase(ordenExistente.getEstado())) {
            throw new IllegalStateException("Regla de Negocio: No se puede modificar una orden que se encuentra en estado " + ordenExistente.getEstado());
        }

        ordenExistente.setTotal(dto.getTotal());
        ordenExistente.setSubtotal(dto.getTotal());

        return orderRepository.save(ordenExistente);
    }

    @Override
    @Transactional(readOnly = true)
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("La orden con ID: " + orderId + " no existe."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByEstado(String estado) {
        return orderRepository.findByEstado(estado.toUpperCase());
    }

    @Override
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order ordenExistente = this.findById(orderId);

        if ("CANCELADA".equalsIgnoreCase(ordenExistente.getEstado())) {
            return ordenExistente;
        }
        if ("EN_DESPACHO".equalsIgnoreCase(ordenExistente.getEstado())) {
            throw new IllegalStateException("No se puede cancelar una orden que ya está en proceso de despacho físico.");
        }

        // REGLA MÍNIMA CRÍTICA: Liberar el stock reservado en la bodega (inventory-service)
        if (ordenExistente.getDetails() != null && !ordenExistente.getDetails().isEmpty()) {
            for (DetailOrder detalle : ordenExistente.getDetails()) {
                // inventoryClient.liberarStock(detalle.getIdProducto(), detalle.getCantidad().longValue());
            }
        }

        ordenExistente.setEstado("CANCELADA");
        return orderRepository.save(ordenExistente);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO findByIdDTO(Long orderId) {
        Order orden = this.findById(orderId);

        OrderDTO dto = new OrderDTO();
        dto.setOrderId(orden.getOrderId());
        dto.setIdUsuario(orden.getUserId());
        dto.setTotal(orden.getTotal());
        dto.setEstado(orden.getEstado());
        dto.setFechaCreacion(orden.getFecha());

        return dto;
    }

    @Override
    @Transactional
    public OrderDTO updateEstado(Long orderId, String estado) {
        Order ordenExistente = this.findById(orderId);

        ordenExistente.setEstado(estado.toUpperCase());
        Order ordenActualizada = orderRepository.save(ordenExistente);

        OrderDTO dto = new OrderDTO();
        dto.setOrderId(ordenActualizada.getOrderId());
        dto.setIdUsuario(ordenActualizada.getUserId());
        dto.setTotal(ordenActualizada.getTotal());
        dto.setEstado(ordenActualizada.getEstado());
        dto.setFechaCreacion(ordenActualizada.getFecha());

        return dto;
    }
}