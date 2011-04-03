package fst;

public class StringLink extends Link<ResultCollector>{
	private final String lowerString;
	private final String upperString;

	public StringLink(String lowerString, String upperString, State<ResultCollector> target){
		this.lowerString=lowerString;
		this.upperString=upperString;
		setTarget(target);
	}

	public StringLink(String input, String output, double d,
			State<ResultCollector> target) {
		this(input,output,target);
		setWeight(d);
	}

	@Override
	public boolean cross(State<ResultCollector> source, Configuration<ResultCollector> configuration) {
		super.cross(source, configuration);
		Tape lowerHead=configuration.getInputTape();
		for (char ch: lowerString.toCharArray()){
			if (!lowerHead.canRead()) return false;
			if (ch!=lowerHead.read()) return false;
		}
		for (char ch: upperString.toCharArray()){
			configuration.getOutputTape().write(ch);
		}
		return true;
	}

	@Override
	public String toString() {
		return lowerString+"->"+upperString;
	}
}
