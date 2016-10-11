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
        String json = null;
        InputStream is = context.getAssets().open("cs273superheros.json");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        json = new String(buffer, "UTF-8");

        try {
            JSONObject jsonRootObject = new JSONObject(json);
            JSONArray allSuperheroesJSON = jsonRootObject.getJSONArray("CS273Superheroes");
            int numberOfHeros = allSuperheroesJSON.length();

            for (int i = 0; i < numberOfHeros; i++) {
                JSONObject superheroJSON = allSuperheroesJSON.getJSONObject(i);
                Superhero hero = new Superhero();
                hero.setUsername(superheroJSON.get("Username"));
                hero.setName(superheroJSON.get("Name"));
                hero.setSuperpower(superheroJSON.get("Superpower"));
                hero.setOneThing(superheroJSON.get("OneThing"));
                hero.setImageName(superheroJSON.get("ImageName"));

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
