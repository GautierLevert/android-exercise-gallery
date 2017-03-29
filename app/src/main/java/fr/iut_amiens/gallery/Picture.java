package fr.iut_amiens.gallery;

import android.net.Uri;

import java.io.File;

public class Picture {

    private String title;

    private File content;

    public Picture(String title, File content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public File getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Picture)) return false;

        Picture picture = (Picture) o;

        if (title != null ? !title.equals(picture.title) : picture.title != null) return false;
        return !(content != null ? !content.equals(picture.content) : picture.content != null);

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Picture{" +
                "title='" + title + '\'' +
                ", content=" + content +
                '}';
    }
}
