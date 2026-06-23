package com.gamehubstore.order_mscv.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

// "inventory-mscv" debe coincidir exactamente con el nombre registrado en tu Eureka Server
@FeignClient(name = "inventory-mscv", path = "/api/v1/inventory")
public interface InventoryClient {

    /**
     * Endpoint para congelar/reservar stock antes de pagar.
     */
    @PutMapping("/reserve/{idProducto}")
    void reservarStock(@PathVariable("idProducto") Long idProducto, @RequestParam("cantidad") Long cantidad);

    /**
     * Endpoint inverso para devolver el stock si la orden se cancela.
     */
    @PutMapping("/release/{idProducto}")
    void liberarStock(@PathVariable("idProducto") Long idProducto, @RequestParam("cantidad") Long cantidad);
}