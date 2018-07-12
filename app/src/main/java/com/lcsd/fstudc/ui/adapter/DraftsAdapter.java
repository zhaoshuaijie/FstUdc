package com.lcsd.fstudc.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lcsd.fstudc.R;
import com.lcsd.fstudc.sql.ImageLive;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jie on 2018/5/30.
 */
public class DraftsAdapter extends BaseListAdapter<ImageLive> {
    private Context mContext;

    public DraftsAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_drafts, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_name.setText(mList.get(position).getTitle());
        holder.tv_time.setText("最后修改时间：" + mList.get(position).getTime());
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.tv_title)
        TextView tv_name;
        @BindView(R.id.tv_time)
        TextView tv_time;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }
}
