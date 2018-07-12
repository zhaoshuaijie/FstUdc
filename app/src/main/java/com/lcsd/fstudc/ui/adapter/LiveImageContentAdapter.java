package com.lcsd.fstudc.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lcsd.fstudc.R;
import com.lcsd.fstudc.ninegridview.ImageInfo;
import com.lcsd.fstudc.ninegridview.NineGridView;
import com.lcsd.fstudc.ninegridview.preview.NineGridViewClickAdapter;
import com.lcsd.fstudc.view.CircleImageView;
import com.lcsd.fstudc.view.ExpandableTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jie on 2018/5/30.
 */
public class LiveImageContentAdapter extends BaseListAdapter {
    private Context mContext;

    public LiveImageContentAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_ninegridview, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ArrayList<ImageInfo> imageInfos = new ArrayList<>();
        ImageInfo imageInfo=new ImageInfo();
        imageInfo.setBigImageUrl("https://raw.githubusercontent.com/yipianfengye/android-adDialog/master/images/testImage1.png");
        imageInfos.add(imageInfo);
        holder.nineGrid.setAdapter(new NineGridViewClickAdapter(mContext, imageInfos));
        holder.content.setText("嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻详详细细嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻详详细细嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻详详细细嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻详详细细嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻详详细细");
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.tv_content)
        ExpandableTextView content;
        //九宫格图片展示控件
        @BindView(R.id.nineGrid)
        NineGridView nineGrid;
        @BindView(R.id.tv_name)
        TextView name;
        @BindView(R.id.tv_time)
        TextView time;
        //头像
        @BindView(R.id.civ_head)
        CircleImageView avatar;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }
}
