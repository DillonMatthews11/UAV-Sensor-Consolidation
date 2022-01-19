import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;
import java.util.Vector;
import java.util.Scanner;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import java.util.Collections;
import java.util.HashMap;
public class UAVSystem {

	public static void main(String[] args) {
		Vector<object> csv = parseCSV(new File(args[0]));
		Vector <object> json= parseJson(new File(args[1]));
		Vector<Vector<Integer>> result = getResults(csv,json);
		writeOutput(result);
		
	}
	public static void writeOutput(Vector<Vector<Integer>> result) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));
			int count = 0;
			for(Vector<Integer> v : result) {
				count++;
				String s = "";
				s+=v.get(0);
				s+=":";
				s+= v.get(1);
				if(count < result.size())
					s+="\n";
				bw.write(s);
				
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static Vector<Vector<Integer>> getResults(Vector<object> csv,Vector<object> json){
		Vector<Vector<Integer>> res = new Vector<Vector<Integer>>();
		HashMap<Integer,object> csvMap = new HashMap<Integer,object>();
		for(object o : csv) {
			
			csvMap.put(o.getId(),o);
		}		
		HashMap<Integer,object> jsonMap = new HashMap<Integer,object>();
		for(object o : json) {
			
			jsonMap.put(o.getId(),o );
		}
		while(!jsonMap.isEmpty()||!csvMap.isEmpty()) {
			Vector<Double> shortestDistances = new Vector<Double>();
			HashMap<Double,Vector<Integer>> shortestDistanceMap = new HashMap<Double, Vector<Integer>>();
			for(int c : csvMap.keySet()) {
				double shortest = 1111111;
				int shortestID = -1;
				for(int j : jsonMap.keySet()) {
					if(csvMap.get(c).distance(jsonMap.get(j))<shortest) {
						shortestID = j;
						shortest = csvMap.get(c).distance(jsonMap.get(j));
						
					}
				}
				Vector<Integer> pair = new Vector<Integer>();
				pair.add(c);
				pair.add(shortestID);
				shortestDistanceMap.put(shortest, pair);
				shortestDistances.add(shortest);
			}
			Collections.sort(shortestDistances);
			for(double distance : shortestDistances) {
				Vector<Integer> temp = shortestDistanceMap.get(distance);
				if(distance >100) {
					Vector<Integer> v = new Vector<Integer>();
					v.add(temp.get(0));
					v.add(-1);
					res.add(v);
					csvMap.remove(temp.get(0));
				}
				else {
					if(jsonMap.containsKey(temp.get(1))) {
						res.add(temp);
						csvMap.remove(temp.get(0));
						jsonMap.remove(temp.get(1));
					}
				}
			}
			if(csvMap.isEmpty()&&!jsonMap.isEmpty()) {
				for(int j : jsonMap.keySet()) {
					Vector<Integer> v = new Vector<Integer>();
					v.add(-1);
					v.add(j);
					res.add(v);
					
					
				}
				jsonMap.clear();
			}
			
				
			
		}
		
		return res;
	}
	
	public static Vector<object> parseJson(File f){
		Vector<object> res = new Vector<object>();
		JSONParser jsonParser = new JSONParser();
        
        try (FileReader reader = new FileReader(f))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            JSONArray list = (JSONArray) obj;
            
             //System.out.println(list);
             
             list.forEach( in -> parseInputObject( (JSONObject) in , res) );
            
           
 
        } catch (Exception e) {
            e.printStackTrace();
        }
		return res;
	}
	public static void parseInputObject(JSONObject input, Vector<object> res) {
		
		String id = (String) input.get("Id");    
       double latitude = (double) input.get("Latitude");
       double longitude = (double) input.get("Longitude");
       object o = new object(Integer.parseInt(id),latitude,longitude);
       res.add(o);
	}
	public static Vector<object> parseCSV(File f){
		Vector<object> res = new Vector<object>();
		Scanner sc;
		try {
			sc = new Scanner(f);
			sc.useDelimiter(",|\\n");
			sc.next();
			sc.next();
			sc.next();
			while (sc.hasNext()){  
				String s = sc.next();
				if(!s.isBlank())
					res.add(new object(Integer.parseInt(s),Double.parseDouble(sc.next()),Double.parseDouble(sc.next())));
				
			}   
			sc.close();  
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		return res;
	}
	

}
class object{
	private int id;
	private double lat;
	private double lon;
	public object(int id, double lat, double lon) {
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		
	}
	public double distance(object dest) {
		double dlat = this.lat - dest.getLat();
		double dlon = this.lon - dest.getLon();
		return Math.sqrt(dlat*dlat+dlon*dlon);
	}
	public int getId() {
		return id;
	}
	public double getLon() {
		return lon;
	}
	public double getLat() {
		return lat;
	}
}
