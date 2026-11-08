package com.construccion.software;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CalculadoraTest {

    private final Calculadora calculadora = new Calculadora();

    @Test
    void sumaDosNumeros() {
        assertEquals(5, calculadora.sumar(2, 3));
    }

    @Test
    void restaDosNumeros() {
        assertEquals(-1, calculadora.restar(2, 3));
    }

    @Test
    void multiplicaDosNumeros() {
        assertEquals(6, calculadora.multiplicar(2, 3));
    }

    @Test
    void divideDosNumeros() {
        assertEquals(2.5, calculadora.dividir(5, 2));
    }

    @Test
    void dividirEntreCeroLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> calculadora.dividir(1, 0));
    }
}
