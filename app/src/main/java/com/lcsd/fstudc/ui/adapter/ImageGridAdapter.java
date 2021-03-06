package com.lcsd.fstudc.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.ui.moudle.ImageInfo;

import java.io.File;
import java.util.ArrayList;


public class ImageGridAdapter extends BaseListAdapter<ImageInfo> {

    private int maxSize = 9;

    public ImageGridAdapter(Context context, int maxSize) {
        super(context);
        this.mContext = context;
        this.maxSize = maxSize;
    }

    //获取源图片
    public ArrayList<String> getSourceList() {
        ArrayList<String> ret = new ArrayList<String>();
        for (ImageInfo info : mList) {
            if (!info.isAddButton) {
                ret.add(info.getSource_image());
            }
        }
        return ret;
    }

    //添加一个项
    public void addItem(ImageInfo info) {
        mList.add(mList.size() - 1, info);
        if (mList.size() > maxSize) {
            mList.remove(maxSize);
        }
        notifyDataSetChanged();
    }

    //删除一个项
    public void deleteItem(int position) {
        mList.remove(position);
        boolean hasAddButton = false;
        for (ImageInfo info : mList) {
            if (info.isAddButton()) {
                hasAddButton = true;
            }
        }
        if (!hasAddButton) {
            ImageInfo info = new ImageInfo();
            info.setAddButton(true);
            mList.add(info);
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grid_img, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.iv_gridview_imageview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ImageInfo info = mList.get(position);
        if (info != null) {
            if (!info.isAddButton()) {
                File imageFile = new File(info.getSource_image());
                Glide.with(mContext)
                        .load(imageFile)
                        .placeholder(R.drawable.camerasdk_pic_loading)
                        .error(R.drawable.camerasdk_pic_loading)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .override(270, 270)
                        .centerCrop()
                        .into(holder.image);
            } else {
                Glide.with(mContext)
                        .load(R.drawable.ic_add_image_button)
                        .override(270, 270)
                        .centerCrop()
                        .into(holder.image);
            }
        }
        return convertView;
    }

    class ViewHolder {
        public ImageView image;
    }
}

	
	
