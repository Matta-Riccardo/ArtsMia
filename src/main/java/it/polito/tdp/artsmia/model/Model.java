package it.polito.tdp.artsmia.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {

	private Graph<ArtObject, DefaultWeightedEdge> grafo;
	private ArtsmiaDAO dao;
	private Map<Integer, ArtObject> idMap;
	
	public Model() {
		dao = new ArtsmiaDAO();
		idMap = new HashMap<Integer, ArtObject>();
	}
	
//	Essendo che potrei dover ricreare il grafo più volte, facendo la new nel costruttore verrà creato una volta sola quando chiamo Model per la prima volta
//	se necessito di ricreare un altro grafo dovrò ricordarmi di svuotare quello di partenza e ricrearne uno nuovo, alloro la new la faccio dentro al metodo creaGrafo()
	
	public void creaGrafo() {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		dao.listObjects(idMap);
		Graphs.addAllVertices(this.grafo, idMap.values());
		
//		Aggiungo gli archi.....
//		Approccio 1. --------> QUESTO APPROCCIO NON GIUNGE AL TERMINE (FUNZIONA SOLO CON UN PICCOLO NUMERO DI VERTICI)
//		for(ArtObject a1 : this.grafo.vertexSet()) {
//			for(ArtObject a2 : this.grafo.vertexSet()) {
//				if(!a1.equals(a2) && !this.grafo.containsEdge(a1,a2)) {
//					int peso = dao.getPeso(a1,a2);
//					if(peso > 0) {
//						Graphs.addEdgeWithVertices(this.grafo, a1, a2, peso);
//					}
//				}
//			}
//		}
		
//		Approccio 2. Blocco un oggetto e mi faccio dare dal database tutti gli oggetti ad esso collegati con relativo peso
		
		for(Adiacenza a : this.dao.getAdiacenze(idMap)) {
			Graphs.addEdgeWithVertices(this.grafo, a.getA1(), a.getA2(), a.getPeso());
		}
		
		
		
		
		System.out.println("Grafo creato!");
		System.out.println("# VERTICI: " + this.grafo.vertexSet().size());
		System.out.println("# ARCHI: " + this.grafo.edgeSet().size());
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public ArtObject getObject(int objectId) {
		return idMap.get(objectId);
	}

	public int getComponenteConnessa(ArtObject vertice) {
		Set<ArtObject> visitati = new HashSet<ArtObject>();
		
		DepthFirstIterator<ArtObject, DefaultWeightedEdge> it = new DepthFirstIterator<ArtObject, DefaultWeightedEdge>(this.grafo, vertice); //per la ricerca passo il grafo e il vertice di partenza
		while(it.hasNext()) {
			visitati.add(it.next());
		}
		
		return visitati.size();
	}
}
