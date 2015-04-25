package control;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import javax.print.attribute.standard.MediaSize.Other;

import org.controlsfx.dialog.Dialogs;
import org.omg.CORBA.PUBLIC_MEMBER;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import control.model.Contact;
import control.model.Country;
import control.model.Graphique;
import control.model.Group;
import control.model.Workspace;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

public class controleur {	
	@FXML TreeView<Object> tree;
	@FXML GridPane contactPane;
	@FXML TextField nom,prenom,ville,voie,cp;
	@FXML ChoiceBox<String> pays;
	@FXML DatePicker date;
	@FXML RadioButton sex_f,sex_m;
	@FXML ToggleGroup sex_Group;
	@FXML PieChart pie;
	@FXML BarChart<String, Number> barcity;
	@FXML BarChart bardate;
	@FXML TabPane tab;
	@FXML Tab tabCon,tabGrap;
	private final Workspace workspace;
	private Contact editingContact;
	private TreeItem<Object> currentGroup;
	private TreeItem<Object> currentContact;
	private Contact originaleContact;
	private HashMap<String, Control> map_valid;
	private TreeItem<Object> selectedItem;
	private final Image groupIcon = 
			new Image("file:///Users/YU/Documents/workspace/nf28_td04/src/control/view/images/group.png");
	private final Image contIcon =
			new Image("file:///Users/YU/Documents/workspace/nf28_td04/src/control/view/images/contact.png");
	ListChangeListener<Contact> contactListener;
	ListChangeListener<Group> groupListener;
	MapChangeListener<String, String> f;
	ListChangeListener<PieChart.Data> p;
	ListChangeListener<XYChart.Series<String, Number>> c;
	Graphique graphique;
	public controleur()
	{
		workspace= new Workspace();
		editingContact = new Contact();
		graphique = new Graphique();
	}
	
	public void initialize()
	{
		TreeItem<Object> root = new TreeItem<Object>("Fiche de Contacts");
		root.setExpanded(true);
		tree.setRoot(root);
		tree.setEditable(true);
		tree.setCellFactory(param -> new TextFieldTreeCellImpl());
		contactPane.visibleProperty().set(false);
		
		//TREE 	LISTENER
		tree.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			tab.getSelectionModel().select(tabCon);
			/*
			if (newValue.getValue() instanceof Contact)
			{
				//
				currentContact = newValue;
				//modify the content of this contact
				contactPane.setVisible(true);
				//copy one as an original Contact
				originaleContact = (Contact) (newValue.getValue());
				originaleContact.copyContact(editingContact);
			}
			else if (newValue.getValue() instanceof Group){
				//editingContact.init();
				//currentGroup = newValue;
				//contactPane.setVisible(false);
				//champStyleInit();
			}
			else 
			{
				//champStyleInit();
				contactPane.setVisible(false);
			}
			*/
			this.selectedItem = (TreeItem<Object>) newValue;
			if (this.selectedItem == null) {
				return;
			}
			contactPane.visibleProperty().set(false);
			originaleContact = null;
			if (this.selectedItem.getValue() instanceof Contact)
			{
				contactPane.visibleProperty().set(true);
				originaleContact = (Contact) this.selectedItem.getValue();
				originaleContact.copyContact(editingContact);
			}
		});
		
		//CONTACT LISTENER
		
		contactListener=change -> {
			change.next();
			TreeItem<Object> parent;
			if (selectedItem.getValue() instanceof Group)
				parent = selectedItem;
			else
				parent = selectedItem.getParent();
			if(change.wasAdded())
			{
				Contact c = change.getAddedSubList().get(0);
				TreeItem<Object> contItem = new TreeItem<Object>(c, new ImageView(contIcon));
				
				parent.getChildren().add(contItem);
				
				fixSelectedElements(contItem,c);
			}
			else if(change.wasRemoved())
			{
				parent.getChildren().remove(selectedItem);
				fixSelectedElements(parent, null);
			}
		};
		
		
		//GROUP LISTENER		
		groupListener=change -> {
			change.next();
			if(change.wasAdded())
			{
				Group group = change.getAddedSubList().get(0); //always 1 element 
				TreeItem<Object> grpItem = new TreeItem<Object>(group, new ImageView(groupIcon));
				root.getChildren().add(grpItem);	
				fixSelectedElements(grpItem, null);
				group.getContactList().addListener(contactListener);
			}
			else if (change.wasRemoved()) 
			{
				TreeItem<Object> parentItem = selectedItem.getParent();
				parentItem.getChildren().remove(selectedItem);
				fixSelectedElements(parentItem,null);
			}
		};	
		//PIE LISTENER
		p = change -> {
			change.next();
				pie.setData(graphique.pieChartDataProperty());
		};
		//MAP LISTENER POUR VALID
		f = change -> {
		   if (change.wasAdded()) {
			   map_valid.get(change.getKey()).setStyle("-fx-border-color: red ;");
		   } 
		   else if (change.wasRemoved()) {
				map_valid.get(change.getKey()).setStyle("-fx-border-color: green ;");
		   }
		}; 
	
		makeBindings();
		
		mapForValid();
	}
	private void fixSelectedElements(TreeItem<Object> item, Contact c) {
		selectedItem = item;
		tree.getSelectionModel().select(selectedItem);
		originaleContact = c;
	}
	public void mapForValid()
	{	
		map_valid = new HashMap<String,Control>();
		map_valid.put(editingContact.nomProperty().getName(),nom);
		map_valid.put(editingContact.prenomProperty().getName(),prenom);
		map_valid.put(editingContact.voieProperty().getName(),voie);
		map_valid.put(editingContact.code_postalProperty().getName(),cp);
		map_valid.put(editingContact.villeProperty().getName(),ville);
		map_valid.put(editingContact.payspProperty().getName(),pays);
		map_valid.put(editingContact.getDate().getName(),date);
		map_valid.put(editingContact.getSex().getName(),sex_f);     
	}
	public void makeBindings()
	{
		pays.getSelectionModel().selectedItemProperty().addListener(
				(ov, oldv, newv) -> {
					editingContact.payspProperty().setValue(newv);
				}
		);
		pays.setItems(FXCollections.observableArrayList(Country.listePays()));
		editingContact.payspProperty().addListener(
				(ov, oldv, newv) -> {
					try {
						if(newv.isEmpty())
						{
							pays.getSelectionModel().select(null);
						}
						else {
							pays.getSelectionModel().select(newv);
						}
						}catch(NullPointerException e){}				
				});
		date.valueProperty().addListener(
				(ov,oldv, newv)->
		{
			if(newv != null)
			editingContact.getDate().setValue(newv.toString());
		});
		
		editingContact.getDate().addListener(
				(obj, o, n) -> {
					if (n != null && !n.trim().equals("")) {
						DateTimeFormatter formatter = DateTimeFormatter
								.ofPattern("yyyy-MM-dd");
						LocalDate date2 = LocalDate.parse(n, formatter);
						date.setValue(date2);
					}
					else {
						date.setValue(null);
					}
				});
		
		sex_Group.selectedToggleProperty().addListener(
			(ov, oldv, newv) -> {
				if(newv!=null)
				{
					RadioButton bt = (RadioButton) newv;
					editingContact.getSex().setValue(bt.getText());
				}
				else 
				{
				}
		});
		editingContact.getSex().addListener((obj, o, n) -> {
			if (n == null) {
				return;
			}
			if (n.equals("F")) {
				sex_Group.selectToggle(sex_f);
			} else if(n.equals("M")){
				sex_Group.selectToggle(sex_m);
			} else 
			{
				sex_Group.selectToggle(null);
			}			
		});
		graphique.contactBarChartDataProperty().addListener(
				(obsv, oldv, newv) -> { barcity.getData().clear(); 
				barcity.getData().add( newv);
				});
		
		graphique.birthdateBarChartDataProperty().addListener(
				(obsv, oldv, newv) -> { bardate.getData().clear(); 
				bardate.getData().addAll(newv);
				});
				
		nom.textProperty().bindBidirectional(editingContact.nomProperty());
		prenom.textProperty().bindBidirectional(editingContact.prenomProperty());
		voie.textProperty().bindBidirectional(editingContact.voieProperty());
		cp.textProperty().bindBidirectional(editingContact.code_postalProperty());
		ville.textProperty().bindBidirectional(editingContact.villeProperty());	
		editingContact.getMap().addListener(f);
		workspace.getGroupeList().addListener(groupListener);
		graphique.pieChartDataProperty().addListener(p);
		workspace.errorProperty().addListener(
				(observable, oldv, newv) -> Dialogs.create().title("Problème")
						.masthead("Erreur").message(newv).showError());
	}
	//OUVRIR
	@FXML
	private void openFile(ActionEvent evt) {
		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(new File("."));
			File file = fileChooser.showOpenDialog(null);
			if (file != null) {
				workspace.loadFile(file);
			}
		} catch (IOException | ClassNotFoundException e) {
			// TODO: Add dialog box informing the IO Error
			e.printStackTrace();
		}
	}
	//SAUVEGARDER
	@FXML
	private void save() {
		if (workspace.getFile() == null) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(new File("/Users/YU/Documents/workspace/nf28_td04/src/rsc"));
			File file = fileChooser.showSaveDialog(null);
			if (file == null) {
				return;
			}
			workspace.setFile(file);
		}

		try {
			workspace.save();
		} catch (Exception e) {
			// TODO: Add dialog box informing the IO Error
			e.printStackTrace();
		}
	}
	//ADD ELEMENT
		public void plus()
		{
			/*
			TreeItem<Object> currentItem = tree.getSelectionModel().getSelectedItem();
			if(currentItem != null)
			{
				if (currentItem.getValue() instanceof Group)
				{
					editingContact.init();
					contactPane.visibleProperty().set(true);					
				}	
				else if (currentItem.getValue() instanceof Contact)
				{
					return;
				}
				else 
				{
					workspace.addGroupe();		
				}
			}
			*/
			// Nothing selected
			if (selectedItem == null)
				return;
			// adding a group
			if (selectedItem.getParent() == null) {
				workspace.addGroupe();
				return;
			}
			// adding a contact
			if (selectedItem.getValue() instanceof Group) {
				editingContact.init();
				contactPane.visibleProperty().set(true);
				originaleContact = null;
				return;
			}
			// adding a sibling contact
			if (selectedItem.getValue() instanceof Contact) {
				editingContact.init();
				contactPane.visibleProperty().set(true);
				originaleContact = null;
			}
		}
	//SUPRIMER ELEMENT
	@FXML
	public void moins(){
		/*
		TreeItem<Object> currentItem = tree.getSelectionModel().getSelectedItem();
		if(currentItem != null)
		{
			if (currentItem.getValue() instanceof Group)
			{
				workspace.removeGroup((Group)currentGroup.getValue());
			}	
			else if (currentItem.getValue() instanceof Contact)
			{
				workspace.removeContact(originaleContact);
			}
			else
			{
				return;
			}
		}
		*/
		if (selectedItem.getValue() instanceof Group) 
		{
			workspace.removeGroup((Group) selectedItem.getValue());
		} else if (selectedItem.getValue() instanceof Contact) 
		{
			workspace.removeContact((Contact) selectedItem.getValue());
		}
	}
	//VALID
	@FXML
	public void valid()
	{
		/*
		if (editingContact.validChamp()== true)
		{
			if (tree.getSelectionModel().getSelectedItem().getValue() instanceof Contact)
			{	
				workspace.updateContact(originaleContact, editingContact);				
			}
			else{
				editingContact.setGroup((Group)currentGroup.getValue());					
				workspace.addContact(editingContact);
			}
		}	
		*/
		if(editingContact.validChamp())
		{
			if(originaleContact != null)
			{
				workspace.updateContact(originaleContact, editingContact);
				return;
			}
			else 
			{
				Object g = selectedItem.getValue();
				if (selectedItem.getValue() instanceof Contact)
					g = ((Contact) selectedItem.getValue()).getGroup();
				editingContact.setGroup((Group) g);
				workspace.addContact(editingContact);
			}
			
		}
		
	}
	@FXML
	public void openGraphique()
	{
		graphique.setGroups(workspace.getGroupeList());
		graphique.designCharts();
	}
	private final class TextFieldTreeCellImpl extends TreeCell<Object> {
		 
        private TextField textField;
 
        public TextFieldTreeCellImpl() {
        }
 
        @Override
        public void startEdit() {
        	 if(!(tree.getSelectionModel().getSelectedItem().getValue() instanceof Group))
        	 {
        		 return;
        	 }
            super.startEdit();
            if (textField == null) {
                createTextField();
            }
            setText(null);
            setGraphic(textField);
            textField.selectAll();
            
        }
 
        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText(getItem() !=null ? getItem().toString() : null); 
            setGraphic(getTreeItem().getGraphic());
        }
 
        @Override
        public void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
 
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(getTreeItem().getGraphic());
                }
            }
        }
 
        private void createTextField() {
            textField = new TextField(getString());
            textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                    	((Group)getTreeItem().getValue()).getGroupName().set(textField.getText());
                        commitEdit(getTreeItem().getValue());
                    } else if (t.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                    }
                }
            });
        }
 
        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }
}
