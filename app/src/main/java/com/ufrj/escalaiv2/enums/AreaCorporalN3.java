package com.ufrj.escalaiv2.enums;

public enum AreaCorporalN3 {
    // Cabeça/face (0, 0)
    TESTA(0, 0, "Testa"),
    NUCA(1, 0, "Nuca"),

    // Pescoço/coluna cervical (1, 0)
    PESCOCO_ANTERIOR(2, 1, "Pescoço anterior"),
    PESCOCO_POSTERIOR(3, 1, "Pescoço posterior"),
    COLUNA_CERVICAL(4, 1, "Coluna cervical"),
    TRAPEZIO_DIREITO(5, 1, "Trapézio direito"),
    TRAPEZIO_ESQUERDO(6, 1, "Trapézio esquerdo"),

    // Ombro/clavícula (2, 1)
    OMBRO_DIREITO(7, 2, "Ombro direito"),
    OMBRO_ESQUERDO(8, 2, "Ombro esquerdo"),
    CLAVICULA_DIREITA(9, 2, "Clavícula direita"),
    CLAVICULA_ESQUERDA(10, 2, "Clavícula esquerda"),
    ESCAPULA_DIREITA(11, 2, "Escápula direita"),
    ESCAPULA_ESQUERDA(12, 2, "Escápula esquerda"),

    // Braço (3, 1)
    BICEPS_DIREITO(13, 3, "Bíceps direito"),
    BICEPS_ESQUERDO(14, 3, "Bíceps esquerdo"),
    TRICEPS_DIREITO(15, 3, "Tríceps direito"),
    TRICEPS_ESQUERDO(16, 3, "Tríceps esquerdo"),

    // Cotovelo (4, 1)
    COTOVELO_DIREITO(17, 4, "Cotovelo direito"),
    COTOVELO_ESQUERDO(18, 4, "Cotovelo esquerdo"),

    // Antebraço (5, 1)
    ANTEBRACO_DIREITO_ANTERIOR(19, 5, "Antebraço direito anterior"),
    ANTEBRACO_DIREITO_POSTERIOR(20, 5, "Antebraço direito posterior"),
    ANTEBRACO_ESQUERDO_ANTERIOR(21, 5, "Antebraço esquerdo anterior"),
    ANTEBRACO_ESQUERDO_POSTERIOR(22, 5, "Antebraço esquerdo posterior"),

    // Punho (6, 1)
    PUNHO_DIREITO(23, 6, "Punho direito"),
    PUNHO_ESQUERDO(24, 6, "Punho esquerdo"),

    // Mão/dedos/polegar (7, 1)
    PALMA_MAO_DIREITA(25, 7, "Palma da mão direita"),
    COSTA_MAO_DIREITA(26, 7, "Costa da mão direita"),
    PALMA_MAO_ESQUERDA(27, 7, "Palma da mão esquerda"),
    COSTA_MAO_ESQUERDA(28, 7, "Costa da mão esquerda"),
    POLEGAR_DIREITO(29, 7, "Polegar direito"),
    INDICADOR_DIREITO(30, 7, "Indicador direito"),
    MEDIO_DIREITO(31, 7, "Dedo médio direito"),
    ANELAR_DIREITO(32, 7, "Anelar direito"),
    MINIMO_DIREITO(33, 7, "Dedo mínimo direito"),
    POLEGAR_ESQUERDO(34, 7, "Polegar esquerdo"),
    INDICADOR_ESQUERDO(35, 7, "Indicador esquerdo"),
    MEDIO_ESQUERDO(36, 7, "Dedo médio esquerdo"),
    ANELAR_ESQUERDO(37, 7, "Anelar esquerdo"),
    MINIMO_ESQUERDO(38, 7, "Dedo mínimo esquerdo"),

    // Esterno/costela/torácica (8, 2)
    ESTERNO(39, 8, "Esterno"),
    COSTELA_DIREITA(40, 8, "Costela direita"),
    COSTELA_ESQUERDA(41, 8, "Costela esquerda"),
    TORAX_ANTERIOR(42, 8, "Tórax anterior"),
    TORAX_POSTERIOR(43, 8, "Tórax posterior"),
    COLUNA_TORACICA(44, 8, "Coluna torácica"),
    PEITORAL_DIREITO(45, 8, "Peitoral direito"),
    PEITORAL_ESQUERDO(46, 8, "Peitoral esquerdo"),

    // Abdome (9, 2)
    ABDOME_SUPERIOR(47, 9, "Abdome superior"),
    ABDOME_INFERIOR(48, 9, "Abdome inferior"),
    ABDOME_LATERAL_DIREITO(49, 9, "Abdome lateral direito"),
    ABDOME_LATERAL_ESQUERDO(50, 9, "Abdome lateral esquerdo"),

    // Lombar/sacro (10, 2)
    LOMBAR_DIREITA(51, 10, "Lombar direita"),
    LOMBAR_ESQUERDA(52, 10, "Lombar esquerda"),
    SACRO(53, 10, "Sacro"),
    COCCIX(54, 10, "Cóccix"),

    // Quadril/virilha (11, 3)
    QUADRIL_DIREITO(55, 11, "Quadril direito"),
    QUADRIL_ESQUERDO(56, 11, "Quadril esquerdo"),
    VIRILHA_DIREITA(57, 11, "Virilha direita"),
    VIRILHA_ESQUERDA(58, 11, "Virilha esquerda"),
    GLUTEO_DIREITO(59, 11, "Glúteo direito"),
    GLUTEO_ESQUERDO(60, 11, "Glúteo esquerdo"),

    // Coxa (12, 3)
    COXA_DIREITA_ANTERIOR(61, 12, "Coxa direita anterior"),
    COXA_DIREITA_POSTERIOR(62, 12, "Coxa direita posterior"),
    COXA_ESQUERDA_ANTERIOR(63, 12, "Coxa esquerda anterior"),
    COXA_ESQUERDA_POSTERIOR(64, 12, "Coxa esquerda posterior"),

    // Joelho (13, 3)
    JOELHO_DIREITO(65, 13, "Joelho direito"),
    JOELHO_ESQUERDO(66, 13, "Joelho esquerdo"),
    PATELA_DIREITA(67, 13, "Patela direita"),
    PATELA_ESQUERDA(68, 13, "Patela esquerda"),

    // Perna/tendão calcâneo (14, 3)
    PERNA_DIREITA_ANTERIOR(69, 14, "Perna direita anterior"),
    PERNA_DIREITA_POSTERIOR(70, 14, "Perna direita posterior"),
    PERNA_ESQUERDA_ANTERIOR(71, 14, "Perna esquerda anterior"),
    PERNA_ESQUERDA_POSTERIOR(72, 14, "Perna esquerda posterior"),
    PANTURRILHA_DIREITA(73, 14, "Panturrilha direita"),
    PANTURRILHA_ESQUERDA(74, 14, "Panturrilha esquerda"),
    TENDAO_CALCANEO_DIREITO(75, 14, "Tendão calcâneo direito"),
    TENDAO_CALCANEO_ESQUERDO(76, 14, "Tendão calcâneo esquerdo"),

    // Tornozelo (15, 3)
    TORNOZELO_DIREITO(77, 15, "Tornozelo direito"),
    TORNOZELO_ESQUERDO(78, 15, "Tornozelo esquerdo"),
    MALEOLOS_DIREITO(79, 15, "Maléolos direito"),
    MALEOLOS_ESQUERDO(80, 15, "Maléolos esquerdo"),

    // Pé/dedos do pé (16, 3)
    DORSO_PE_DIREITO(81, 16, "Dorso do pé direito"),
    PLANTA_PE_DIREITO(82, 16, "Planta do pé direito"),
    DORSO_PE_ESQUERDO(83, 16, "Dorso do pé esquerdo"),
    PLANTA_PE_ESQUERDO(84, 16, "Planta do pé esquerdo"),
    CALCANHAR_DIREITO(85, 16, "Calcanhar direito"),
    CALCANHAR_ESQUERDO(86, 16, "Calcanhar esquerdo"),
    DEDOS_PE_DIREITO(87, 16, "Dedos do pé direito"),
    DEDOS_PE_ESQUERDO(88, 16, "Dedos do pé esquerdo"),
    HALUX_DIREITO(89, 16, "Hálux direito"),
    HALUX_ESQUERDO(90, 16, "Hálux esquerdo");

    private final int id;
    private final int areaRegiaoCorporalId;
    private final String nome;

    AreaCorporalN3(int id, int areaRegiaoCorporalId, String nome) {
        this.id = id;
        this.areaRegiaoCorporalId = areaRegiaoCorporalId;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public int getAreaRegiaoCorporalId() {
        return areaRegiaoCorporalId;
    }

    public String getNome() {
        return nome;
    }

    public AreaCorporalN2 getAreaRegiaoCorporal() {
        return AreaCorporalN2.getById(areaRegiaoCorporalId);
    }

    public static AreaCorporalN3 getById(int id) {
        for (AreaCorporalN3 parte : values()) {
            if (parte.getId() == id) {
                return parte;
            }
        }
        return null;
    }

    public static AreaCorporalN3[] getByAreaRegiaoCorporalId(int areaRegiaoCorporalId) {
        return java.util.Arrays.stream(values())
                .filter(parte -> parte.getAreaRegiaoCorporalId() == areaRegiaoCorporalId)
                .toArray(AreaCorporalN3[]::new);
    }

    @Override
    public String toString() {
        return nome;
    }
}