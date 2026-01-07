package com.ufrj.escalaiv2.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class LesaoResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private LesaoData data;

    @SerializedName("lesoes")
    private List<LesaoData> lesoes;

    public static class LesaoData implements Parcelable {
        @SerializedName("id")
        private int id;

        @SerializedName("user_id")
        private int userId;

        @SerializedName("area_lesao_n1")
        private int areaLesaoN1;

        @SerializedName("area_lesao_n2")
        private int areaLesaoN2;

        @SerializedName("area_lesao_n3")
        private int areaLesaoN3;

        @SerializedName("massa")
        private float massa;

        @SerializedName("altura")
        private int altura;

        @SerializedName("grau_escalada")
        private int grauEscalada;

        @SerializedName("tempo_pratica_meses")
        private int tempoPraticaMeses;

        @SerializedName("frequencia_semanal")
        private int frequenciaSemanal;

        @SerializedName("horas_semanais")
        private int horasSemanais;

        @SerializedName("lesoes_previas")
        private int lesoesPrevias;

        @SerializedName("reincidencia")
        private boolean reincidencia;

        @SerializedName("buscou_atendimento")
        private boolean buscouAtendimento;

        @SerializedName("profissional_atendimento")
        private int profissionalAtendimento;

        @SerializedName("diagnostico")
        private int diagnostico;

        @SerializedName("profissional_tratamento")
        private int profissionalTratamento;

        @SerializedName("modalidade_praticada")
        private int modalidadePraticada;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("updated_at")
        private String updatedAt;

        // Campos de data da lesão
        @SerializedName("data_inicio")
        private String dataInicio;

        @SerializedName("data_conclusao")
        private String dataConclusao;

        // Campos de previsão de afastamento
        @SerializedName("tempo_afastamento_meses")
        private Double tempoAfastamentoMeses;

        @SerializedName("intervalo_confianca_min")
        private Double intervaloConfiancaMin;

        @SerializedName("intervalo_confianca_max")
        private Double intervaloConfiancaMax;

        // Construtor padrão
        public LesaoData() {}

        // Construtor Parcelable
        protected LesaoData(Parcel in) {
            id = in.readInt();
            userId = in.readInt();
            areaLesaoN1 = in.readInt();
            areaLesaoN2 = in.readInt();
            areaLesaoN3 = in.readInt();
            massa = in.readFloat();
            altura = in.readInt();
            grauEscalada = in.readInt();
            tempoPraticaMeses = in.readInt();
            frequenciaSemanal = in.readInt();
            horasSemanais = in.readInt();
            lesoesPrevias = in.readInt();
            reincidencia = in.readByte() != 0;
            buscouAtendimento = in.readByte() != 0;
            profissionalAtendimento = in.readInt();
            diagnostico = in.readInt();
            profissionalTratamento = in.readInt();
            modalidadePraticada = in.readInt();
            createdAt = in.readString();
            updatedAt = in.readString();
            dataInicio = in.readString();
            dataConclusao = in.readString();
            // Ler Double com verificação de null usando flag
            boolean hasTempo = in.readByte() != 0;
            tempoAfastamentoMeses = hasTempo ? in.readDouble() : null;
            boolean hasMin = in.readByte() != 0;
            intervaloConfiancaMin = hasMin ? in.readDouble() : null;
            boolean hasMax = in.readByte() != 0;
            intervaloConfiancaMax = hasMax ? in.readDouble() : null;
        }

        public static final Creator<LesaoData> CREATOR = new Creator<LesaoData>() {
            @Override
            public LesaoData createFromParcel(Parcel in) {
                return new LesaoData(in);
            }

            @Override
            public LesaoData[] newArray(int size) {
                return new LesaoData[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeInt(userId);
            dest.writeInt(areaLesaoN1);
            dest.writeInt(areaLesaoN2);
            dest.writeInt(areaLesaoN3);
            dest.writeFloat(massa);
            dest.writeInt(altura);
            dest.writeInt(grauEscalada);
            dest.writeInt(tempoPraticaMeses);
            dest.writeInt(frequenciaSemanal);
            dest.writeInt(horasSemanais);
            dest.writeInt(lesoesPrevias);
            dest.writeByte((byte) (reincidencia ? 1 : 0));
            dest.writeByte((byte) (buscouAtendimento ? 1 : 0));
            dest.writeInt(profissionalAtendimento);
            dest.writeInt(diagnostico);
            dest.writeInt(profissionalTratamento);
            dest.writeInt(modalidadePraticada);
            dest.writeString(createdAt);
            dest.writeString(updatedAt);
            dest.writeString(dataInicio);
            dest.writeString(dataConclusao);
            // Escrever Double com verificação de null usando flag
            dest.writeByte((byte) (tempoAfastamentoMeses != null ? 1 : 0));
            if (tempoAfastamentoMeses != null) {
                dest.writeDouble(tempoAfastamentoMeses);
            }
            dest.writeByte((byte) (intervaloConfiancaMin != null ? 1 : 0));
            if (intervaloConfiancaMin != null) {
                dest.writeDouble(intervaloConfiancaMin);
            }
            dest.writeByte((byte) (intervaloConfiancaMax != null ? 1 : 0));
            if (intervaloConfiancaMax != null) {
                dest.writeDouble(intervaloConfiancaMax);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        // Getters e Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getAreaLesaoN1() {
            return areaLesaoN1;
        }

        public void setAreaLesaoN1(int areaLesaoN1) {
            this.areaLesaoN1 = areaLesaoN1;
        }

        public int getAreaLesaoN2() {
            return areaLesaoN2;
        }

        public void setAreaLesaoN2(int areaLesaoN2) {
            this.areaLesaoN2 = areaLesaoN2;
        }

        public int getAreaLesaoN3() {
            return areaLesaoN3;
        }

        public void setAreaLesaoN3(int areaLesaoN3) {
            this.areaLesaoN3 = areaLesaoN3;
        }

        public float getMassa() {
            return massa;
        }

        public void setMassa(float massa) {
            this.massa = massa;
        }

        public int getAltura() {
            return altura;
        }

        public void setAltura(int altura) {
            this.altura = altura;
        }

        public int getGrauEscalada() {
            return grauEscalada;
        }

        public void setGrauEscalada(int grauEscalada) {
            this.grauEscalada = grauEscalada;
        }

        public int getTempoPraticaMeses() {
            return tempoPraticaMeses;
        }

        public void setTempoPraticaMeses(int tempoPraticaMeses) {
            this.tempoPraticaMeses = tempoPraticaMeses;
        }

        public int getFrequenciaSemanal() {
            return frequenciaSemanal;
        }

        public void setFrequenciaSemanal(int frequenciaSemanal) {
            this.frequenciaSemanal = frequenciaSemanal;
        }

        public int getHorasSemanais() {
            return horasSemanais;
        }

        public void setHorasSemanais(int horasSemanais) {
            this.horasSemanais = horasSemanais;
        }

        public int getLesoesPrevias() {
            return lesoesPrevias;
        }

        public void setLesoesPrevias(int lesoesPrevias) {
            this.lesoesPrevias = lesoesPrevias;
        }

        public boolean isReincidencia() {
            return reincidencia;
        }

        public void setReincidencia(boolean reincidencia) {
            this.reincidencia = reincidencia;
        }

        public boolean isBuscouAtendimento() {
            return buscouAtendimento;
        }

        public void setBuscouAtendimento(boolean buscouAtendimento) {
            this.buscouAtendimento = buscouAtendimento;
        }

        public int getProfissionalAtendimento() {
            return profissionalAtendimento;
        }

        public void setProfissionalAtendimento(int profissionalAtendimento) {
            this.profissionalAtendimento = profissionalAtendimento;
        }

        public int getDiagnostico() {
            return diagnostico;
        }

        public void setDiagnostico(int diagnostico) {
            this.diagnostico = diagnostico;
        }

        public int getProfissionalTratamento() {
            return profissionalTratamento;
        }

        public void setProfissionalTratamento(int profissionalTratamento) {
            this.profissionalTratamento = profissionalTratamento;
        }

        public int getModalidadePraticada() {
            return modalidadePraticada;
        }

        public void setModalidadePraticada(int modalidadePraticada) {
            this.modalidadePraticada = modalidadePraticada;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getDataInicio() {
            return dataInicio;
        }

        public void setDataInicio(String dataInicio) {
            this.dataInicio = dataInicio;
        }

        public String getDataConclusao() {
            return dataConclusao;
        }

        public void setDataConclusao(String dataConclusao) {
            this.dataConclusao = dataConclusao;
        }

        public Double getTempoAfastamentoMeses() {
            return tempoAfastamentoMeses;
        }

        public void setTempoAfastamentoMeses(Double tempoAfastamentoMeses) {
            this.tempoAfastamentoMeses = tempoAfastamentoMeses;
        }

        public Double getIntervaloConfiancaMin() {
            return intervaloConfiancaMin;
        }

        public void setIntervaloConfiancaMin(Double intervaloConfiancaMin) {
            this.intervaloConfiancaMin = intervaloConfiancaMin;
        }

        public Double getIntervaloConfiancaMax() {
            return intervaloConfiancaMax;
        }

        public void setIntervaloConfiancaMax(Double intervaloConfiancaMax) {
            this.intervaloConfiancaMax = intervaloConfiancaMax;
        }
    }

    // Getters e Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LesaoData getData() {
        return data;
    }

    public void setData(LesaoData data) {
        this.data = data;
    }

    public List<LesaoData> getLesoes() {
        return lesoes;
    }

    public void setLesoes(List<LesaoData> lesoes) {
        this.lesoes = lesoes;
    }
}
