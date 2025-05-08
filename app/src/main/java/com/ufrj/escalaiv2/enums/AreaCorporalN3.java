package com.ufrj.escalaiv2.enums;

public enum AreaCorporalN3 {
    // Cabeça/face (0, 0)
    TESTA(0, 0, "Testa"),
    NUCA(1, 0, "Nuca"),

    // Pescoço/coluna cervical (1, 0)
    PESCOCO_ANTERIOR(12, 1, "Pescoço anterior"),
    PESCOCO_POSTERIOR(13, 1, "Pescoço posterior"),
    COLUNA_CERVICAL(14, 1, "Coluna cervical"),
    TRAPEZIO_DIREITO(15, 1, "Trapézio direito"),
    TRAPEZIO_ESQUERDO(16, 1, "Trapézio esquerdo"),

    // Ombro/clavícula (2, 1)
    OMBRO_DIREITO(17, 2, "Ombro direito"),
    OMBRO_ESQUERDO(18, 2, "Ombro esquerdo"),
    CLAVICULA_DIREITA(19, 2, "Clavícula direita"),
    CLAVICULA_ESQUERDA(20, 2, "Clavícula esquerda"),
    ESCAPULA_DIREITA(21, 2, "Escápula direita"),
    ESCAPULA_ESQUERDA(22, 2, "Escápula esquerda"),

    // Braço (3, 1)
    BICEPS_DIREITO(27, 3, "Bíceps direito"),
    BICEPS_ESQUERDO(28, 3, "Bíceps esquerdo"),
    TRICEPS_DIREITO(29, 3, "Tríceps direito"),
    TRICEPS_ESQUERDO(30, 3, "Tríceps esquerdo"),

    // Cotovelo (4, 1)
    COTOVELO_DIREITO(31, 4, "Cotovelo direito"),
    COTOVELO_ESQUERDO(32, 4, "Cotovelo esquerdo"),

    // Antebraço (5, 1)
    ANTEBRACO_DIREITO_ANTERIOR(33, 5, "Antebraço direito anterior"),
    ANTEBRACO_DIREITO_POSTERIOR(34, 5, "Antebraço direito posterior"),
    ANTEBRACO_ESQUERDO_ANTERIOR(35, 5, "Antebraço esquerdo anterior"),
    ANTEBRACO_ESQUERDO_POSTERIOR(36, 5, "Antebraço esquerdo posterior"),

    // Punho (6, 1)
    PUNHO_DIREITO(37, 6, "Punho direito"),
    PUNHO_ESQUERDO(38, 6, "Punho esquerdo"),

    // Mão/dedos/polegar (7, 1)
    PALMA_MAO_DIREITA(39, 7, "Palma da mão direita"),
    COSTA_MAO_DIREITA(40, 7, "Costa da mão direita"),
    PALMA_MAO_ESQUERDA(41, 7, "Palma da mão esquerda"),
    COSTA_MAO_ESQUERDA(42, 7, "Costa da mão esquerda"),
    POLEGAR_DIREITO(43, 7, "Polegar direito"),
    INDICADOR_DIREITO(44, 7, "Indicador direito"),
    MEDIO_DIREITO(45, 7, "Dedo médio direito"),
    ANELAR_DIREITO(46, 7, "Anelar direito"),
    MINIMO_DIREITO(47, 7, "Dedo mínimo direito"),
    POLEGAR_ESQUERDO(48, 7, "Polegar esquerdo"),
    INDICADOR_ESQUERDO(49, 7, "Indicador esquerdo"),
    MEDIO_ESQUERDO(50, 7, "Dedo médio esquerdo"),
    ANELAR_ESQUERDO(51, 7, "Anelar esquerdo"),
    MINIMO_ESQUERDO(52, 7, "Dedo mínimo esquerdo"),

    // Esterno/costela/torácica (8, 2)
    ESTERNO(53, 8, "Esterno"),
    COSTELA_DIREITA(54, 8, "Costela direita"),
    COSTELA_ESQUERDA(55, 8, "Costela esquerda"),
    TORAX_ANTERIOR(56, 8, "Tórax anterior"),
    TORAX_POSTERIOR(57, 8, "Tórax posterior"),
    COLUNA_TORACICA(58, 8, "Coluna torácica"),
    PEITORAL_DIREITO(59, 8, "Peitoral direito"),
    PEITORAL_ESQUERDO(60, 8, "Peitoral esquerdo"),

    // Abdome (9, 2)
    ABDOME_SUPERIOR(61, 9, "Abdome superior"),
    ABDOME_INFERIOR(62, 9, "Abdome inferior"),
    ABDOME_LATERAL_DIREITO(63, 9, "Abdome lateral direito"),
    ABDOME_LATERAL_ESQUERDO(64, 9, "Abdome lateral esquerdo"),

    // Lombar/sacro (10, 2)
    LOMBAR_DIREITA(67, 10, "Lombar direita"),
    LOMBAR_ESQUERDA(68, 10, "Lombar esquerda"),
    SACRO(69, 10, "Sacro"),
    COCCIX(70, 10, "Cóccix"),

    // Quadril/virilha (11, 3)
    QUADRIL_DIREITO(71, 11, "Quadril direito"),
    QUADRIL_ESQUERDO(72, 11, "Quadril esquerdo"),
    VIRILHA_DIREITA(73, 11, "Virilha direita"),
    VIRILHA_ESQUERDA(74, 11, "Virilha esquerda"),
    GLUTEO_DIREITO(75, 11, "Glúteo direito"),
    GLUTEO_ESQUERDO(76, 11, "Glúteo esquerdo"),

    // Coxa (12, 3)
    COXA_DIREITA_ANTERIOR(77, 12, "Coxa direita anterior"),
    COXA_DIREITA_POSTERIOR(78, 12, "Coxa direita posterior"),
    COXA_ESQUERDA_ANTERIOR(79, 12, "Coxa esquerda anterior"),
    COXA_ESQUERDA_POSTERIOR(80, 12, "Coxa esquerda posterior"),

    // Joelho (13, 3)
    JOELHO_DIREITO(81, 13, "Joelho direito"),
    JOELHO_ESQUERDO(82, 13, "Joelho esquerdo"),
    PATELA_DIREITA(83, 13, "Patela direita"),
    PATELA_ESQUERDA(84, 13, "Patela esquerda"),

    // Perna/tendão calcâneo (14, 3)
    PERNA_DIREITA_ANTERIOR(85, 14, "Perna direita anterior"),
    PERNA_DIREITA_POSTERIOR(86, 14, "Perna direita posterior"),
    PERNA_ESQUERDA_ANTERIOR(87, 14, "Perna esquerda anterior"),
    PERNA_ESQUERDA_POSTERIOR(88, 14, "Perna esquerda posterior"),
    PANTURRILHA_DIREITA(89, 14, "Panturrilha direita"),
    PANTURRILHA_ESQUERDA(90, 14, "Panturrilha esquerda"),
    TENDAO_CALCANEO_DIREITO(91, 14, "Tendão calcâneo direito"),
    TENDAO_CALCANEO_ESQUERDO(92, 14, "Tendão calcâneo esquerdo"),

    // Tornozelo (15, 3)
    TORNOZELO_DIREITO(93, 15, "Tornozelo direito"),
    TORNOZELO_ESQUERDO(94, 15, "Tornozelo esquerdo"),
    MALEOLOS_DIREITO(95, 15, "Maléolos direito"),
    MALEOLOS_ESQUERDO(96, 15, "Maléolos esquerdo"),

    // Pé/dedos do pé (16, 3)
    DORSO_PE_DIREITO(97, 16, "Dorso do pé direito"),
    PLANTA_PE_DIREITO(98, 16, "Planta do pé direito"),
    DORSO_PE_ESQUERDO(99, 16, "Dorso do pé esquerdo"),
    PLANTA_PE_ESQUERDO(100, 16, "Planta do pé esquerdo"),
    CALCANHAR_DIREITO(101, 16, "Calcanhar direito"),
    CALCANHAR_ESQUERDO(102, 16, "Calcanhar esquerdo"),
    DEDOS_PE_DIREITO(103, 16, "Dedos do pé direito"),
    DEDOS_PE_ESQUERDO(104, 16, "Dedos do pé esquerdo"),
    HALUX_DIREITO(105, 16, "Hálux direito"),
    HALUX_ESQUERDO(106, 16, "Hálux esquerdo");

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