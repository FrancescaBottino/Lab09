package it.polito.tdp.borders.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.borders.db.BordersDAO;

public class Model {
	
	private Map <Integer, Country> idMap;
	private BordersDAO dao;
	private SimpleGraph<Country, DefaultEdge> grafo;

	public Model() {
		
		idMap= new HashMap<Integer, Country>();
		dao=new BordersDAO();
		dao.loadAllCountries(idMap);
		
		
	}
	
	public void creaGrafo(int anno) {
		
		this.grafo= new SimpleGraph<>(DefaultEdge.class);
		
		//vertici
		
		List<Country> vertici= dao.getVertici(anno);
		
		Graphs.addAllVertices(this.grafo, vertici);
		
		//archi
		
		for(Border b: dao.getCountryPairs(anno, idMap)) {
			
			if(this.grafo.containsVertex(b.getC1()) && this.grafo.containsVertex(b.getC2())) {
				
				DefaultEdge e= this.grafo.getEdge(b.getC1(), b.getC2());
				
				if(e==null) {
					Graphs.addEdgeWithVertices(this.grafo, b.getC1(), b.getC2());
				}
				
				
			}
			
		}
		
		
		
	}
	
	public Set<Country> getVertexSet(){
		return grafo.vertexSet();
	}

	public SimpleGraph<Country, DefaultEdge> getGrafo() {
		return grafo;
	}
	
	public int getNumeroComponentiConnesse() {
		
		ConnectivityInspector<Country, DefaultEdge> c= new ConnectivityInspector<>(grafo);
		
		return c.connectedSets().size();
		
		
	}
	
	

}
