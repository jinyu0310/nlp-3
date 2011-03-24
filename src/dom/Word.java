package dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Word {
	private final List<Splitting> splittings = new ArrayList<Splitting>();

	String name;

	public int count = 1;

	public Word(String name) {
		this.name = name;
	}

	public List<Splitting> getSplittings() {
		return Collections.unmodifiableList(splittings);
	}

	public void generateSplittings(HashSet<String> prefixes,
			HashSet<String> suffixes) {
		int length = name.length();

		for (int stem_start = 0; stem_start < length; stem_start++) {
			for (int suffix_start = stem_start + 1; suffix_start <= length; suffix_start++) {
				String prefix = name.substring(0, stem_start);
				String suffix = name.substring(suffix_start, length);
				if (prefixes.contains(prefix) && suffixes.contains(suffix)) {
					String stem = name.substring(stem_start, suffix_start);
					// System.out.println(prefix + "/" + stem + "/" + suffix);

				}
			}
		}
	}

	public void addSplitting(Splitting s) {
		if (s.getWord() != null)
			throw new Error("splitting already added to an other word");
		splittings.add(s);
		s.setWordImp(this);
	}

	public void removeSplitting(Splitting s) {
		splittings.remove(s);
		s.setWordImp(null);
	}

	void addSplittingImp(Splitting splitting) {
		splittings.add(splitting);
	}

	void removeSplittingImp(Splitting splitting) {
		splittings.remove(splitting);
	}
}
