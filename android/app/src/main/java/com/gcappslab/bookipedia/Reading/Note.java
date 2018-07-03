package com.gcappslab.bookipedia.Reading;

import android.support.annotation.NonNull;

/**
 * Note object
 */

public class Note implements Comparable<Note>{

    private int pos;
    private int end;
    private String text;
    private char type;

    public Note(int pos, String text, char type) {
        this.pos = pos;
        this.text = text;
        this.type = type;

        if (type == 'H'){
            end = pos + text.length();
        }
    }

    @Override
    public int compareTo(@NonNull Note o) {
        int p2 = o.getPos();
        return (int) Math.signum(pos-p2);
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
