package com.universidad.inventario.exception;

/**
 * Excepción lanzada cuando se solicita un recurso que no existe en la base de datos.
 * Equivalente semántico a un HTTP 404 Not Found en la capa REST.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Long id) {
        super(String.format("El recurso '%s' con id %d no fue encontrado.", recurso, id));
    }

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
