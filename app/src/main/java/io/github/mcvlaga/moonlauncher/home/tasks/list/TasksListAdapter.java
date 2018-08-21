package io.github.mcvlaga.moonlauncher.home.tasks.list;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.widget.CompoundButtonCompat;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.mcvlaga.moonlauncher.R;
import io.github.mcvlaga.moonlauncher.home.tasks.data.Task;
import io.github.mcvlaga.moonlauncher.home.tasks.utils.TasksUtils;

public class TasksListAdapter extends ArrayAdapter<Task> {

    private static class ViewHolder {
        CheckBox checkBox;
        TextView title;
        TextView date;
    }

    private final TasksListFragment.TasksListener mTasksListener;

    private Context mContext;

    private List<Task> tasksItems = new ArrayList<>();
    private ListView listView;

    TasksListAdapter(ListView listview, ArrayList<Task> tasksData, Context context,
                 TasksListFragment.TasksListener listener) {
        super(context, R.layout.task_item, tasksData);
        mContext = context;
        mTasksListener = listener;
        listView = listview;
        tasksItems = tasksData;
    }

    public void setData(ArrayList<Task> tasksData) {
        tasksItems.clear();
        tasksItems.addAll(tasksData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        // Get the data item for this position
        final Task task = tasksItems.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.task_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.checkBox = convertView.findViewById(R.id.task_checkbox);
            viewHolder.title = convertView.findViewById(R.id.task_title);
            viewHolder.date = convertView.findViewById(R.id.task_due_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String dateString = TasksUtils.getFriendlyDateStringForWidget(mContext,
                task.getDate(), task.getTime());
        viewHolder.date.setText(dateString);
        viewHolder.checkBox.setOnCheckedChangeListener(null);
        viewHolder.checkBox.setChecked(task.isCompleted());
        viewHolder.checkBox.jumpDrawablesToCurrentState();
        viewHolder.checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTasksListener.onCompleteTaskClick(task);
                } else {
                    mTasksListener.onActivateTaskClick(task);
                }
                viewHolder.checkBox.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }
        });
        viewHolder.title.setText(task.getTitle());
        viewHolder.title.setTextColor(task.getColor());
        if (task.isCompleted()) {
            viewHolder.title.setPaintFlags(viewHolder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            viewHolder.title.setPaintFlags(viewHolder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        int states[][] = {{android.R.attr.state_checked}, {}};
        int colors[] = {task.getColor(), task.getColor()};
        CompoundButtonCompat.setButtonTintList(viewHolder.checkBox, new ColorStateList(states, colors));
        // Return the completed view to render on screen
        return convertView;
    }
}
