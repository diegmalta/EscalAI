package com.ufrj.escalaiv2.Repository;

public class AguaRepository {
    private final SharedPreferences preferences;

    public AguaRepository(Context context) {
        preferences = context.getSharedPreferences("agua", Context.MODE_PRIVATE);
    }

    public void salvarQuantidadeAgua(int amount) {
        preferences.edit().putInt("total_agua", amount).apply();
    }

    public int getQuantidadeAgua() {
        return preferences.getInt("total_agua", 0);
    }
}