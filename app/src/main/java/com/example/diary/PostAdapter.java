package com.example.diary;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    List<Post> items;
    Context context;

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

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, contents, date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.titleTextView);
            contents=itemView.findViewById(R.id.contentsTextView);
            date=itemView.findViewById(R.id.dateTextView);

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
        }
        public void setItem(Post item) {
            title.setText(item.getTitle());
            contents.setText(item.getContents());
            date.setText(item.getDate());
        }
    }
}
