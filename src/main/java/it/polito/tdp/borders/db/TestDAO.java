package it.polito.tdp.borders.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.tdp.borders.model.Country;
import it.polito.tdp.borders.model.Border;

public class TestDAO {

	public static void main(String[] args) {

		BordersDAO dao = new BordersDAO();

		System.out.println("Lista di tutte le nazioni:");
		Map<Integer, Country> countries = dao.loadAllCountries(new HashMap<Integer, Country>());
		
		List<Border> confini= dao.getCountryPairs(2000, countries);
		
		for(Border b: confini) {
			System.out.println(b.getC1().getNome()+"-"+b.getC2().getNome());
		}
	}
}
