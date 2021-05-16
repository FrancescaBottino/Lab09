package it.polito.tdp.borders;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import it.polito.tdp.borders.model.Country;
import it.polito.tdp.borders.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {

	private Model model;
	
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="txtAnno"
    private TextField txtAnno; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader
    
    @FXML
    private ComboBox<Country> boxStati;



    @FXML
    void doCalcolaConfini(ActionEvent event) {
    	
    	txtResult.clear();
    	int anno=0;
    	try {
    		anno=Integer.parseInt(txtAnno.getText());
    	}
    	catch(NumberFormatException e) {
    		txtResult.setText("Inserisci un valore numerico");
    		return;
    	}
    	
    	if(anno<1816 || anno>2016) {
    		txtResult.setText("Inserisci un anno tra il 1816 e il 2016");
    		return;
    	}
    	
 
    	model.creaGrafo(anno);
    	
    	System.out.println("Grafo creato con: "+model.getGrafo().vertexSet().size()+" vertici e "+model.getGrafo().edgeSet().size()+" archi.");
    	
    	txtResult.appendText("Numero componenti connesse: "+model.getNumeroComponentiConnesse()+"\n");
    	
    	String s="";
    	
    	//grado del vertice
    	for(Country c: model.getVertexSet()) {
    	    s+=c.getNome()+" "+model.getGrafo().degreeOf(c)+"\n";
    	}
    	
    	txtResult.appendText(s);
    
    }
    
    
    @FXML
    void doStatiRaggiungibili(ActionEvent event) {
    	
    	txtResult.clear();
    	/*
    	 * Facendo click sul bottone “Stati raggiungibili” viene visualizzata la lista 
    	 * di tutti i nodi raggiungibili nel grafo a partire da un vertice selezionato, 
    	 * che coincide con la componente connessa del grafo relativa allo stato scelto.
    	 */
    	
    	Country stato=boxStati.getValue();
    	
    	List<Country> soluzione=model.trovaRaggiungibili1(stato);
    	//Set<Country> soluzione=model.trovaRaggiungibili2(stato);
    	//List<Country> soluzione=model.trovaRaggiungibili3(stato);
    	//List<Country> soluzione=model.trovaRaggiungibili4(stato);
    	//List<Country> soluzione=model.trovaRaggiungibili5(stato);
    	
    	txtResult.appendText("Numero stati raggiungibili: "+soluzione.size()+"\n");
    	
    	for(Country c: soluzione) {
    		txtResult.appendText(c.getNome()+"\n");
    	}
    	
    	
    	

    }


    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert txtAnno != null : "fx:id=\"txtAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxStati != null : "fx:id=\"bpxStati\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	
    	List<Country> countries= model.getCountries();
    	
    	Collections.sort(countries);
    	
    	boxStati.getItems().addAll(countries);
    }
}


