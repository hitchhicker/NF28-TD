package control.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class Contact implements Externalizable{
	private StringProperty text_nom;
	private StringProperty text_prenom;
	private StringProperty text_voie;
	private StringProperty text_code_postal;
	private StringProperty text_ville;
	private StringProperty text_pays;
	private StringProperty text_groupe;
	private ObservableMap<String, String> map;
	private StringProperty date;
	private StringProperty sex;
	private Group group;
	
	public Contact()
	{
		text_nom = new SimpleStringProperty(this,"text_nom","");
		text_prenom = new SimpleStringProperty(this,"text_prenom","");
		text_voie = new SimpleStringProperty(this,"text_voie","");
		text_code_postal = new SimpleStringProperty(this,"text_code_postal","");
		text_ville = new SimpleStringProperty(this,"text_ville","");
		text_pays = new SimpleStringProperty(this,"text_pays","");
		date = new SimpleStringProperty(this,"date","");
		sex = new SimpleStringProperty(this,"sex","");
		map = FXCollections.observableHashMap();
	}
	
	public String toString()
	{
		return text_prenom.getValue() + " " + text_nom.getValue();
	}
	
	public boolean validChamp()
	{
		map.clear();
		if(text_nom.getValue().isEmpty())
		{
			map.put(text_nom.getName(), "nom est vide");
		}
		if(text_prenom.getValue().isEmpty())
		{
			map.put(text_prenom.getName(), "prenom est vide");
		}
		if(text_code_postal.getValue().isEmpty())
		{
			map.put(text_code_postal.getName(), "code postal est vide");
		}
		if(text_pays.getValue().isEmpty())
		{
			map.put(text_pays.getName(), "pays est vide");
		}
		if(text_ville.getValue().isEmpty())
		{
			map.put(text_ville.getName(), "ville est vide");
		}
		if(text_voie.getValue().isEmpty())
		{
			map.put(text_voie.getName(), "voie est vide");
		}
		if(date.getValue().isEmpty())
		{
			map.put("date","date est vide");
		}
		if(sex.getValue().isEmpty())
		{
			map.put("sex", "sex est vide");
		}
		return map.isEmpty();
	}
	public void init()
	{
		(new Contact()).copyContact(this);
	}

	public void copyContact(Contact c)
	{
		c.nomProperty().setValue(text_nom.getValue());
		c.prenomProperty().setValue(text_prenom.getValue());
		c.voieProperty().setValue(text_voie.getValue());
		c.villeProperty().setValue(text_ville.getValue());
		c.code_postalProperty().setValue(text_code_postal.getValue());
		c.payspProperty().setValue(text_pays.getValue());
		c.getDate().setValue(date.getValue());
		c.getSex().setValue(sex.getValue());
		c.setGroup(getGroup());
	}
	public ObservableMap<String, String> getMap() {
		return map;
	}

	public void setMap(ObservableMap<String, String> map) {
		this.map = map;
	}

	public StringProperty nomProperty()
	{
		return text_nom;
	}
	public StringProperty prenomProperty()
	{
		return text_prenom;
	}
	public StringProperty voieProperty()
	{
		return text_voie;
	}
	public StringProperty code_postalProperty()
	{
		return text_code_postal;
	}
	public StringProperty villeProperty()
	{
		return text_ville;
	}
	public StringProperty payspProperty()
	{
		return text_pays;
	}
	public StringProperty groupeProperty()
	{
		return text_groupe;
	}
	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public StringProperty getDate() {
		return date;
	}

	public void setDate(StringProperty date) {
		this.date = date;
	}

	public StringProperty getSex() {
		return sex;
	}

	public void setSex(StringProperty sex) {
		this.sex = sex;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeUTF(nomProperty().get());
		out.writeUTF(prenomProperty().get());
		out.writeUTF(voieProperty().get());
		out.writeUTF(code_postalProperty().get());
		out.writeUTF(villeProperty().get());
		out.writeUTF(payspProperty().get());
		out.writeUTF(date.get());
		out.writeUTF(sex.get());
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub
		nomProperty().set(in.readUTF());
		prenomProperty().set(in.readUTF());
		voieProperty().set(in.readUTF());
		code_postalProperty().set(in.readUTF());
		villeProperty().set(in.readUTF());
		payspProperty().set(in.readUTF());
		date.set(in.readUTF());
		sex.set(in.readUTF());
	}

}
