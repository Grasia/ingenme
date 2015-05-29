package ingenias.editor;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

public class FieldPositionHelper {
	
	private static Hashtable<Rectangle,String> positions=new Hashtable<Rectangle,String>(); 
	
	public static synchronized void clear(){
		positions.clear();
	}
	
	/**
	 * 
	 * @param fieldName The name where the entity will be inserted
	 * @param id the id of the entity inserted
	 * @param pos the rectangle defining the bounds with reference the graph
	 */
	public static synchronized  void put(String fieldName,String id, Rectangle pos){
		positions.put(pos,fieldName+"::"+id);
	}
	
	public static synchronized void drawKnownLinks(Graphics g){
		for (Rectangle key:positions.keySet()){ 
			g.drawRect(key.x, key.y, key.width, key.height);
		}
	}
	
	public synchronized static Set<String> getFieldAt(Rectangle pos){
		HashSet<String> result=new HashSet<String>();
		for (Rectangle key:positions.keySet()){
			String[] keyParts=positions.get(key).split("::");
			String fieldName=keyParts[0];
			if (keyParts.length>=2){
				String entID=keyParts[1];
			}
			
			if (key.intersects(pos)){
					result.add(fieldName);
			}
		}
		
		return result;
	}
	
	public synchronized static Hashtable<Rectangle,String> getLinkAt(Rectangle pos){
		Hashtable<Rectangle,String>  result=new Hashtable<Rectangle,String>() ;
		for (Rectangle key:positions.keySet()){
			String[] keyParts=positions.get(key).split("::");
			String fieldName=keyParts[0];
			if (fieldName.startsWith("http:")  ){					
				if (key.intersects(pos)){
					result.put(key,fieldName);					
			}
			}
		}
		
		return result;
	}

	public synchronized static Set<String> getEntityAt(Rectangle pos){
		HashSet<String> result=new HashSet<String>();
		for (Rectangle key:positions.keySet()){
			String[] keyParts=positions.get(key).split("::");
			String fieldName=keyParts[0];
			if (key.intersects(pos)){					
					if (keyParts.length>=2 && !keyParts[1].equals("")){
						String entID=keyParts[1];
						result.add(entID);
					}
			}
		}
		
		return result;
	}
}