package fr.iut_amiens.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PictureAdapter extends BaseAdapter {

    private ArrayList<Picture> images = new ArrayList<>();

    private LayoutInflater layoutInflater;

    public PictureAdapter(Context context) {
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(Picture image) {
        images.add(image);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Picture getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("ADAPTER", "getView(" + position + ")");

        View view;
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.listitem_image, parent, false);
        } else {
            view = convertView;
        }

        Picture image = getItem(position);

        TextView titleView = (TextView) view.findViewById(R.id.imageTitle);
        titleView.setText(image.getTitle());

        ImageView imageView; /* your ImageView */
        File pictureFile; /* your picture file */

        pictureFile = image.getContent();
        imageView = (ImageView) view.findViewById(R.id.imageView);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath(), options);
        imageView.setImageBitmap(bitmap);

        Log.d("ADAPTER", "image content " + image.getContent());

        return view;
    }

    public void clear() {
        images.clear();
        notifyDataSetChanged();
    }

    public List<Picture> getAll() {
        return images;
    }

    public void addAll(Collection<Picture> collection) {
        images.addAll(collection);
    }
}
