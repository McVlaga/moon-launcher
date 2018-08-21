package io.github.mcvlaga.moonlauncher.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import io.github.mcvlaga.moonlauncher.home.SearchViewHolder;

import static android.support.v7.widget.helper.ItemTouchHelper.DOWN;
import static android.support.v7.widget.helper.ItemTouchHelper.UP;

public class DragAndDropHelper extends ItemTouchHelper.Callback {

    private ActionCompletionContract contract;

    private boolean draggable = true;

    private boolean isElevated = false;

    public DragAndDropHelper(ActionCompletionContract contract) {
        this.contract = contract;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = UP | DOWN;
        if (!draggable || viewHolder instanceof SearchViewHolder) {
            dragFlags = 0;
        }
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        contract.onViewMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void onChildDraw(Canvas c,
                            RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            float dX,
                            float dY,
                            int actionState,
                            boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        if (Build.VERSION.SDK_INT >= 21 && isCurrentlyActive && !isElevated) {
            final float newElevation = 10f + ViewCompat.getElevation(viewHolder.itemView);
            ObjectAnimator elevateUp = ObjectAnimator.ofFloat((CardView)viewHolder.itemView, "cardElevation", newElevation);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 1.01f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 1.01f);
            AnimatorSet elevate = new AnimatorSet();
            elevate.playTogether(scaleDownX, scaleDownY, elevateUp);
            elevate.setDuration(200);
            elevate.start();

            isElevated = true;
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final float newElevation = ViewCompat.getElevation(viewHolder.itemView) - 10f;
        ObjectAnimator elevateDown = ObjectAnimator.ofFloat((CardView)viewHolder.itemView, "cardElevation", newElevation);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 1.0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 1.0f);
        AnimatorSet elevate = new AnimatorSet();
        elevate.playTogether(scaleDownX, scaleDownY, elevateDown);
        elevate.setDuration(200);
        elevate.start();

        isElevated = false;
    }

    public void setDragable(boolean draggable) {
        this.draggable = draggable;
    }

    public interface ActionCompletionContract {
        void onViewMoved(int oldPosition, int newPosition);
    }
}