package com.dlim2012.url;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class URLToken {

    int nextToken;
    private final Lock lock = new ReentrantLock();
    URLToken(){

    }

}
