package com.cehn17.supermarket.service;

import com.cehn17.supermarket.dto.SucursalDTO;

import java.util.List;

public interface ISucursalService {

    List<SucursalDTO> traerSucursales();
    SucursalDTO crearSucursal(SucursalDTO sucursalDto);
    SucursalDTO actualizarSucursal(Long id,SucursalDTO sucursalDto);
    void eliminarSucursal(Long id);
}

