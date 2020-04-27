package ru.crg.test_tool;

import lombok.Data;

import java.net.URL;

@Data
public class ReglamentLink {

    private URL url;
    private String text;
    private boolean disabled;

}
