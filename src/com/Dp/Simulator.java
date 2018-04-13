package com.Dp;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import javax.swing.*;
import java.util.*;

public class Simulator {
    private Event event;
    private Player player;
    private Random rand = new Random();
    private ArrayList<Float> timeStats = new ArrayList<>(), speedTime = new ArrayList<>();
    private ArrayList<Float> bufferStats = new ArrayList<>();
    private ArrayList<Integer> speedStats = new ArrayList<>();
    private ArrayList<Integer> qualityStats = new ArrayList<>();

    private final String startLOAD = "startLOAD", speedCHANGE = "speedCHANGE", endLOAD = "endLOAD",
            FHD = "1080p", HD = "720p", SD = "480p";

    private final int vHighSpeed= 8, highSpeed = 6, midSpeed = 4, lowSpeed = 2 , vLowSpeed = 1,
            bufferMinSize = 30, bufferMaxSize = 60, speedChanges = 10;
    private  float lambda ;

    private PriorityQueue<Event> events = new PriorityQueue<>(speedChanges + 1,eventComparator);

    private static Comparator<Event> eventComparator = new Comparator<Event>() {
        @Override
        public int compare(Event o1, Event o2) {
            return (int) (o1.getEndTime() - o2.getEndTime());
        }
    };

    void startSimulation(int totalTime, boolean adaptive){
        float currentTime = 0,
                time= 0,
            playingTime, loadTime;
        int netSpeed = vLowSpeed;
        lambda = (float) 2/totalTime;
        player = new Player();
        player.setBufferSize(20);// Setting buffor size on start of sim
        String quality = setQuality(netSpeed) ;
        getExpDistribution(totalTime);
        Segment seg = new Segment(quality);

        speedStats.add(netSpeed);
        speedTime.add(currentTime);

        qualityStats.add(parseQuality(quality));
        bufferStats.add(player.getBufferSize());
        timeStats.add(currentTime);

        events.add(new Event(startLOAD,currentTime));
        while (currentTime < totalTime){
            event = events.poll();

            switch (event.getType()){
                case startLOAD:
                    events.add(new Event(endLOAD, seg, netSpeed, currentTime));
                    break;
                case speedCHANGE:
                    seg.setSize(seg.getSize() - netSpeed*(event.getEndTime() - currentTime));
                    currentTime = event.getEndTime();
                    speedStats.add(netSpeed);
                    speedTime.add(currentTime);
                    if(adaptive)
                        netSpeed = randomSpeed();
                        //netSpeed = newSpeed(netSpeed);
                    else
                        netSpeed = getHighorLow(netSpeed);
                    speedStats.add(netSpeed);
                    speedTime.add(currentTime);
                    removeEndLoad();
                    events.add(new Event(startLOAD,currentTime));
                    break;
                case endLOAD:
                    player.setBufferSize(player.getBufferSize() + seg.getLength());
                    currentTime = event.getEndTime();
                    if(adaptive)
                        quality = setQuality(netSpeed);
                    if(player.getBufferSize() > bufferMaxSize){
                        loadTime = player.getBufferSize() - bufferMaxSize;
                        currentTime += loadTime;
                        events.add(new Event(startLOAD,currentTime));
                        seg = new Segment(quality);
                    } else {
                        seg = new Segment(quality);
                        events.add(new Event(startLOAD, currentTime));
                    }
                    break;
            }
            if(time < currentTime){
                playingTime = currentTime - time;
                if(playingTime> player.getBufferSize()) {
                    player.setBufferSize(0);
                    System.out.println("Buffering at " + currentTime);
                }else {
                    player.setBufferSize(player.getBufferSize() - playingTime);
                }
                time = currentTime;

                qualityStats.add(parseQuality(quality));
                bufferStats.add(player.getBufferSize());
                timeStats.add(currentTime);
                speedStats.add(netSpeed);
                speedTime.add(currentTime);
            }
        }

        System.out.println("Total time " + totalTime);
        System.out.println("Current time "+ currentTime);
        ShowStats();
    }

    private String setQuality(int speed){
        float bufferSize = player.getBufferSize() + Segment.length;
            if(bufferSize - (float) Segment.adjustSize(FHD)/speed > bufferMinSize)
                return FHD;
            else if(bufferSize - (float) Segment.adjustSize(HD)/speed > bufferMinSize)
                return HD;
            else
                return SD;
    }

    private void ShowStats()
    {
        XYChart chart = QuickChart.getChart("Buffer and Connection speed ", "Time", "Buffer size",
                "Buffer size[s]", timeStats, bufferStats);
        chart.addSeries("Speed ", speedTime, speedStats);
        XYChart QChart = QuickChart.getChart("Quality Stats", "Time", "Resolution", "Quality",
                timeStats, qualityStats);
        new SwingWrapper<>(chart).displayChart().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        new SwingWrapper<>(QChart).displayChart().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    private void removeEndLoad(){
        Event event = null;
        for (Event e : events){
            if(e.getType().equals(endLOAD))
                event = e;
        }
        if(event != null)
            events.remove(event);

    }

    private int randomSpeed(){
        return rand.nextInt(8) +1;
    }

    private int newSpeed(int  speed){
        int random = rand.nextInt(5);
        switch (random){
            case 0:
                if(speed != vLowSpeed)
                    return vLowSpeed;
            case 1:
                if(speed != lowSpeed)
                    return lowSpeed;
            case 2:
                if(speed != midSpeed)
                    return midSpeed;
            case 3:
                if(speed != highSpeed)
                    return highSpeed;
            case 4:
                if(speed != vHighSpeed)
                    return vHighSpeed;

        }
        return speed;
    }

    private int parseQuality(String quality){

        return Integer.parseInt(quality.substring(0 , quality.length() -1));
    }

    private int getHighorLow(int speed) {
        if(speed == vHighSpeed)
            return vLowSpeed;
        else
            return vHighSpeed;
    }


    private void  getExpDistribution(int totalTime){
        Event event ;
        float tmp;
        for(int i = 0 ; i< speedChanges; i++){
            do {
                tmp = (float) Math.log(1 - rand.nextDouble()) / (-lambda);
            }while (tmp > totalTime || tmp < 0);
                event = new Event(speedCHANGE, tmp);
                events.add(event);
                System.out.println(tmp);
        }
    }


}
