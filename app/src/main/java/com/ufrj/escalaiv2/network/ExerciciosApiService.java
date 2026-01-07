package com.ufrj.escalaiv2.network;

import com.ufrj.escalaiv2.dto.AreaCorporalN1Response;
import com.ufrj.escalaiv2.dto.AreaCorporalN2Response;
import com.ufrj.escalaiv2.dto.AreaCorporalN3Response;
import com.ufrj.escalaiv2.dto.ExercicioResponse;
import com.ufrj.escalaiv2.dto.TiposResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ExerciciosApiService {

    @GET("exercicios/")
    Call<List<ExercicioResponse>> listarExercicios(
            @Query("tipo") String tipo,
            @Query("n1_id") Integer n1_id,
            @Query("n2_id") Integer n2_id,
            @Query("n3_id") Integer n3_id,
            @Query("dificuldade") String dificuldade,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("exercicios/{exercicio_id}")
    Call<ExercicioResponse> obterExercicio(@Path("exercicio_id") int exercicioId);

    @POST("exercicios/{exercicio_id}/like")
    Call<Void> darLike(@Path("exercicio_id") int exercicioId);

    @GET("exercicios/filtros/tipos")
    Call<TiposResponse> listarTipos();

    @GET("areas-corporais/n1")
    Call<List<AreaCorporalN1Response>> listarAreasN1();

    @GET("areas-corporais/n2")
    Call<List<AreaCorporalN2Response>> listarAreasN2(@Query("n1_id") Integer n1_id);

    @GET("areas-corporais/n3")
    Call<List<AreaCorporalN3Response>> listarAreasN3(@Query("n2_id") Integer n2_id);
}

