package com.ufrj.escalaiv2.enums;

public enum AreaCorporalN1 {
    CABECA_E_PESCOCO(0, "Cabeça e pescoço"),
    MEMBROS_SUPERIORES(1, "Membros superiores"),
    TRONCO(2, "Tronco"),
    MEMBROS_INFERIORES(3, "Membros inferiores");

    private final int id;
    private final String nome;

    AreaCorporalN1(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public static AreaCorporalN1 getById(int id) {
        for (AreaCorporalN1 area : values()) {
            if (area.getId() == id) {
                return area;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return nome;
    }
}