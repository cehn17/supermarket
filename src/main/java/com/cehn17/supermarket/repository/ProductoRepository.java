package com.cehn17.supermarket.repository;

import com.cehn17.supermarket.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
