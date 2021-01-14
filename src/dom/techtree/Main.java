package dom.techtree;

import javax.swing.JFrame;

import dom.techtree.gui.TechTreeEditorFrame;

public class Main {
	public static void main(String[] args) {
		TechTreeEditorFrame frame = new TechTreeEditorFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
}
