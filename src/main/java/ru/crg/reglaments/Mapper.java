package ru.crg.reglaments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class Mapper {

    @NotNull
    public static ReglamentLink mapToReglamentLink(Map<String, Object> oMap, String columnName) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(oMap.get(columnName).toString());
        } catch (Exception e) {
            return new ReglamentLink();
        }

        ReglamentLink reglamentLink = new ReglamentLink();
        try {
            reglamentLink.setUrl(makeUrl(jsonObject.get("url").toString()));
        } catch (JSONException ignored) {

        }

        try {
            reglamentLink.setText(jsonObject.get("text").toString());
        } catch (JSONException ignored) {

        }

        try {
            reglamentLink.setDisabled((Boolean) jsonObject.get("disabled"));
        } catch (JSONException ignored) {

        }

        return reglamentLink;
    }

    @Nullable
    private static URL makeUrl(String rLink) {
        if (rLink == null) {
            return null;
        }

        try {
            return new URL(rLink);
        } catch (MalformedURLException e) {
            try {
                String uri = rLink;
                if (!rLink.startsWith("/")) {
                    uri = "/" + rLink;
                }

                return new URL("http", "localhost", 8100, uri);
            } catch (MalformedURLException malformedURLException) {
                return null;
            }
        }
    }

}
