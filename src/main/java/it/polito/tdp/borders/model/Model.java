package it.polito.tdp.borders.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.borders.db.BordersDAO;

public class Model {
	
	private Map <Integer, Country> idMap;
	private BordersDAO dao;
	private SimpleGraph<Country, DefaultEdge> grafo;
	private Map<Country, Country> visita;

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
	
	public List<Country> getCountries(){
		
		List<Country> paesi=new ArrayList<Country>();
		
		for(Country c: idMap.values()) {
			
			if(idMap.values().size()!=0)
				paesi.add(c);
		}
		
		return paesi;
	}
	
	/*Facendo click sul bottone “Stati raggiungibili” viene visualizzata la lista 
	 * di tutti i nodi raggiungibili nel grafo a partire da un vertice selezionato, 
     * che coincide con la componente connessa del grafo relativa allo stato scelto.
     */
	
	
	//METODO RICORSIVO
	
	public List<Country> trovaRaggiungibili1(Country inizio) {
		
		List<Country> soluzione=new ArrayList<Country>();
		
		cerca(soluzione, inizio);
		
		return soluzione;
		
	}
	
	public void cerca(List<Country> soluzione, Country inizio) {
		
		//generazione sotto problemi
		
		soluzione.add(inizio);
		
		for(Country vicino: Graphs.neighborListOf(grafo, inizio)) {
			
			if(!soluzione.contains(vicino)) {
				cerca(soluzione, vicino);
			
			}
			
			
		}
			
		
	}

	// METODO CON IL METODO DELLA CLASSE CONNECTIVITY INSPECTOR
	
	public Set<Country> trovaRaggiungibili2(Country inizio){
		
		return new ConnectivityInspector<Country, DefaultEdge>(grafo).connectedSetOf(inizio);
	}
	
	//METODO CON IL TRAVERSAL LISTENER
	
	public List<Country> trovaRaggiungibili3(Country inizio){
		
		List<Country> percorso= new ArrayList<Country>();
		
		BreadthFirstIterator<Country, DefaultEdge> it= new BreadthFirstIterator<>(grafo, inizio);
		
		this.visita= new HashMap<Country, Country>();
		visita.put(inizio, null);
		
		
		it.addTraversalListener(new TraversalListener<Country, DefaultEdge>(){

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
				
				
				Country c1=grafo.getEdgeSource(e.getEdge());
				Country c2= grafo.getEdgeTarget(e.getEdge());
				
				if(visita.containsKey(c1) && !visita.containsKey(c2)) {
					visita.put(c2, c1);
				}
				else if(visita.containsKey(c2) && !visita.containsKey(c1)){
					visita.put(c1,  c2);
					
				}
				
				
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Country> e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Country> e) {
				// TODO Auto-generated method stub
				
			}
			});
		
			while(it.hasNext()) {
				
				percorso.add(it.next());
			}
			
			//ottengo il percorso dall'albero di visita
			
		
			if(!visita.containsKey(inizio))
				return null;
			
			return percorso;
		
	}
	
	//METODO ITERATIVO
	
	public List<Country> trovaRaggiungibili4(Country inizio){
		
		List<Country> daVisitare= new ArrayList<Country>();
		List<Country> visitati= new ArrayList<Country>();
		
		visitati.add(inizio);
		
		daVisitare.addAll(Graphs.neighborListOf(grafo, inizio));
		
		while(!daVisitare.isEmpty()) {
			//c'è ancora da aggiungere 
			Country c=daVisitare.remove(0);
			visitati.add(c);
			List<Country> vicini= Graphs.neighborListOf(grafo, c);
			vicini.removeAll(visitati);
			vicini.removeAll(daVisitare);
			daVisitare.addAll(vicini); //quelli che mi mancano
			
		}
		
		
		return visitati;
			
		
	}
	
	
	//METODO CON DFV (metodo visita in profondità)
	
	public List<Country> trovaRaggiungibili5(Country inizio){
		
		List<Country> percorso= new ArrayList<Country>();
		
		GraphIterator<Country, DefaultEdge> dfv= new DepthFirstIterator<Country, DefaultEdge>(grafo, inizio);
		
		while(dfv.hasNext()) {
			percorso.add(dfv.next());
		}
		
		return percorso;
		
	}
	
	

}
