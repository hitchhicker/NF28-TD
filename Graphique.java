package control.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.collections.FXCollections;

public class Graphique {
	
	// DÈclarations
	ObservableList<PieChart.Data> pieChartData;
	ObjectProperty<XYChart.Series<String, Number>> contactBarChartData;
	ObjectProperty<ArrayList<XYChart.Series<Long, String>>> birthdateBarChartData;
	ObservableList<Group> groups;
	public Graphique()
	{
		pieChartData = FXCollections.observableArrayList();
		contactBarChartData = new SimpleObjectProperty<>();
		birthdateBarChartData = new SimpleObjectProperty<>();	
	}
	public void setGroups(ObservableList<Group> g)
	{
		groups = g;
	}
	public ObjectProperty<XYChart.Series<String, Number>> contactBarChartDataProperty()
	{
		return contactBarChartData;
	}
	public ObservableList<PieChart.Data> pieChartDataProperty()
	{
		return pieChartData;
	}
	public ObjectProperty<ArrayList<XYChart.Series<Long, String>>> birthdateBarChartDataProperty()
	{
		return birthdateBarChartData;
	}
	// Constructions
	
	    
	public void designCharts() {
		 // SÈrie pour l'histogramme des villes
		XYChart.Series<String, Number> serie = new XYChart.Series<String, Number>();

    // SÈries pour pour l'histogramme des dates de naissance
		XYChart.Series<Long, String> beforedat1 = new XYChart.Series<Long, String>();
		XYChart.Series<Long, String> between = new XYChart.Series<Long, String>();
		XYChart.Series<Long, String> afterdat2 = new XYChart.Series<Long, String>();

		beforedat1.setName("< " + "1990-01-01".toString());
		between.setName("entre");
		afterdat2.setName(">= " + "2000-01-01".toString());
			// --------------------- pieChart -----------------------------------
			/**
			 * Balaie les groupes et crÈe une donnÈe du graphique : (nom du groupe,
			 * nombre de contacts)
			 */
			
			List<PieChart.Data> ldata = groups
					.stream()
					.map(group -> new PieChart.Data(group.getGroupName().getValue(), group
							.contactSize())).collect(Collectors.toList());
			/**
			 * Met les donnÈes dans une liste observable
			 */
			pieChartData.clear();
			pieChartData.addAll(ldata);
			// --------------------- pieChart -----------------------------------	
			
			// --------------------- barChart -----------------------------------
			/**
			 * CrÈe une map en balayant tous les contacts de type : clÈ = la ville
			 * du contact ; valeur = nombre de contacts ayant cette ville. Balaie la
			 * map pour ajouter une donnÈe du graphique (nom ville, nombre de
			 * contacts pour cette ville) dans la sÈrie
			 */
			groups.stream()
					.flatMap(group -> group.getContactList().stream())
					.collect(
							Collectors.groupingBy(contact -> contact.villeProperty().getValue(),
									 Collectors.counting()))
					.forEach(
							(city, nb) -> serie.getData().add(
									new XYChart.Data<String, Number>(city, nb)));

			/**
			 * Place la sÈrie dans un objet observable
			 */
			contactBarChartData.setValue(serie);
			// --------------------- barChart -----------------------------------
			
			// --------------------- 3barChart ----------------------------------
			/**
			 * On fait trois fois ce qu'on a fait pour l'autre histogramme sauf que
			 * l'on filtre en plus les contacts selon la date de naissance. On
			 * utilise une fonction pour factoriser le code. Elle
			 * prend comme paramËtre  la sÈrie dans laquelle on ajoute les donnÈes graphiques
			 * et le prÈdicat de contact dÈsirÈ.
			 */
	     
	     /**
	      *  prÈdicats
	      */           
			Predicate<Contact> pbefore = contact -> contact.getDate().toString()
					.compareTo("1990-01-01") < 0;
			Predicate<Contact> pbetween = contact -> contact.getDate().toString()
					.compareTo("1990-01-01") >= 0
					&& contact.getDate().getValue().compareTo("2000-01-01".toString()) < 0;
			Predicate<Contact> pafter = contact -> contact.getDate().toString()
					.compareTo("2000-01-01") >= 0;

			/**
			 * On ajoute les donnÈes graphiques pour chaque sÈrie (groupe, nb de
			 * contacts satisfaisoant le prÈdicat
			 */
			filterContactBirthdat(beforedat1, pbefore);
			filterContactBirthdat(between, pbetween);
			filterContactBirthdat(afterdat2, pafter);

			/**
			 * On crÈe une liste de sÈries de donnÈes que l'on place dans un objet
			 * observable
			 */
			ArrayList<XYChart.Series<Long, String>> lseries = new ArrayList<XYChart.Series<Long, String>>();
			lseries.add(beforedat1);
			lseries.add(between);
			lseries.add(afterdat2);

			birthdateBarChartData.setValue(lseries);
		}

		/**
		 * 
		 * @param XYChart
		 *            .Series<Number, Predicate<Contact>
		 * 
		 *            1. filtre les contacts suivant un prÈdicat (portant sur la
		 *            date de naissance) 2. compte les contacts restant par groupe
		 *            d'appartenance. 3. pour chaque (group, nb) ajoute une donnÈe
		 *            graphique ‡ la sÈrie
		 */
		private void filterContactBirthdat(XYChart.Series<Long, String> serie,
				Predicate<Contact> p) {
			groups.stream()
					.flatMap(group -> group.getContactList().stream())
					.filter(p)
					// 1
					.collect(
							Collectors.groupingBy(contact -> contact.getGroup()
									.getGroupName().getValue(), Collectors.counting())) // 2
					.forEach( // 3
							(groupname, nb) -> serie.getData()
									.add(new XYChart.Data<Long, String>(nb,
											groupname)));
		}
	}
