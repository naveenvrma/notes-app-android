package com.naveen.notes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.naveen.notes.R;
import com.naveen.notes.listeners.INoteItemListener;
import com.naveen.notes.model.NoteModel;
import com.naveen.notes.ui.SwipeRecyclerView.RecyclerViewDragHolder;

import org.jetbrains.annotations.NotNull;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Locale;

public class NoteListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<NoteModel> noteModels = new ArrayList<>();
    private ArrayList<NoteModel> mSearchList = new ArrayList<>();
    private INoteItemListener mListener;

    public NoteListAdapter(Context context, ArrayList<NoteModel> list, INoteItemListener listener) {
        this.mContext = context;
        this.noteModels.addAll(list);
        this.mSearchList.addAll(noteModels);
        this.mListener = listener;
    }

    public void deleteNoteItem(NoteModel noteModel, int pos) {
        noteModels.remove(noteModel);
        mSearchList.remove(noteModel);
        notifyItemRemoved(pos);
    }

    public void clearAll() {
        noteModels.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // button bar shows when left swipe
        View buttonBar = LayoutInflater.from(mContext).inflate(R.layout.item_note_button_bar, parent, false);
        // news item content
        View itemBar = LayoutInflater.from(mContext).inflate(R.layout.item_note, parent, false);
        // return view holder
        return new ViewHolder(mContext, buttonBar, itemBar, RecyclerViewDragHolder.EDGE_RIGHT).getDragViewHolder();
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder myHolder = (ViewHolder) RecyclerViewDragHolder.getHolder(holder);
        NoteModel newsItemModel = noteModels.get(holder.getAdapterPosition());

        try {
            myHolder.tvTitle.setText(newsItemModel.getTitle());
            myHolder.tvTime.setText(newsItemModel.getTime());
            myHolder.tvInfo.setText(newsItemModel.getInfo());

            myHolder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.OnClickEditNoteItem(newsItemModel, position);
                }
            });

            myHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.OnClickDeleteNoteItem(newsItemModel, position);
                }
            });
            myHolder.noteItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.OnClickDetailNoteItem(newsItemModel, position);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (noteModels.size() > 0) return noteModels.size();
        return 0;
    }
    private static String removeAccent(String text) {
        String result = Normalizer.normalize(text, Normalizer.Form.NFD);
        return result.replaceAll("[^\\p{ASCII}]", "");
    }
    public void searchItems(CharSequence text) {
        text = removeAccent((String) text).toLowerCase(Locale.getDefault());

        noteModels.clear();
        if (text.length() == 0) {
            noteModels.addAll(mSearchList);
        } else {
            for (int i = 0; i < mSearchList.size(); i++) {
                String name = removeAccent(mSearchList.get(i).getTitle());
                if (name.toLowerCase(Locale.getDefault()).contains(text)) {
                    noteModels.add(mSearchList.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerViewDragHolder {

        private TextView tvTitle;
        private TextView tvTime;
        private TextView tvInfo;
        private TextView deleteBtn;
        private TextView editBtn;
        private LinearLayout noteItemView;

        public ViewHolder(Context context, View bgView, View topView) {
            super(context, bgView, topView);
        }
        public ViewHolder(Context context, View bgView, View topView, int mTrackingEdges) {
            super(context, bgView, topView, mTrackingEdges);
        }
        @Override
        public void initView(View itemView) {
            tvTitle = (TextView) itemView.findViewById(R.id.item_note_title);
            tvTime = (TextView) itemView.findViewById(R.id.item_note_time);
            tvInfo = (TextView) itemView.findViewById(R.id.item_note_info);
            deleteBtn = (TextView) itemView.findViewById(R.id.item_note_delete);
            editBtn = (TextView) itemView.findViewById(R.id.item_note_edit);
            noteItemView = (LinearLayout) itemView.findViewById(R.id.item_note_view);
        }
    }
}

