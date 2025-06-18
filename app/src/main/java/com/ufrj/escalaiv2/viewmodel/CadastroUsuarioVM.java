package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ufrj.escalaiv2.model.Usuario;
import com.ufrj.escalaiv2.repository.UsuarioRepository;
import com.ufrj.escalaiv2.dto.ApiResponse;
import com.ufrj.escalaiv2.repository.AuthRepository; // Repositório da API

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * ViewModel para CadastroUsuarioActivity, modificado para usar API remota.
 */
public class CadastroUsuarioVM extends AndroidViewModel {

    // Repositório para interagir com a API de autenticação
    private final AuthRepository authRepository;

    // Mantendo a referência ao repositório antigo (comentado)
    // private final UsuarioRepository repository;

    // LiveData para erros de validação ou da API
    private final MutableLiveData<List<String>> erros = new MutableLiveData<>(new ArrayList<>());
    // LiveData para indicar sucesso no cadastro da API
    private final MutableLiveData<ApiResponse<Void>> registrationResult = new MutableLiveData<>();
    // LiveData para estado de carregamento
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    // LiveData para data de nascimento (mantido se a UI ainda precisar)
    private final MutableLiveData<String> dataNascimentoSelecionada = new MutableLiveData<>();

    // Inicializando cadastroSucesso para evitar erro de compilação
    private final MutableLiveData<Boolean> cadastroSucesso = new MutableLiveData<>(false);

    public CadastroUsuarioVM(@NonNull Application application) {
        super(application);
        // Mantendo o código antigo comentado
        // repository = new UsuarioRepository(application);

        // Instancia o repositório da API
        authRepository = new AuthRepository();
    }


    public LiveData<List<String>> getErros() {
        return erros;
    }

    public LiveData<Boolean> getCadastroSucesso() {
        return cadastroSucesso;
    }

    public LiveData<ApiResponse<Void>> getRegistrationResult() {
        return registrationResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getDataNascimentoSelecionada() {
        return dataNascimentoSelecionada;
    }


    public void setDataNascimento(String data) {
        dataNascimentoSelecionada.setValue(data);
    }

    /**
     * Inicia o processo de cadastro chamando a API remota.
     * Envia apenas email e senha.
     *
     * @param email O email do usuário.
     * @param senha A senha do usuário.
     * @param confirmaSenha A confirmação da senha.
     * @param aceitouTermos Se o usuário aceitou os termos.
     */
    public LiveData cadastrarUsuario(String email, String nome, String sobrenome, String dataNascimento, String senha, String confirmaSenha, boolean aceitouTermos) {
        isLoading.setValue(true);
        registrationResult.setValue(null); // Limpa resultado anterior

        List<String> listaErros = validarCadastro(email, nome, sobrenome, dataNascimento, "123", senha, confirmaSenha, aceitouTermos);

        if (!listaErros.isEmpty()) {
            erros.setValue(listaErros);
            isLoading.setValue(false);
            // Retorna um LiveData vazio ou com erro, já que a validação falhou
            MutableLiveData<ApiResponse<Void>> errorResult = new MutableLiveData<>();
            ApiResponse<Void> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Erro de validação");
            errorResult.setValue(errorResponse);
            return errorResult;
        }

        // Chama o repositório para registrar via API
        return authRepository.registerUser(email, nome, sobrenome, dataNascimento, senha);
    }

    /**
     * Método de validação completa (mantido para compatibilidade com o fluxo antigo)
     */
    private List<String> validarCadastro(String email, String nome, String sobrenome,
                                         String dataNasc, String celular, String senha,
                                         String confirmaSenha, boolean aceitouTermos) {
        List<String> listaErros = new ArrayList<>();

        // Validação de campos vazios
        if (email.isEmpty() || nome.isEmpty() || sobrenome.isEmpty() || dataNasc.isEmpty() ||
                celular.isEmpty() || senha.isEmpty() || confirmaSenha.isEmpty()) {
            listaErros.add("É necessário preencher todos os campos.");
        }

        // Validação de email
        if (!Pattern.compile("@").matcher(email).find() ||
                !Pattern.compile("[.]").matcher(email).find()) {
            listaErros.add("É necessário inserir um email válido.");
        }

        // Validação de senha
        if (!senha.isEmpty()) {
            if (senha.length() < 8) {
                listaErros.add("A senha deve possuir mais de 8 caracteres.");
            }
            if (!Pattern.compile("[A-Z]").matcher(senha).find()) {
                listaErros.add("A senha deve possuir ao menos 1 caractere letra maiúscula.");
            }
            if (!Pattern.compile("[a-z]").matcher(senha).find()) {
                listaErros.add("A senha deve possuir ao menos 1 caractere letra minúscula.");
            }
            if (!Pattern.compile("\\d").matcher(senha).find()) {
                listaErros.add("A senha deve possuir ao menos 1 caractere numérico.");
            }
            if (!confirmaSenha.equals(senha)) {
                listaErros.add("As senhas devem ser iguais.");
            }
        }

        // Validação dos termos de uso
        if (!aceitouTermos) {
            listaErros.add("É necessário aceitar os termos de uso.");
        }

        // Validação da idade
        if (!dataNasc.isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date dateOfBirth = dateFormat.parse(dataNasc);
                Calendar calendarDateOfBirth = Calendar.getInstance();
                calendarDateOfBirth.setTime(dateOfBirth);

                Calendar now = Calendar.getInstance();
                int idade = now.get(Calendar.YEAR) - calendarDateOfBirth.get(Calendar.YEAR);

                if (idade < 18) {
                    listaErros.add("Você deve ter pelo menos 18 anos para se cadastrar.");
                }
            } catch (ParseException e) {
                listaErros.add("Data de nascimento inválida.");
            }
        }

        return listaErros;
    }

    /**
     * Método chamado pela Activity para obter o LiveData do repositório após iniciar a chamada.
     * A Activity observará este LiveData para obter a resposta da API.
     */
    public LiveData<ApiResponse<Void>> getApiRegistrationResponse(String email, String nome, String sobrenome, String dataNascimento, String password) {
        // Retorna o LiveData do repositório para a Activity observar
        return authRepository.registerUser(email, nome, sobrenome, dataNascimento, password);
    }

    /**
     * Método chamado pela Activity quando a operação da API (sucesso ou falha) termina,
     * para que a ViewModel possa resetar o estado de carregamento.
     */
    public void registrationAttemptFinished() {
        isLoading.setValue(false);
    }
}
