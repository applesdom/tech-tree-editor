package dom.techtree.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import dom.techtree.LocalizationManager;
import dom.techtree.data.Part;

@SuppressWarnings("serial")
public class PartPanel extends JPanel {
	private static final Color NORMAL_COLOR = new JTextField().getBackground(),
							   INVALID_COLOR = new Color(255, 192, 192),
							   SELECTED_COLOR = new Color(196, 232, 128);
	
	private Part part;
	private JCheckBox hiddenBox;
	private JTextField entryCostField;
	private boolean selected;
	
	public PartPanel(Part part) {
		this(part, false, false);
	}
	
	public PartPanel(Part part, boolean selectable, boolean editable) {
		this.part = part;
		
		this.setLayout(null);
		this.setBorder(BorderFactory.createRaisedBevelBorder());
		this.setPreferredSize(new Dimension(390, 27));
		this.setMinimumSize(new Dimension(390, 27));
		this.setMaximumSize(new Dimension(390, 27));
		this.setBackground(NORMAL_COLOR);
		
		if(selectable) {
			PartPanel thisPanel = this;
			MouseAdapter mouseAdapter = new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					thisPanel.setBackground(selected ? NORMAL_COLOR : SELECTED_COLOR);
					selected = !selected;
				}
			};
			this.addMouseListener(mouseAdapter);
		}
		
		JLabel titleLabel = new JLabel();
		String localTitle = LocalizationManager.translate(part.title);
		if(localTitle == null) {
			titleLabel.setText(" " + part.title);
		} else {
			titleLabel.setText(" " + localTitle);
		}
		titleLabel.setSize(235, 20);
		titleLabel.setLocation(0, 3);
		this.add(titleLabel);
		
		hiddenBox = new JCheckBox();
		hiddenBox.setSelected(part.techHidden);
		hiddenBox.setSize(20, 20);
		hiddenBox.setLocation(240, 3);
		hiddenBox.setEnabled(editable);
		if(editable) {
			hiddenBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					part.techHidden = hiddenBox.isSelected();
				}
			});
		}
		this.add(hiddenBox);
		
		entryCostField = new JTextField();
		entryCostField.setText(Double.toString(part.entryCost));
		entryCostField.setBorder(BorderFactory.createEmptyBorder(2, 4, 3, 4));
		entryCostField.setSize(60, 20);
		entryCostField.setLocation(293, 3);
		entryCostField.setEnabled(editable);
		if(editable) {
			entryCostField.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent e) {
					try {
						entryCostField.setBackground(NORMAL_COLOR);
						part.entryCost = Double.parseDouble(entryCostField.getText());
					} catch(NumberFormatException e1) {
						entryCostField.setBackground(INVALID_COLOR);
					}
				}
				
				@Override
				public void insertUpdate(DocumentEvent e) {
					changedUpdate(e);
				}
				
				@Override
				public void removeUpdate(DocumentEvent e) {
					changedUpdate(e);
				}
			});
		}
		this.add(entryCostField);
	
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public Part getPart() {
		return part;
	}
}
