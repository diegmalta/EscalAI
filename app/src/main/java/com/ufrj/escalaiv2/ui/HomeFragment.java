package com.ufrj.escalaiv2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ufrj.escalaiv2.R;
import com.ufrj.escalaiv2.activity.AguaActivity;
import com.ufrj.escalaiv2.activity.HumorActivity;
import com.ufrj.escalaiv2.activity.DorActivity;
import com.ufrj.escalaiv2.activity.SonoActivity;
import com.ufrj.escalaiv2.activity.TreinoActivity;
import com.ufrj.escalaiv2.activity.LesaoActivity;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button aguaButton = view.findViewById(R.id.aguaButton);
        aguaButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AguaActivity.class);
            startActivity(intent);
        });

        Button humorButton = view.findViewById(R.id.humorButton);
        humorButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HumorActivity.class);
            startActivity(intent);
        });

        Button dorButton = view.findViewById(R.id.dorButton);
        dorButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DorActivity.class);
            startActivity(intent);
        });

        Button sonoButton = view.findViewById(R.id.sonoButton);
        sonoButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SonoActivity.class);
            startActivity(intent);
        });

        Button treinoButton = view.findViewById(R.id.treinoButton);
        treinoButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TreinoActivity.class);
            startActivity(intent);
        });

        Button bloco6Button = view.findViewById(R.id.bloco6Button);
        bloco6Button.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LesaoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

