package puzzle;

public class Rankdata {
	private String name; //이름
	private String time; //시간

	Rankdata() {
	}

	Rankdata(String name, String time) {
		this.name = name;
		this.time = time;
	}
	
	public String getname() {
		return name;
	}
	
	public String gettime() {
		return time;
	}
}