package com.ufrj.escalaiv2.enums;

public enum GrauEscaladaBrasileiro {
    TERCEIRO_GRAU(0, "3º grau"),
    QUARTO_GRAU(1, "4º grau"),
    QUINTO_GRAU(2, "5º grau"),
    QUINTO_SUP(3, "5º sup"),
    SEXTO_GRAU(4, "6º grau"),
    SEXTO_SUP(5, "6º sup"),
    SETIMO_GRAU(6, "7º grau"),
    SETIMO_SUP(7, "7º sup"),
    OITAVO_GRAU(8, "8º grau"),
    OITAVO_SUP(9, "8º sup"),
    NONO_GRAU(10, "9º grau"),
    NONO_SUP(11, "9º sup"),
    DECIMO_GRAU(12, "10º grau"),
    DECIMO_SUP(13, "10º sup"),
    DECIMO_PRIMEIRO_GRAU(14, "11º grau"),
    DECIMO_PRIMEIRO_SUP(15, "11º sup"),
    DECIMO_SEGUNDO_GRAU(16, "12º grau");

    private final int id;
    private final String nome;

    GrauEscaladaBrasileiro(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public static GrauEscaladaBrasileiro getById(int id) {
        for (GrauEscaladaBrasileiro grau : values()) {
            if (grau.getId() == id) {
                return grau;
            }
        }
        return SEXTO_GRAU; // Valor padrão
    }

    public static String[] getAllNames() {
        GrauEscaladaBrasileiro[] graus = values();
        String[] names = new String[graus.length];

        for (int i = 0; i < graus.length; i++) {
            names[i] = graus[i].getNome();
        }

        return names;
    }
}
