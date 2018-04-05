package com.example.student.spicamera;

/**
 * Created by Student on 4/4/2018.
 */

public class RowItem {
    private String imageUrl;
    private String title;
    private String desc;
    private String imageName;

    public RowItem(String imageUrl, String title, String desc, String imageName) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.desc = desc;
        this.imageName = imageName;
    }
    public RowItem(String imageName) {
        this.imageUrl = "";
        this.title = "";
        this.desc = "";
        this.imageName = imageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getTitle() {
        return title;
    }
    public String getImageName() {
        return this.imageName;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String toString() {
        return title + "\n" + desc;
    }
    @Override
    public boolean equals(Object o) {
        // self check
        if (this == o)
            return true;
        // null check
        if (o == null)
            return false;
        // type check and cast
        if (getClass() != o.getClass())
            return false;
        RowItem r = (RowItem) o;
        // field comparison
        return r.getImageName().equals(this.getImageName());

    }
}