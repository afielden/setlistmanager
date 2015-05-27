package com.bigdrum.metronomemate.export;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bigdrum.metronomemate.R;

public class CustomAdapter extends ArrayAdapter<Contact> {

	private boolean[] checkBoxState;
	private final List<Contact> list;
	private final Activity context;
	private SelectRecipients ui;
	private int numberOfSelectedItems;

	public CustomAdapter(Activity context, int layoutResource, int textviewResource, List<Contact> ContactList, SelectRecipients ui) {
		super(context, layoutResource, textviewResource, ContactList);
		this.context = context;
		this.list = ContactList;
		this.ui = ui;
		this.numberOfSelectedItems = 0;
	}

	static class ViewHolder {
		protected TextView text;
		protected CheckBox checkbox;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.checked_item_row_layout, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) view.findViewById(R.id.checked_item_row_edittext);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.checked_item_row_checkbox);
			viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							Contact element = (Contact) viewHolder.checkbox.getTag();
							element.setSelected(buttonView.isChecked());
							if (isChecked) {
								numberOfSelectedItems++;
							}
							else {
								numberOfSelectedItems--;
								if (numberOfSelectedItems < 0) {
									numberOfSelectedItems = 0;
								}
							}
							list.get(position).setSelected(isChecked);
						}
					});
			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(list.get(position));
		} 
		
		else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
		}
		
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.text.setText(list.get(position).toString());
		holder.checkbox.setChecked(list.get(position).isSelected());
		return view;
	}

	
	/**
	 * 
	 * @param i
	 */
	public void setSelectedItems(int i) {
		this.numberOfSelectedItems = i;
	}


	public int getNumberOfSelectedItems() {
		return numberOfSelectedItems;
	}
	
	
}
