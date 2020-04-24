package ru.crg.reglaments;

import lombok.Data;

import java.net.URL;

@Data
public class ReglamentLink {

    private URL url;
    private String text;
    private boolean disabled;

}
