package com.ufrj.escalaiv2.enums;

public enum DiagnosticoLesao {
    TENDINITE(0, "Tendinite"),
    CONTUSAO(1, "Contusão"),
    BURSITE(2, "Bursite"),
    FRATURA(3, "Fratura"),
    ENTORSE(4, "Entorse"),
    LUXACAO(5, "Luxação"),
    LESAO_MUSCULAR(6, "Lesão Muscular"),
    LESAO_LIGAMENTAR(7, "Lesão Ligamentar"),
    LESAO_MENISCAL(8, "Lesão Meniscal"),
    PULLI(9, "Pulli"),
    OUTRO(10, "Outro");

    private final int id;
    private final String nome;

    DiagnosticoLesao(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public static DiagnosticoLesao getById(int id) {
        for (DiagnosticoLesao diagnostico : values()) {
            if (diagnostico.getId() == id) {
                return diagnostico;
            }
        }
        return OUTRO; // Valor padrão
    }

    public static String[] getAllNames() {
        DiagnosticoLesao[] diagnosticos = values();
        String[] names = new String[diagnosticos.length];

        for (int i = 0; i < diagnosticos.length; i++) {
            names[i] = diagnosticos[i].getNome();
        }

        return names;
    }
}
