package com.gamehubstore.category_mscv.services;

import com.gamehubstore.category_mscv.exceptions.CategoryException;
import com.gamehubstore.category_mscv.models.Category;
import com.gamehubstore.category_mscv.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category save(Category category) {
        // Validar el nombre
        if (categoryRepository.existsByNombreCategory(category.getNombreCategory())) {
            throw new CategoryException("Nombre de categoria ya existe");
        }
        category.setEstado(Category.EstadoCategory.ACTIVO);
        return categoryRepository.save(category);
    }


    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }


    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryException("Nombre de categoria no encontrado"));
    }


    @Override
    public Category update(Long id, String nombre, String descripcion) {
        Category oldCategory = findById(id);
        if (nombre != null && !nombre.isBlank()) oldCategory.setNombreCategory(nombre);
        if (descripcion != null) oldCategory.setDescripcionCategory(descripcion);
        return categoryRepository.save(oldCategory);
    }


    @Override
    public void desactivar(Long id) {
        Category categoria = findById(id);

        // Aquí deberías consultar product-service para verificar si hay productos activos
        boolean tieneProductosActivos = false; // TODO: llamada REST a product-service

        if (tieneProductosActivos) {
            throw new RuntimeException("No se puede desactivar categoría con productos activos");
        }

        categoria.setEstado(Category.EstadoCategory.INACTIVO);
        categoryRepository.save(categoria);
    }


}
