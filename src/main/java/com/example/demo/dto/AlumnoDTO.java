package com.example.demo.dto;

import com.example.demo.entity.EstadoAlumno;

import lombok.Data;

@Data
public class AlumnoDTO {

    private Integer idAlumno;
    private int dniAlumno;
    private String nombre;
    private String apellido;
    private String direccion;
    private EstadoAlumno estadoActual;
    private Integer idMatricula;
    private Integer idPago;

}
