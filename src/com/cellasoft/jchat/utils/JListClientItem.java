package com.cellasoft.jchat.utils;

public class JListClientItem {

    private String name, comment, imageFile;

    public JListClientItem(String name, String comment, String imageFile) {
        this.name = name;
        this.comment = comment;
        this.imageFile = imageFile;
    }

    /** String representation used in printouts and in JLists */
    @Override
    public String toString() {
        return name;
    }

    /** Return country containing city or province named "Java". */
    public String getName() {
        return name;
    }

    /** Specify country containing city or province named "Java". */
    public void setName(String name) {
        this.name = name;
    }

    /** Return comment about city or province named "Java".
     *  Usually of the form "near such and such a city".
     */
    public String getComment() {
        return comment;
    }

    /** Specify comment about city or province named "Java". */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /** Return path to image file of country flag. */
    public String getImageFile() {
        return imageFile;
    }

    /** Specify path to image file of country flag. */
    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }
}
