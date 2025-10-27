package com.arka.usuario_service.excepcion;

public class ProveedorNotFoundException extends RuntimeException {
  public ProveedorNotFoundException(String message) {
    super(message);
  }
}
