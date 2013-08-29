package com.visiors.minuetta.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;

import com.visiors.minuetta.view.PropertyView.GraphObject;


public class PropertyView extends TableView<GraphObject>{

	private final ObservableList<GraphObject> data = FXCollections.observableArrayList(new GraphObject("Name",
			"Rectangle"), new GraphObject("X", "100"), new GraphObject("Y", "200"), new GraphObject("Style", "Simple"),
			new GraphObject("Presentation", "Default"));


	public PropertyView() {


		setEditable(true);

		TableColumn propertyCol = new TableColumn("Property");
		propertyCol.setMinWidth(40);
		propertyCol.setCellValueFactory(new PropertyValueFactory<GraphObject, String>("Property"));
		propertyCol.setCellFactory(TextFieldTableCell.forTableColumn());
		propertyCol.setOnEditCommit(new EventHandler<CellEditEvent<GraphObject, String>>() {

			@Override
			public void handle(CellEditEvent<GraphObject, String> t) {

				t.getTableView().getItems().get(t.getTablePosition().getRow()).setProperty(t.getNewValue());
			}
		});

		TableColumn valueCol = new TableColumn("Value");
		valueCol.setMinWidth(40);
		valueCol.setCellValueFactory(new PropertyValueFactory<GraphObject, String>("Value"));
		valueCol.setCellFactory(TextFieldTableCell.forTableColumn());
		valueCol.setOnEditCommit(new EventHandler<CellEditEvent<GraphObject, String>>() {

			@Override
			public void handle(CellEditEvent<GraphObject, String> t) {

				t.getTableView().getItems().get(t.getTablePosition().getRow()).setValue(t.getNewValue());
			}
		});

		setItems(data);
		getColumns().addAll(propertyCol, valueCol);

		final TextField property = new TextField();
		property.setPromptText("Property");
		property.setMaxWidth(propertyCol.getPrefWidth());
		final TextField value = new TextField();
		value.setMaxWidth(valueCol.getPrefWidth());
		value.setPromptText("Value");


		HBox hb = new HBox();
		hb.getChildren().addAll(property, value);
		hb.setSpacing(3);
	}




	public static class GraphObject {

		private final SimpleStringProperty property;
		private final SimpleStringProperty value;

		private GraphObject(String property, String value) {

			this.property = new SimpleStringProperty(property);
			this.value = new SimpleStringProperty(value);
		}

		public String getProperty() {

			return property.get();
		}

		public void setProperty(String fName) {

			property.set(fName);
		}

		public String getValue() {

			return value.get();
		}

		public void setValue(String fName) {

			value.set(fName);
		}
	}
}
