package com.ufrj.escalaiv2.enums;

public enum ProfissionalSaude {
    FISIOTERAPEUTA(0, "Fisioterapeuta"),
    MEDICO_ESPORTE(1, "Médico do Esporte"),
    ORTOPEDISTA(2, "Ortopedista"),
    CLINICO_GERAL(3, "Clínico Geral"),
    OUTRO(4, "Outro");

    private final int id;
    private final String nome;

    ProfissionalSaude(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public static ProfissionalSaude getById(int id) {
        for (ProfissionalSaude profissional : values()) {
            if (profissional.getId() == id) {
                return profissional;
            }
        }
        return OUTRO; // Valor padrão
    }

    public static String[] getAllNames() {
        ProfissionalSaude[] profissionais = values();
        String[] names = new String[profissionais.length];

        for (int i = 0; i < profissionais.length; i++) {
            names[i] = profissionais[i].getNome();
        }

        return names;
    }
}
