package com.bigdrum.metronomemate.ui.setlist;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigdrum.metronomemate.R;

public class CustomAdapter extends ArrayAdapter<Model> implements OnCheckedChangeListener, OnClickListener {

	private boolean[] checked;
	private Set<View> views;
	private final Activity context;
	private MetronomeFragment ui;
	private int numberOfSelectedItems;
	private int selectedPosition = -1;
    private View parentView;
    private Model selectedItem;

	public CustomAdapter(Activity context, int layoutResource, int textviewResource, List<Model> modelList, MetronomeFragment ui) {
		super(context, layoutResource, textviewResource, modelList);
		this.context = context;
		this.ui = ui;
		this.numberOfSelectedItems = 0;
		this.checked = new boolean[modelList.size()];
		this.views = new HashSet<View>();
//        this.parentView = ui.getView();
	}

    enum ViewType { SETLISTNAME, SONGNAME, SUBSETMARKER };

	static class ViewHolder {
		protected TextView itemNumber;
		protected ImageView imageView;
		protected TextView text;
		protected CheckBox checkbox;
		protected boolean clicked;
        protected ViewType viewType;
        protected boolean isEditable;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View view = null;
		boolean editMode = ui.isEditMode();

		if (!editMode) {
			return createReadOnlyView(position, convertView);
		}

		if (convertView == null || convertView.findViewById(R.id.setlist_row_handler) == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.list_item_handle_left, null);
			
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.itemNumber = (TextView)view.findViewById(R.id.itemNumber);
			viewHolder.imageView = (ImageView)view.findViewById(R.id.setlist_row_handler);
			viewHolder.text = (TextView) view.findViewById(R.id.setlist_row_name);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.checked_item_row_checkbox);
			viewHolder.checkbox.setOnCheckedChangeListener(this);
			viewHolder.checkbox.setOnClickListener(this);

			view.setTag(viewHolder);
			
			viewHolder.checkbox.setTag(getItem(position));
		} 
		
		else {
			view = convertView;
			view.setBackgroundColor(Color.TRANSPARENT);
			((ViewHolder) view.getTag()).checkbox.setTag(getItem(position));
		}
		
		views.add(view);
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.itemNumber.setText(getItemNumber(position));
		holder.text.setText(getItem(position).getName());
		String song = getItem(position).getName();
		holder.checkbox.setChecked(checked[position]);
		holder.imageView.setVisibility(ui.isEditMode() ? View.VISIBLE :View.INVISIBLE);
		holder.checkbox.setVisibility(ui.isEditMode() ? View.VISIBLE :View.INVISIBLE);
		
		if (position == selectedPosition) {
			view.setBackgroundColor(Color.GREEN);
		}
		
		return view;
	}


	/**
	 *
	 * @return
	 */
	private View createReadOnlyView(final int position, View convertView) {

		View view = null;
		Model listItem = getItem(position);
        ViewHolder currentViewHolder = null;
        LayoutInflater inflator = context.getLayoutInflater();

        if (convertView != null) {
            currentViewHolder = (ViewHolder)convertView.getTag();
        }

		if (convertView == null || currentViewHolder.isEditable) {

			view = inflator.inflate(R.layout.list_item_not_editable, null);

			final ViewHolder viewHolder = new ViewHolder();

            initialiseViewHolder(viewHolder, ViewType.SONGNAME, view, position);
		}

        else if (convertView != null && listItem.isSubsetItem()) {

            view = inflator.inflate(R.layout.list_item_subset_marker_readonly, null);

            ViewHolder viewHolder = (ViewHolder)convertView.getTag();

            initialiseViewHolder(viewHolder, ViewType.SUBSETMARKER, view, position);
        }

        else if (convertView != null && !listItem.isSubsetItem()) {
            view = inflator.inflate(R.layout.list_item_not_editable, null);

            ViewHolder viewHolder = (ViewHolder)convertView.getTag();

            initialiseViewHolder(viewHolder, ViewType.SONGNAME, view, position);
        }

		else {
			view = convertView;
			view.setBackgroundColor(Color.TRANSPARENT);
			((ViewHolder) view.getTag()).itemNumber.setTag(getItem(position));
		}

		views.add(view);

		if (position == selectedPosition) {
			view.setBackgroundColor(Color.GREEN);
		}

		return view;
	}


    /**
     *
     * @param viewHolder
     * @param viewType
     */
    private void initialiseViewHolder(ViewHolder viewHolder, ViewType viewType,
                                      View view, int position) {

        viewHolder.itemNumber = (TextView)view.findViewById(R.id.itemNumber);
        viewHolder.text = (TextView) view.findViewById(R.id.setlist_row_name);
        viewHolder.text.setText(getItem(position).getName());
        viewHolder.viewType = viewType;
        viewHolder.isEditable = false;

        if (viewHolder.itemNumber != null) {
            viewHolder.itemNumber.setText(getItemNumber(position));
            viewHolder.itemNumber.setTag(getItem(position));
        }

        view.setTag(viewHolder);
    }

	
	/**
	 * 
	 * @return
	 */
	private String getItemNumber(int position) {
		
		if (getItem(position).getArtist() == null) {
			return String.valueOf(position + 1) + ":";
		}
		
		if (getItem(position).isSubsetItem()) {
			return "";
		}
		
		int displayedPosition = 0;
		for (int i=0; i<=position; i++) {
			if (! getItem(i).isSubsetItem()) {
				displayedPosition++;
			}
		}
		
		return String.valueOf(displayedPosition) + ":";
	}

	
	/**
	 * 
	 * @param i
	 */
	public void setSelectedItems(int i) {
		this.numberOfSelectedItems = i;
	}
	
	public void setSelectedPosition(int pos) {
		this.selectedPosition = pos;
	}


	/**
	 * 
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
		Model element = (Model) buttonView.getTag();
		element.setSelected(buttonView.isChecked());

		getItem(element.getPosition()).setSelected(isChecked);
		checked[element.getPosition()] = isChecked;
		
	}
	
	
	 
	/**
	 *
	 */
    public void notifyDataSetChangedAndReset() {
    	notifyDataSetChanged();
    	
    	this.checked = new boolean[getCount()];
    	this.views.clear();
    }


    /**
     * 
     */
	@Override
	public void onClick(View buttonView) {
		
		boolean isChecked = ((CheckBox)buttonView).isChecked();
		if (isChecked) {
			numberOfSelectedItems++;
		}
		else {
			numberOfSelectedItems--;
			if (numberOfSelectedItems < 0) {
				numberOfSelectedItems = 0;
			}
		}
	
		ui.setEditItemActions(numberOfSelectedItems);
	}


	/**
	 * Sets the checked status of a list item
	 * @param position
	 */
	public void setItemCheckedAt(int position, boolean state) {

		checked[position] = state;

	}


	/**
	 *
	 */
	public void clearAllCheckedItems() {
		for (int index=0; index < checked.length; index++) {
			checked[index] = false;
		}
	}


    public Model getSelectedItem() {

        for (int index=0; index < checked.length; index++) {
            if (checked[index] == true) {
                return getItem(index);
            }
        }

        return null;
    }
}
