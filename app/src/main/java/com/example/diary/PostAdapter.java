package com.example.diary;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    List<Post> items;
    Context context;
    boolean deleteMode = false;

    public PostAdapter(Context context) {
        this.context=context;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        holder.setItem(items.get(position));
    }

    public void setItems(List<Post> items) {
        this.items=items;
        notifyDataSetChanged();
    }

    public void setDeleteMode(boolean deleteMode) {
        this.deleteMode = deleteMode;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public ArrayList<Post> getDeleteItems() {
        ArrayList<Post> deleteItems = new ArrayList<> ();
        for (Post post : items) {
            if (post.getSelected()) {
                deleteItems.add(post);
            }
        }
        return deleteItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, contents, date;
        CheckBox checkBox;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.titleTextView);
            contents=itemView.findViewById(R.id.contentsTextView);
            date=itemView.findViewById(R.id.dateTextView);
            checkBox=itemView.findViewById(R.id.checkBox);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getBindingAdapterPosition();
                    if (position!=RecyclerView.NO_POSITION) {
                        Intent intent= new Intent(context, PostActivity.class);
                        intent.putExtra("post", (Parcelable) items.get(position)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });


            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Log.e("PostAdapter", "onCheckChanged method");
                    int position=getBindingAdapterPosition();
                    if (checkBox.isChecked()) {
                        Log.e("PostAdapter", title.getText()+" : checked");
                        items.get(position).setSelected(true);
                    }
                    else {
                        Log.e("PostAdapter", title.getText()+" : unchecked");
                        items.get(position).setSelected(false);
                    }
                }
            });

        }
        public void setItem(Post item) {
            title.setText(item.getTitle());
            contents.setText(item.getContents());
            date.setText(item.getDate());
            if (deleteMode) checkBox.setVisibility(View.VISIBLE);
            else checkBox.setVisibility(View.GONE);
        }
    }
}
