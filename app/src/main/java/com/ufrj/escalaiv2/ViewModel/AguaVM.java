package com.ufrj.escalaiv2.ViewModel;

public class AguaVM extends ViewModel {
    private final MutableLiveData<Integer> quantidadeTotalAgua;
    private final MutableLiveData<Integer> valorAtualSlider;
    private final MutableLiveData<WaterConsumptionEvent> uiEvent;

    public AguaVM() {
        quantidadeTotalAgua = new MutableLiveData<>(0);
        valorAtualSlider = new MutableLiveData<>(0);
        uiEvent = new MutableLiveData<>();
    }

    public LiveData<Integer> getQuantidadeTotalAgua() {
        return quantidadeTotalAgua;
    }

    public LiveData<Integer> getValorAtualSlider() {
        return valorAtualSlider;
    }

    public LiveData<WaterConsumptionEvent> getUiEvent() {
        return uiEvent;
    }

    public void atualizarValorSlider(int value) {
        valorAtualSlider.setValue(value);
    }

    public void confirmarConsumoAgua() {
        int total = quantidadeTotalAgua.getValue() != null ? quantidadeTotalAgua.getValue() : 0;
        int valorAtual = valorAtualSlider.getValue() != null ? valorAtualSlider.getValue() : 0;

        quantidadeTotalAgua.setValue(total + valorAtual);
        valorAtualSlider.setValue(0);
        uiEvent.setValue(AguaEvent.SHOW_SUCCESS_MESSAGE);
    }
}