package control.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Workspace {

	private ObservableList<Group> groupeList;
	private File fileToSave;
	private StringProperty error;
	
	public Workspace()
	{
		groupeList = FXCollections.observableArrayList();
		error = new SimpleStringProperty();
	}
	public void addGroupe() {
		Group group = new Group();
		groupeList.addAll(group);
	}
	public ObservableList<Group> getGroupeList() {
		return groupeList;
	}
	public void setGroupeList(ObservableList<Group> groupeList) {
		this.groupeList = groupeList;
	}
	public void addContact(Contact c)
	{
		c.getGroup().addContact(c);
	}
	public void removeContact(Contact c)
	{
		c.getGroup().removeContact(c);
	}
	public void updateContact(Contact o, Contact n)
	{
		o.getGroup().updateContact(o,n);
	}
	public StringProperty errorProperty() {
		return error;
	}
	public void removeGroup(Group g)
	{
		groupeList.remove(g);
	}
	public void setFile(File file) {
		this.fileToSave = file;
	}

	public File getFile() {
		return fileToSave;
	}
	public void loadFile(File file) throws IOException, ClassNotFoundException {
		groupeList.clear();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				file))) {
			Object[] grs = (Object[]) ois.readObject();
			for (int i = 0; i < grs.length; i++) {
				Group g = (Group) grs[i];
				groupeList.add(g);
				Object[] l = g.contactList().toArray();
				g.contactList().clear();
				for (int j = 0; j < l.length; j++) {
					g.contactList().add((Contact) l[j]);
				}

				System.out.println(g);
			}
			ois.close();
			setFile(file);
		} catch (Exception e) {
			e.printStackTrace();
			error.setValue("Erreur de lecture du fichier");
		}
	}

	public void save() {
		/**
		 * Workspace stored as an array of Groups
		 */
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(getFile()));
			oos.writeObject(groupeList.toArray());
			oos.close();
		} catch (IOException e) {
			error.setValue("Erreur d'écriture du fichier");
			e.printStackTrace();
		}

	}
}
