package mw.editor;

import mw.library.Area;

public class Template {
	
	private Area area;
	private int sections = 0; // will later store the section list
	
	public Template(Area area) {
		this.area = area;
	}
	
	public void reset() {
		this.sections = 0; // reset the section list
	}
}
