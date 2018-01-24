package com.Dp;

public  class Segment {
    public final static int length = 2; // Długość w sek
    private float size ;  // Wielkość w MB

    Segment(String quality){
        size = adjustSize(quality);
    }

    public static int adjustSize(String quality){
        switch (quality){
            case "1080p":
                return  10;
            case "720p":
                return 4;
            case "480p":
                return 2;
        }
        return 4;
    }


    int getLength() {
        return length;
    }

    float getSize() {
        return size;
    }

    void setSize(float size) {this.size = size;}
}
