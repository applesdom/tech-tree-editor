package dom.techtree.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import dom.techtree.data.Part;

public class PartSelectDialog extends JDialog {
	private JPanel partListPanel;
	private boolean ok;
	
	public PartSelectDialog(JFrame parent) {
		super(parent, "Part Picker", JDialog.ModalityType.DOCUMENT_MODAL);
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setSize(new Dimension(360, 500));
		this.setResizable(false);
		
		partListPanel = new JPanel();
		partListPanel.setBackground((new JTextField()).getBackground());
		partListPanel.setLayout(new BoxLayout(partListPanel, BoxLayout.PAGE_AXIS));
		JScrollPane scrollPane = new JScrollPane(partListPanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(scrollPane, BorderLayout.CENTER);
		
		JButton okButton = new JButton();
		okButton.setText("Ok");
		PartSelectDialog thisDialog = this;
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ok = true;
				thisDialog.setVisible(false);
			}
		});
		this.add(okButton, BorderLayout.PAGE_END);
	}
	
	public void setPartList(Collection<Part> partList) {
		partListPanel.removeAll();
		
		// Add all parts to the gui
		for(Part part : partList) {
			partListPanel.add(new PartPanel(part, true, false));
		}
		//System.out.println();
	}
	
	//public List<> getSelectedPartList()
	
	public List<Part> showSelectDialog() {
		ok = false;
		this.setVisible(true);
		this.setEnabled(true);
		this.repaint();
		
		// Block until user has made selection and closed window (or clicked "OK")
		try {
			while(this.isVisible()) {
				Thread.sleep(10);
			}
		} catch(InterruptedException e) {
			//e.printStackTrace();
		}
		
		// If "OK" was clicked, parse parts to gather all selected, otherwise return null
		if(ok) {
			List<Part> ret = new ArrayList<Part>();
			for(Component c : partListPanel.getComponents()) {
				PartPanel partPanel = (PartPanel) c;
				if(partPanel.isSelected()) {
					ret.add(partPanel.getPart());
				}
			}
			return ret;
		} else {
			return null;
		}
	}
}
