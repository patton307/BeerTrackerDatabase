package com.theironyard;

/**
 * Created by zach on 10/27/15.
 */
public class Beer {
    int id;
    String name;
    String type;

    public Beer(int id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Beer(String name) {
        this.name = name;
    }
}
