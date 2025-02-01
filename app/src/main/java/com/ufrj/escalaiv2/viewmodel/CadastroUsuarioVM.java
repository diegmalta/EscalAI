package com.ufrj.escalaiv2.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ufrj.escalaiv2.model.Usuario;
import com.ufrj.escalaiv2.repository.UsuarioRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class CadastroUsuarioVM extends AndroidViewModel {
    private UsuarioRepository repository;
    private final MutableLiveData<List<String>> erros = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> cadastroSucesso = new MutableLiveData<>(false);
    private final MutableLiveData<String> dataNascimentoSelecionada = new MutableLiveData<>();

    public CadastroUsuarioVM(Application application) {
        super(application);
        repository = new UsuarioRepository(application);
    }

    public LiveData<List<String>> getErros() {
        return erros;
    }

    public LiveData<Boolean> getCadastroSucesso() {
        return cadastroSucesso;
    }

    public LiveData<String> getDataNascimentoSelecionada() {
        return dataNascimentoSelecionada;
    }

    public void setDataNascimento(String data) {
        dataNascimentoSelecionada.setValue(data);
    }

    public void cadastrarUsuario(String email, String nome, String sobrenome,
                                 String dataNasc, String celular, String senha,
                                 String confirmaSenha, boolean aceitouTermos) {
        List<String> listaErros = validarCadastro(email, nome, sobrenome,
                dataNasc, celular, senha,
                confirmaSenha, aceitouTermos);

        if (!listaErros.isEmpty()) {
            erros.setValue(listaErros);
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setNome(nome);
        usuario.setSobrenome(sobrenome);
        usuario.setDataNasc(dataNasc);
        usuario.setCelular(celular);
        usuario.setSenha(senha);

        // Executa inserção em background thread através do Repository
        repository.insert(usuario);
        cadastroSucesso.setValue(true);
    }

    private List<String> validarCadastro(String email, String nome, String sobrenome,
                                         String dataNasc, String celular, String senha,
                                         String confirmaSenha, boolean aceitouTermos) {
        List<String> listaErros = new ArrayList<>();

        // Validação de campos vazios
        if (email.isEmpty() || nome.isEmpty() || dataNasc.isEmpty() ||
                celular.isEmpty() || senha.isEmpty() || confirmaSenha.isEmpty()) {
            listaErros.add("É necessário preencher todos os campos.");
        }

        // Validação de email
        if (!Pattern.compile("[@]").matcher(email).find() ||
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
            if (!Pattern.compile("[0-9]").matcher(senha).find()) {
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
}