package com.cehn17.supermarket.service;

import com.cehn17.supermarket.dto.DetalleVentaDTO;
import com.cehn17.supermarket.dto.VentaDTO;
import com.cehn17.supermarket.exception.NotFoundException;
import com.cehn17.supermarket.mapper.Mapper;
import com.cehn17.supermarket.model.DetalleVenta;
import com.cehn17.supermarket.model.Producto;
import com.cehn17.supermarket.model.Sucursal;
import com.cehn17.supermarket.model.Venta;
import com.cehn17.supermarket.repository.ProductoRepository;
import com.cehn17.supermarket.repository.SucursalRepository;
import com.cehn17.supermarket.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VentaService implements IVentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private SucursalRepository sucursalRepository;

    @Override
    public List<VentaDTO> traerVentas() {
        return ventaRepository.findAll()
                .stream()
                .map(Mapper::toDTO)
                .toList();
    }

    @Override
    public VentaDTO crearVenta(VentaDTO ventaDto) {
        //Validaciones
        if(ventaDto == null) throw new NotFoundException("VentaDTO es null");
        if(ventaDto.getIdSucursal() == null) throw new NotFoundException("Debe indicar la Sucursal");
        if(ventaDto.getDetalle() == null || ventaDto.getDetalle().isEmpty())
            throw new NotFoundException("Debe incluir al menos un producto");

        //Buscar la sucursal
        Sucursal sucursal = sucursalRepository.findById(ventaDto.getIdSucursal())
                .orElseThrow(() ->  new NotFoundException("Sucursal no encontrada"));
        //crear la venta
        Venta venta = new Venta();
        venta.setFecha(ventaDto.getFecha());
        venta.setEstado(ventaDto.getEstado());
        venta.setSucursal(sucursal);
        venta.setTotal(ventaDto.getTotal());

        List<DetalleVenta> detalles = new ArrayList<>();

        for(DetalleVentaDTO detalleVentaDTO : ventaDto.getDetalle()){
            Producto p = productoRepository.findByNombre(detalleVentaDTO.getNombreProd())
                    .orElseThrow(() -> new NotFoundException("Producto no encontrado: ".concat(detalleVentaDTO.getNombreProd())));

            //Crear detalle
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(p);
            detalle.setPrecio(detalleVentaDTO.getPrecio());
            detalle.setCantProd(detalleVentaDTO.getCantProd());
            detalle.setVenta(venta);

            detalles.add(detalle);
        }

        venta.setDetalle(detalles);

        return Mapper.toDTO(ventaRepository.save(venta));
    }

    @Override
    public VentaDTO actualizarVenta(Long id, VentaDTO ventaDto) {
        //buscar si la venta existe para actualizarla
        Venta v = ventaRepository.findById(id).orElse(null);
        if (v == null) throw new RuntimeException("Venta no encontrada");

        if (ventaDto.getFecha()!=null) {
            v.setFecha(ventaDto.getFecha());
        }
        if(ventaDto.getEstado()!=null) {
            v.setEstado(ventaDto.getEstado());
        }

        if (ventaDto.getTotal()!=null) {
            v.setTotal(ventaDto.getTotal());
        }

        if (ventaDto.getIdSucursal()!=null) {
            Sucursal suc = sucursalRepository.findById(ventaDto.getIdSucursal()).orElse(null);
            if (suc == null) throw new NotFoundException("Sucursal no encontrada");
            v.setSucursal(suc);
        }

        return Mapper.toDTO(ventaRepository.save(v));
    }

    @Override
    public void eliminarVenta(Long id) {
        if(!ventaRepository.existsById(id)) throw new NotFoundException("Venta no encontrada: ".concat(String.valueOf(id)));
        ventaRepository.deleteById(id);
    }
}
