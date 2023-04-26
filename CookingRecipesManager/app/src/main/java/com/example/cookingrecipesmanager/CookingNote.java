package com.example.cookingrecipesmanager;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CookingNote implements Parcelable {
    private String title;
    private String author;
    private String description;
    private int img;
    private Float evaluate;
    private Boolean iFavorites;

    public CookingNote(String title, String author, String description, int img, Float evaluate, Boolean iFavorites) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.img = img;
        this.evaluate = evaluate;
        this.iFavorites = iFavorites;
    }

    protected CookingNote(Parcel in) {
        title = in.readString();
        author = in.readString();
        description = in.readString();
        img = in.readInt();
        if (in.readByte() == 0) {
            evaluate = null;
        } else {
            evaluate = in.readFloat();
        }
        byte tmpIFavorites = in.readByte();
        iFavorites = tmpIFavorites == 0 ? null : tmpIFavorites == 1;
    }

    public static final Creator<CookingNote> CREATOR = new Creator<CookingNote>() {
        @Override
        public CookingNote createFromParcel(Parcel in) {
            return new CookingNote(in);
        }

        @Override
        public CookingNote[] newArray(int size) {
            return new CookingNote[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() { return description; }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public Float getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(Float evaluate) {
        this.evaluate = evaluate;
    }

    public Boolean getiFavorites() {
        return iFavorites;
    }

    public void setiFavorites(Boolean iFavorites) {
        this.iFavorites = iFavorites;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(description);
        dest.writeInt(img);
        if (evaluate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(evaluate);
        }
        dest.writeByte((byte) (iFavorites == null ? 0 : iFavorites ? 1 : 2));
    }
}
