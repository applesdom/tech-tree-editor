package dom.techtree;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JFrame;

import dom.techtree.gui.TechTreeEditorFrame;

public class Main {
	public static void main(String[] args) {
		try {
			Persistent.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		TechTreeEditorFrame frame = new TechTreeEditorFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					Persistent.save();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
}
