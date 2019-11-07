package com.example.midproject;

import java.util.Calendar;

public class Memo {
    int _id;
    Calendar date;
    String context;

    public Memo(int _id, Calendar date, String context) {
        this._id = _id;
        this.date = date;
        this.context = context;
    }
}