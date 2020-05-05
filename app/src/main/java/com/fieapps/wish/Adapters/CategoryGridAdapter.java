package com.fieapps.wish.Adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.fieapps.wish.Interfaces.OnCategoryItemSelected;
import com.fieapps.wish.Models.Category;
import com.fieapps.wish.R;
import com.fieapps.wish.Utils.Constants;

import java.util.List;

public class CategoryGridAdapter extends BaseAdapter {

    private OnCategoryItemSelected listener;

    private List<Category> list;

    private List<Category> selectedList;

    public CategoryGridAdapter(OnCategoryItemSelected listener, List<Category> list, List<Category> selectedList) {
        this.listener = listener;
        this.list = list;
        this.selectedList = selectedList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category_select, viewGroup, false);
            holder = new ViewHolder();
            holder.imageView = view.findViewById(R.id.image_view);
            holder.textView = view.findViewById(R.id.text_view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Uri uri = Uri.parse(Constants.BASE_URL + list.get(i).getImage());
        Glide.with(viewGroup.getContext()).load(uri).into(holder.imageView);
        holder.textView.setText(list.get(i).getName());
        view.setBackground(ContextCompat.getDrawable(viewGroup.getContext(),
                selectedList.contains(list.get(i)) ? R.drawable.selected_category_item : R.drawable.category_item));
        view.setOnClickListener(view1 -> listener.onCategoryItemSelected(list.get(i)));
        return view;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
