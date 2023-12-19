package com.example.s___felate__3;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btn;
    Spinner spinner;
    private JSONArray sa = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn);
        spinner = findViewById(R.id.spinner);

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        String url = "https://run.mocky.io/v3/d9293f76-e064-4731-ba9f-dcef017cca1a";
        //String url = "https://run.mocky.io/v3/d9293f76-e064-4731-ba9f-dcef017cca1a";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        sa = response;

                        ArrayList<String> movieNames = new ArrayList<>();

                        try {
                            for (int i = 0; i < sa.length(); i++) {
                                JSONObject movie = sa.getJSONObject(i);
                                String name = movie.getString("name");
                                movieNames.add(name);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                                android.R.layout.simple_spinner_dropdown_item, movieNames);
                        spinner.setAdapter(adapter);

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                try {
                                    JSONObject selectedMovie = sa.getJSONObject(position);
                                    String name = selectedMovie.getString("name");
                                    String price = selectedMovie.getString("price");
                                    String imageUrl = selectedMovie.getString("image");
                                    boolean translate = selectedMovie.getBoolean("traduction");

                                    TextView nameTextView = findViewById(R.id.nameTextView);
                                    nameTextView.setText(name);

                                    Switch swi = findViewById(R.id.swi);
                                    swi.setChecked(translate);
                                    swi.setEnabled(false);

                                    TextView priceTextView = findViewById(R.id.price);
                                    priceTextView.setText(price + " MAD");

                                    ImageView imageView = findViewById(R.id.imageView);
                                    Picasso.get().load(imageUrl).into(imageView);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject selectedMovie = sa.getJSONObject(spinner.getSelectedItemPosition());
                    double price = selectedMovie.getDouble("price");
                    EditText quantityEditText = findViewById(R.id.quantity);
                    String quantityStr = quantityEditText.getText().toString();

                    if (!TextUtils.isEmpty(quantityStr)) {
                        int quantity = Integer.parseInt(quantityStr);

                        double totalPrice = price * quantity;

                        if (quantity > 1) {
                            totalPrice *= 0.9;
                        }

                        String message = "Total Price: " + totalPrice + " MAD";
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Please enter a valid quantity.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException | NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error calculating the total price.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        requestQueue.add(jsonArrayRequest);
    }
}