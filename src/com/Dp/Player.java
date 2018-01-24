package com.Dp;

public class Player {
    private float bufferSize;
    private final String quality = "1080p";

    Player(){
        bufferSize = 0;
    }

    float getBufferSize(){
        return bufferSize;
    }

    void setBufferSize(float newBuff){
        bufferSize = newBuff;
    }


}
