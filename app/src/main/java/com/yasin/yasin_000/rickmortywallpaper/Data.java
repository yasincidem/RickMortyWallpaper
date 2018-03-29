package com.yasin.yasin_000.rickmortywallpaper;

import java.util.ArrayList;

/**
 * Created by yasin_000 on 20.9.2017.
 */

public class Data {
    private ArrayList<String> imageId;

    public Data(ArrayList<String> imageId) {
        this.imageId = imageId;
    }

    public Data(){}
    public ArrayList<String> getImageId() {
        return imageId;
    }

    public void setImageId(ArrayList<String> imageId) {
        this.imageId = imageId;
    }
}
