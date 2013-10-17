package ingenias.editor.events;

public class Key {

	private Object a;
	private Object b;

	public Key(Object a, Object b){
		this.a=a;
		this.b=b;
	}
	
	
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return a.hashCode()+b.hashCode(); // to satisfy that if k1.equals(k2) ==> k1.hashcode()==k2.hashcode();
	}



	public boolean equals(Object o){
		if (o instanceof Key){
			Key target=(Key)o;
			if (target.a==null && target.b==null)
				return target.a==this.a && target.b==this.b;
			return target.a.equals(this.a) && target.b.equals(this.b);
		}
		return super.equals(o);
	}

	public Object getA() {
		// TODO Auto-generated method stub
		return a;
	}
	public Object getB() {
		// TODO Auto-generated method stub
		return b;
	}
	
	public String toString(){
		return a.toString()+":"+b.toString();
	}
}
