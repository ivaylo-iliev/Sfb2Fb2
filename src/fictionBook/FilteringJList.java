package fictionBook;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.util.*;

/**
 * A Filtering JList component. The component is based on a tutorial, found
 * <a href="http://java.sun.com/developer/JDCTechTips/2005/tt1214.html">here</a>.
 * <p>It uses Set in the FilteringModel component for ensuring unique values in the list.</p>
 *
 * @author Ivaylo Iliev
 * @see JList
 * @see JTextField
 * @see ArrayList
 *
 */
public class FilteringJList extends JList {

	private JTextField input;

	/**
	 * Constructor for the FilteringJList
	 */
	public FilteringJList() {
		FilteringModel model = new FilteringModel();
		setModel(new FilteringModel());
	}

	/**
	 * Associates filtering document listener to text
	 * component.
	 *
	 * @param input The input JTextField for the filter
	 */
	public void installJTextField(JTextField input) {
		if (input != null) {
			this.input = input;
			FilteringModel model = (FilteringModel) getModel();
			input.getDocument().addDocumentListener(model);
		}
	}

	/**
	 * Disassociates filtering document listener from text
	 * component.
	 *
	 * @param input The input JTextField for the filter
	 */
	public void uninstallJTextField(JTextField input) {
		if (input != null) {
			FilteringModel model = (FilteringModel) getModel();
			input.getDocument().removeDocumentListener(model);
			this.input = null;
		}
	}

	/**
	 * Doesn't let model change to non-filtering variety
	 */
	@Override
	public void setModel(ListModel model) {
		if (!(model instanceof FilteringModel)) {
			throw new IllegalArgumentException();
		} else {
			super.setModel(model);
		}
	}

	/**
	 * Adds item to model of list
	 *
	 * @param element The element to be added to the list model
	 */
	public void addElement(Object element) {
		((FilteringModel) getModel()).addElement(element);
	}

	/**
	 * Sorts the model, so the unsorted Set is converted to sorted ArrayList
	 */
	public void sort() {
		((FilteringModel) getModel()).sort();
	}

	public Object getElementAt(int Index){
		return ((FilteringModel) getModel()).getElementAt(Index);
	}

	/**
	 * Manages filtering of list model
	 */
	private class FilteringModel extends AbstractListModel
			implements DocumentListener {

		List<Object> list;
		List<Object> filteredList;
		String lastFilter = "";

		/**
		 * Constructor for the FilteringModel
		 */
		public FilteringModel() {
			list = new ArrayList<Object>();
			filteredList = new ArrayList<Object>();
		}

		/**
		 * Adds item to model of list
		 *
		 * @param element The element to be added to the list model
		 */
		public void addElement(Object element) {
			list.add(element);
			filter(lastFilter);
		}

		/**
		 * Sort the element list
		 */
		public void sort(){
			ArrayList tmpList = new ArrayList<String>();
			tmpList.addAll(list);
			
			Collections.sort(tmpList);

			list.clear();
			list.addAll(tmpList);
			
			filter(lastFilter);
		}

		/**
		 * Returns the size of the list
		 *
		 * @return The size of the list
		 */
		@Override
		public int getSize() {
			return filteredList.size();
		}

		/**
		 * Returns element at specific index in the list
		 *
		 * @return The element
		 */
		@Override
		public Object getElementAt(int index) {
			Object returnValue;
			if (index < filteredList.size()) {
				returnValue = filteredList.get(index);
			} else {
				returnValue = null;
			}
			return returnValue;
		}

		/**
		 * Filter the list
		 *
		 * @param search Filter string
		 */
		void filter(String search) {
			filteredList.clear();
			for (Object element : list) {
				if (element.toString().indexOf(search, 0) != -1) {
					filteredList.add(element);
				}
			}
			fireContentsChanged(this, 0, getSize());
		}

		// DocumentListener Methods
		@Override
		public void insertUpdate(DocumentEvent event) {
			Document doc = event.getDocument();
			try {
				lastFilter = doc.getText(0, doc.getLength());
				filter(lastFilter);
			} catch (BadLocationException ble) {
				System.err.println("Bad location: " + ble);
			}
		}

		@Override
		public void removeUpdate(DocumentEvent event) {
			Document doc = event.getDocument();
			try {
				lastFilter = doc.getText(0, doc.getLength());
				filter(lastFilter);
			} catch (BadLocationException ble) {
				System.err.println("Bad location: " + ble);
			}
		}

		@Override
		public void changedUpdate(DocumentEvent event) {
		}
	}
} 