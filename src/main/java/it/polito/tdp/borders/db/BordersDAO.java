package it.polito.tdp.borders.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.tdp.borders.model.Border;
import it.polito.tdp.borders.model.Country;

public class BordersDAO {

	public Map<Integer,Country> loadAllCountries(Map<Integer, Country> idMap) {

		String sql = "SELECT ccode, StateAbb, StateNme FROM country ORDER BY StateAbb";
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				Country c= new Country(rs.getInt("ccode"), rs.getString("StateAbb"), rs.getString("StateNme"));
				idMap.put(rs.getInt("ccode"), c);
				//System.out.format("%d %s %s\n", rs.getInt("ccode"), rs.getString("StateAbb"), rs.getString("StateNme"));
			}
			
			conn.close();
			return idMap;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	
	public List<Border> getCountryPairs(int anno, Map<Integer, Country> idMap) {

		//System.out.println("TODO -- BordersDAO -- getCountryPairs(int anno)");
		
		String sql = "SELECT state1no as id_s1, state2no as id_s2, year "
				+ "FROM contiguity "
				+ "WHERE year <= ? AND conttype= ? AND state1no > state2no ";
		
		List<Border> result = new ArrayList<Border>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			st.setInt(2, 1);
			ResultSet rs = st.executeQuery();
		
		
			while(rs.next()) {
				
				Country c1= idMap.get(rs.getInt("id_s1"));
				Country c2= idMap.get(rs.getInt("id_s2"));
				
				result.add(new Border(c1, c2, rs.getInt("year")));
				
				
			}
			
			conn.close();
			return result;
		
		
		}  catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	

	}
	
	
	public List<Country> getVertici(int anno){
		
		String sql="SELECT DISTINCT c.CCode, c.StateAbb, c.StateNme "
				+ "FROM contiguity cc, country c "
				+ "WHERE year <= ? AND conttype=? AND (c.CCode=cc.state1no OR c.CCode=cc.state2no) ";
		
		List<Country> result= new ArrayList<Country>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			st.setInt(2, 1);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				
				result.add(new Country (rs.getInt("CCode"), rs.getString("StateAbb"), rs.getString("StateNme")));
				
			}
			
			
			conn.close();
			return result;
			
		
		}catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	
		
	}
	
}
