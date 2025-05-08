package com.ufrj.escalaiv2.enums;

public enum AreaCorporalN2 {
    // Cabeça e pescoço
    CABECA_FACE(0, 0, "Cabeça/face"),
    PESCOCO_COLUNA_CERVICAL(1, 0, "Pescoço/coluna cervical"),

    // Membros superiores
    OMBRO_CLAVICULA(2, 1, "Ombro/clavícula"),
    BRACO(3, 1, "Braço"),
    COTOVELO(4, 1, "Cotovelo"),
    ANTEBRACO(5, 1, "Antebraço"),
    PUNHO(6, 1, "Punho"),
    MAO_DEDOS_POLEGAR(7, 1, "Mão/dedos/polegar"),

    // Tronco
    ESTERNO_COSTELA_TORACICA(8, 2, "Esterno/costela/torácica"),
    ABDOME(9, 2, "Abdome"),
    LOMBAR_SACRO(10, 2, "Lombar/sacro"),

    // Membros inferiores
    QUADRIL_VIRILHA(11, 3, "Quadril/virilha"),
    COXA(12, 3, "Coxa"),
    JOELHO(13, 3, "Joelho"),
    PERNA_TENDAO_CALCANEO(14, 3, "Perna/tendão calcâneo"),
    TORNOZELO(15, 3, "Tornozelo"),
    PE_DEDOS_DO_PE(16, 3, "Pé/dedos do pé");

    private final int id;
    private final int areaId;
    private final String nome;

    AreaCorporalN2(int id, int areaId, String nome) {
        this.id = id;
        this.areaId = areaId;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public int getAreaId() {
        return areaId;
    }

    public String getNome() {
        return nome;
    }

    public static AreaCorporalN2 getById(int id) {
        for (AreaCorporalN2 subarea : values()) {
            if (subarea.getId() == id) {
                return subarea;
            }
        }
        return null;
    }

    public static AreaCorporalN2[] getByRegiaoCorporalId(int areaId) {
        return java.util.Arrays.stream(values())
                .filter(subarea -> subarea.getAreaId() == areaId)
                .toArray(AreaCorporalN2[]::new);
    }

    @Override
    public String toString() {
        return nome;
    }
}