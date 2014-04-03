package org.fusuma.to;

import java.util.ArrayList;

public class SAMTPPad extends ArrayList<String> {

	int[] lengths = new int[0];

	public int[] getLengths() {
		return lengths;
	}

	public void setLengths(int[] lengths) {
		this.lengths = lengths;
	}

	public int sumLengths() {
		int sum = 0;
		for (int length : this.lengths) {
			sum += length;
		}
		return sum;
	}

}
