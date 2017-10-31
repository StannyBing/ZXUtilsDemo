package com.zx.zxutils.other.ZXRecyclerAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zx.zxutils.R;
import com.zx.zxutils.views.SwipeRecylerView.LoadMoreListener;

import java.util.List;

/**
 * Created by Xiangb on 2016/9/21.
 * 功能：封装的Recycler适配器。用于待FooterView的情况
 */
public abstract class ZXRecycleAdapter extends RecyclerView.Adapter<RvHolder> {
    private LoadMoreListener mLoadMoreListener;
    public FooterViewHolder footerViewHolder;
    //全局变量
    private static final int ITEM_TYPE_NORMAL = 1;
    private static final int ITEM_TYPE_FOOTER = 2;

    public boolean hasLoadMore = false;

    public int pageSize = 10;//每页数量
    private List<?> dataList;

    public List<?> getDataList() {
        return dataList;
    }

    public boolean isHasLoadMore() {
        return hasLoadMore;
    }

    public void setHasLoadMore(boolean hasLoadMore) {
        this.hasLoadMore = hasLoadMore;
    }

    public abstract List<?> onItemList();

    public abstract int onCreateViewLayoutID(int viewType);

    public abstract void onBindHolder(ZxRvHolder holder, Object itemEntity, int position);

    @Override
    public RvHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_foot_view, parent, false);
            return new FooterViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(onCreateViewLayoutID(viewType), null);
            RvHolder holder = new RvHolder(view);
            return holder;
        }
    }

    @Override
    public void onViewRecycled(RvHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(RvHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            footerViewHolder = (FooterViewHolder) holder;
        } else {
            dataList = onItemList();
            onBindHolder(holder.getViewHolder(), dataList.get(position), position);
        }
    }

    public void setOnLoadMoreListener(LoadMoreListener listener) {
        this.mLoadMoreListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasLoadMore && position + 1 == getItemCount()) {
            return ITEM_TYPE_FOOTER;
        } else {
            return ITEM_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        dataList = onItemList();
        int count = 0;
        if (dataList != null) {
            if (hasLoadMore) {
                count = dataList.size() + 1;
            } else {
                count = dataList.size();
            }
        }
        return count;
    }

    public class FooterViewHolder extends RvHolder {
        private TextView loadText;
        private ProgressBar loadProgress;

        public FooterViewHolder(View parent) {
            super(parent);
            loadText = (TextView) parent.findViewById(R.id.load_tv);
            loadProgress = (ProgressBar) parent.findViewById(R.id.load_progress);
            loadText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mLoadMoreListener != null) {
                        mLoadMoreListener.LoadMore();
                    }
                }
            });
        }

        public void doLoading() {
            loadProgress.setVisibility(View.VISIBLE);
            loadText.setText("正在加载中。。");
        }

        public void setStatus(String infoMsg) {
            loadText.setVisibility(View.VISIBLE);
            loadProgress.setVisibility(View.GONE);
            loadText.setText(infoMsg);
        }

        public void setStatus(int mPageNum, int mTotalNum) {
            loadText.setVisibility(View.VISIBLE);
            if (mTotalNum == 0) {
                loadProgress.setVisibility(View.GONE);
                loadText.setText("没有数据");
            } else if (mPageNum * pageSize < mTotalNum) {
                loadProgress.setVisibility(View.GONE);
                int pageTotal = 0;
                if (mTotalNum % pageSize == 0) {
                    pageTotal = mTotalNum / pageSize;
                } else {
                    pageTotal = mTotalNum / pageSize + 1;
                }
                loadText.setText("点击加载更多，第" + mPageNum + "页，共" + pageTotal + "页");
            } else if (mPageNum == 1 && mTotalNum < pageSize) {
                loadProgress.setVisibility(View.GONE);
                loadText.setVisibility(View.GONE);
            } else {
                loadProgress.setVisibility(View.GONE);
                loadText.setText("已加载完");
            }
        }
    }
}