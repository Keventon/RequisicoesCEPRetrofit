package com.appco.requisicoeshttp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.appco.requisicoeshttp.api.CEPService;
import com.appco.requisicoeshttp.databinding.ActivityMainBinding;
import com.appco.requisicoeshttp.model.CEP;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    String cep;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Consumo Serviço Web
        retrofit = new Retrofit.Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        binding.buttonRecuperarDados.setOnClickListener(v -> {
            cep = binding.textInputCep.getText().toString();

            if (cep.isEmpty()) {
                binding.progressBar.setVisibility(View.GONE);
            }else {
                binding.progressBar.setVisibility(View.VISIBLE);
                recuperarCep(cep);
            }
        });

    }

    private void recuperarCep(String cep) {
        CEPService cepService = retrofit.create(CEPService.class);
        Call<CEP> call = cepService.recuperarCEP(cep);

        call.enqueue(new Callback<CEP>() {
            @Override
            public void onResponse(Call<CEP> call, Response<CEP> response) {
                if (response.isSuccessful()) {
                    CEP cep = response.body();

                    binding.progressBar.setVisibility(View.GONE);
                    binding.textResultado.setVisibility(View.VISIBLE);
                    binding.textResultado.setText(
                            "CEP: " + cep.getCep()
                                    + "\nLocalidade: "
                                    + cep.getLocalidade() + "\nLogradouro: "
                                    + cep.getLogradouro() + "\nComplemento: "
                                    + cep.getComplemento() +  "\nBairro: "
                                    + cep.getBairro() + "\nUF: "
                                    + cep.getUf());
                }else {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.textResultado.setGravity(Gravity.CENTER);
                    binding.textResultado.setText("Não foi possível buscar este CEP :(");
                }
            }

            @Override
            public void onFailure(Call<CEP> call, Throwable t) {

            }
        });
    }
}