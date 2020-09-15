package cn.zerokirby.note.noteData;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.zerokirby.note.activity.EditingActivity;
import cn.zerokirby.note.activity.MainActivity;
import cn.zerokirby.note.R;
import cn.zerokirby.note.db.DatabaseHelper;

import static cn.zerokirby.note.MyApplication.getContext;


public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private MainActivity mainActivity;
    private List<NoteItem> mNoteItemList;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    //构造器
    public NoteAdapter(MainActivity mainActivity, List<NoteItem> noteItemList) {
        this.mainActivity = mainActivity;
        mNoteItemList = noteItemList;
    }

    //获取item数量
    @Override
    public int getItemCount() {
        return mNoteItemList.size();
    }

    //获取DataItem的数据
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NoteItem noteItem = mNoteItemList.get(position);
        holder.title.setText(noteItem.getTitle());//设置标题
        holder.body.setText(noteItem.getBody());//设置内容
        holder.date.setText(noteItem.getYear() + noteItem.getMonth() + noteItem.getDay() +
                "\n" + noteItem.getTime());//设置标准化日期时间
    }

    //为recyclerView的每一个item设置点击事件
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.data_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        //笔记的点击事件
        holder.dataView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //传送数据的id并启动EditingActivity
                int position = holder.getBindingAdapterPosition();
                NoteItem noteItem = mNoteItemList.get(position);
                int id = noteItem.getId();
                Intent intent = new Intent(view.getContext(), EditingActivity.class);
                intent.putExtra("noteId", id);
                view.getContext().startActivity(intent);
            }
        });

        //笔记的长按事件
        holder.dataView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int position = holder.getBindingAdapterPosition();
                NoteItem noteItem = mNoteItemList.get(position);
                int id = noteItem.getId();

                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);//显示删除提示
                builder.setTitle("提示");
                builder.setMessage("是否要删除“" + noteItem.getTitle() + "”？");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {//点击确定则执行删除操作
                        dbHelper = new DatabaseHelper("ProgressNote.db", null, 1);
                        db = dbHelper.getWritableDatabase();
                        db.delete("Note", "id = ?", new String[]{String.valueOf(id)});//查找对应id
                        Toast.makeText(getContext(), mainActivity.getString(R.string.deleteSuccess), Toast.LENGTH_SHORT).show();
                        db.close();

                        mainActivity.modifySync();
                        mainActivity.deleteNoteById(id);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {//什么也不做
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();

                return true;
            }
        });

        return holder;
    }

    //设置item中的View
    static class ViewHolder extends RecyclerView.ViewHolder {
        View dataView;
        TextView title;
        TextView body;
        TextView date;

        ViewHolder(View view) {
            super(view);
            dataView = view;
            title = view.findViewById(R.id.title);
            body = view.findViewById(R.id.body);
            date = view.findViewById(R.id.date);
        }
    }

}