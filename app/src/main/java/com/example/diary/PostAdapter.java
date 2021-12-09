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
        for (Post post : items) {       //모든 게시글 리스트 점검
            if (post.getSelected()) {   //체크된 게시글이면
                deleteItems.add(post);  //삭제할 게시글 리스트에 추가
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

            //게시글 클릭 시 이벤트
            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getBindingAdapterPosition();   //어댑터 내 아이템의 위치
                    if (position!=RecyclerView.NO_POSITION) {   //아이템을 클릭한 것인지 확인
                        Intent intent= new Intent(context, PostActivity.class); //액티비티 전환을 위한 인텐트 객체 생성
                        intent.putExtra("post", (Parcelable) items.get(position)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //PostActivity에 게시글 정보 전달
                        context.startActivity(intent);  //액티비티 전환
                    }
                }
            });


            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Log.e("PostAdapter", "onCheckChanged method");
                    int position=getBindingAdapterPosition();   //어댑터 내 아이템의 위치
                    if (checkBox.isChecked()) { //체크되면 post 객체의 selected=true
                        Log.e("PostAdapter", title.getText()+" : checked");
                        items.get(position).setSelected(true);
                    }
                    else {  //체크가 해제되면 post 객체의 selected=false
                        Log.e("PostAdapter", title.getText()+" : unchecked");
                        items.get(position).setSelected(false);
                    }
                }
            });

        }
        public void setItem(Post item) {
            title.setText(item.getTitle());
            contents.setText(item.getContents());
            date.setText(item.getYear() + "." + (item.getMonth() + 1) + "." + item.getDay());
            if (deleteMode) checkBox.setVisibility(View.VISIBLE);   //삭제하기 모드면 체크박스 보이게
            else {
                checkBox.setVisibility(View.GONE);
                checkBox.setChecked(false); //삭제하기 모드에서 나올 때 체크박스를 모두 해제해야 다음 삭제하기 모드 실행 시 모두 체크 해제되어있음
            }
        }
    }
}
