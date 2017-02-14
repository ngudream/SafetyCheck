package com.safety.free.data;

public class BookElement extends Element {

    private String ISBN;// 书本标识
    private String picture;

    public BookElement() {
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String iSBN) {
        ISBN = iSBN;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
