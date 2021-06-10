package it.polito.tdp.ruzzle.model;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.ruzzle.db.DizionarioDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Model {
	private final int SIZE = 4;
	private Board board ;
	private List<String> dizionario ; //=> lista che contiene tutte le parole estratte dal DAO
	private StringProperty statusText ;

	public Model() {
		this.statusText = new SimpleStringProperty() ;
		
		this.board = new Board(SIZE);
		DizionarioDAO dao = new DizionarioDAO() ;
		this.dizionario = dao.listParola() ;
		statusText.set(String.format("%d parole lette", this.dizionario.size())) ;
	
	}
	
	public void reset() {
		this.board.reset() ;
		this.statusText.set("Board Reset");
	}

	public Board getBoard() {
		return this.board;
	}

	public final StringProperty statusTextProperty() {
		return this.statusText;
	}
	

	public final String getStatusText() {
		return this.statusTextProperty().get();
	}
	

	public final void setStatusText(final String statusText) {
		this.statusTextProperty().set(statusText);
	}

	public List<Pos> trovaParola(String parola) {
		for(Pos p : board.getPositions()) {
			//questa cella contiene la lettera iniziale della mia parola?
			if(board.getCellValueProperty(p).get().charAt(0) //=> recupero la StringProperty della board in posizione p
					== parola.charAt(0)) {
			List<Pos> percorso = new ArrayList<Pos>(); //per tenere traccia delle lettere già riconosciute
			                                           // sarebbe la soluzione parziale
			percorso.add(p);
			if(cerca(parola, 1, percorso))
				return percorso;
		}
		}
		return null;//se non ho trovato la parola
	}

	private boolean cerca(String parola, int livello, List<Pos> percorso) {
		//caso terminale
		if(livello == parola.length()) {
			return true;
		}
		//prendo l'ultima lettere inserita e vado a recuperare tutte le adiacenti
		//tra quelle adiacenti vado a scegliere quella che potenzialmente riesce a farmi continuare la mia parola
		Pos ultima = percorso.get(percorso.size()-1);
		List<Pos> adiacenti = board.getAdjacencies(ultima);//metodo già presente nel modello base
		
		for(Pos p : adiacenti) {
			//non posso utilizzare due volte la stessa lettere
			//e controllo che la lettere adiscente che sto prendendo in considerazione coincida con la letetre presente nella parola in quella 
			//posizione che in questo che in questo caso corrisponde al livello
			if(!percorso.contains(p)&& parola.charAt(livello) == board.getCellValueProperty(p).get().charAt(0)) {
				percorso.add(p);//aggiungo la lettera p alla soluzione parziale
				
				//faccio ricorsione
				    //uscita rapida
				    if(cerca(parola,livello+1,percorso) == true)
				    	return true;// se l'ho trovata è inutile andare avanti con la ricorsione
				
				cerca(parola, livello+1, percorso);
				//faccio backtracking
				percorso.remove(percorso.size()-1);
			}
		}
		return false;
	}

	public List<String> trovaTutte() {
		// Prende tutte le parole del dizionario e er ognuna di esse richiama il trova parola
		List<String> tutte = new ArrayList<String>();
		for(String parola : dizionario) {
			parola = parola.toUpperCase();
			if(parola.length() > 1) { //=> come regola non valgono le parole formate da un adola lettera
			if(this.trovaParola(parola) != null ) {
				tutte.add(parola);
			}
		}
		}
		return tutte;
	}
	

}
