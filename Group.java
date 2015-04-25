package control.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.LinkedHashSet;
import java.util.Set;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Group implements Externalizable{
	private ObservableList<Contact> contactList;
	private StringProperty groupName;
	
	public Group()
	{
		contactList = FXCollections.observableArrayList();
		groupName = new SimpleStringProperty("nom du groupe");
	}
	public ObservableList<Contact> contactList()
	{
		return contactList;
	}
	public StringProperty getGroupName() {
		return groupName;
	}
	public String toString()
	{
		return groupName.getValue();
	}

	public int contactSize()
	{
		return contactList.size();
	}
	public void setGroupName(StringProperty groupName) {
		this.groupName = groupName;
	}

	public ObservableList<Contact> getContactList() {
		return contactList;
	}

	public void setContactList(ObservableList<Contact> contactList) {
		this.contactList = contactList;
	}
	
	public void addContact(Contact c)
	{
		Contact contact = new Contact();
		contact.getDate().setValue(c.getDate().getValue());
		contact.getSex().setValue(c.getSex().getValue());
		contact.nomProperty().setValue(c.nomProperty().getValue());
		contact.prenomProperty().setValue(c.prenomProperty().getValue());
		contact.villeProperty().setValue(c.villeProperty().getValue());
		contact.voieProperty().setValue(c.voieProperty().getValue());
		contact.code_postalProperty().setValue(c.code_postalProperty().getValue());
		contact.payspProperty().setValue(c.payspProperty().getValue());
		contact.setGroup(c.getGroup());
		contactList.addAll(contact);
	}
	public void removeContact(Contact c)
	{
		contactList.remove(c);
	}
	public void updateContact(Contact o, Contact n)
	{
		contactList.remove(o);
		addContact(n);
		
	}
	/**
	 * A special approach to serialize objects with javafx properties. All
	 * javafx properties lack of serialization. Thus, the basic idea to overcome
	 * this issue is to serialize the content instead of the properties and
	 * related componnets such as listeners.
	 * 
	 * @param out
	 * @throws IOException
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeUTF(groupName.get());
		out.writeObject(new LinkedHashSet<>(contactList));
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		groupName.set(in.readUTF());
		contactList.clear();
		((Set<Contact>) in.readObject()).forEach(c -> {
			contactList.add(c);
			c.setGroup(this);
		});

	}
}
