package com.konrad.andropaint;


import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;

public class Line implements Parcelable {
    private Path path;
    private int color;
    private float startX;
    private float startY;
    private float stopX;
    private float stopY;

    public Line(){
    }

    public Line(Path path, int color) {
        this.path = path;
        this.color = color;
    }

    public Line(Path path, int color, float startX, float startY, float stopX, float stopY) {
        this.path = path;
        this.color = color;
        this.startX = startX;
        this.startY = startY;
        this.stopX = stopX;
        this.stopY = stopY;
    }

    protected Line(Parcel in) {
        color = in.readInt();
    }

    public static final Creator<Line> CREATOR = new Creator<Line>() {
        @Override
        public Line createFromParcel(Parcel in) {
            return new Line(in);
        }

        @Override
        public Line[] newArray(int size) {
            return new Line[size];
        }
    };

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getStopX() {
        return stopX;
    }

    public void setStopX(float stopX) {
        this.stopX = stopX;
    }

    public float getStopY() {
        return stopY;
    }

    public void setStopY(float stopY) {
        this.stopY = stopY;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(color);
    }
}
