package edu.orangecoastcollege.cs273.kfrederick5.cs273superheroes;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Link on 10/10/2016.
 */

public class JSONLoader {

    public static ArrayList<Superhero> loadJSONFromAsset(Context context) throws IOException{
        ArrayList<Superhero> allSuperheroes = new ArrayList<>();
        String json;
        InputStream is = context.getAssets().open("cs273superheroes.json");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        json = new String(buffer, "UTF-8");

        try {
            JSONObject jsonRootObject = new JSONObject(json);
            JSONArray allSuperheroesJSON = jsonRootObject.getJSONArray("CS273Superheroes");
            int numberOfHeroes = allSuperheroesJSON.length();

            for (int i = 0; i < numberOfHeroes; i++) {
                JSONObject superheroJSON = allSuperheroesJSON.getJSONObject(i);

                Superhero hero = new Superhero();


                hero.setUsername(superheroJSON.getString("Username"));
                hero.setName(superheroJSON.getString("Name"));
                hero.setSuperpower(superheroJSON.getString("Superpower"));
                hero.setOneThing(superheroJSON.getString("OneThing"));
                hero.setImageName(superheroJSON.getString("ImageName"));

                allSuperheroes.add(hero);
            }
        }

        catch(JSONException e)
        {
            Log.e("CS273 Superheroes", e.getMessage());
        }

        return allSuperheroes;
    }
}
