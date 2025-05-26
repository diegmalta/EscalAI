package com.ufrj.escalaiv2.enums;

public enum TipoTreino {
    ESCALADA_TRADICIONAL_BRASILEIRA(0, "Escalada Tradicional Brasileira"),
    ESCALADA_BOULDER(1, "Escalada Boulder"),
    ESCALADA_ESPORTIVA(2, "Escalada Esportiva"),
    OUTRAS_MODALIDADES_ESCALADA(3, "Outras Modalidades de Escalada"),
    MUSCULACAO(4, "Musculação"),
    OUTRAS_ATIVIDADES_FISICAS(5, "Outras Atividades Físicas");

    private final int id;
    private final String nome;

    TipoTreino(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public static TipoTreino getById(int id) {
        for (TipoTreino tipo : values()) {
            if (tipo.getId() == id) {
                return tipo;
            }
        }
        return OUTRAS_ATIVIDADES_FISICAS; // Valor padrão
    }

    public static String[] getAllNames() {
        TipoTreino[] tipoTreinos = values();
        String[] names = new String[tipoTreinos.length];

        for (int i = 0; i < tipoTreinos.length; i++) {
            names[i] = tipoTreinos[i].getNome();
        }

        return names;
    }
}