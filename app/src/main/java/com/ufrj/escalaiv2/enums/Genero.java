package com.ufrj.escalaiv2.enums;

public enum Genero {
    HOMEM_CISGENERO(0, "Homem cisgênero"),
    MULHER_CISGENERO(1, "Mulher cisgênero"),
    HOMEM_TRANSGENERO(2, "Homem transgênero"),
    MULHER_TRANSGENERO(3, "Mulher transgênero"),
    NAO_BINARIO_MASCULINO(4, "Não-binário do sexo masculino"),
    NAO_BINARIO_FEMININO(5, "Não-binário do sexo feminino"),
    PREFIRO_NAO_DIZER(6, "Prefiro não dizer");

    private final int id;
    private final String nome;

    Genero(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public static Genero getById(int id) {
        for (Genero genero : values()) {
            if (genero.getId() == id) {
                return genero;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return nome;
    }
}
