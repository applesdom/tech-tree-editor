package dom.techtree;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

@SuppressWarnings("serial")
public class PartPanel extends JPanel {
	private static final Color NORMAL_COLOR = new JTextField().getBackground(),
							   INVALID_COLOR = new Color(255, 192, 192);
	
	private PartInfo part;
	private JCheckBox hiddenBox;
	private JTextField entryCostField;
	
	public PartPanel(PartInfo part) {
		this.part = part;
		
		this.setLayout(null);
		this.setBorder(BorderFactory.createRaisedBevelBorder());
		this.setPreferredSize(new Dimension(390, 27));
		this.setMinimumSize(new Dimension(390, 27));
		this.setMaximumSize(new Dimension(390, 27));
		
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
		this.add(hiddenBox);
		
		entryCostField = new JTextField();
		entryCostField.setText(Double.toString(part.entryCost));
		entryCostField.setSize(60, 20);
		entryCostField.setLocation(293, 3);
		this.add(entryCostField);
		
		hiddenBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				part.techHidden = hiddenBox.isSelected();
			}
		});
		
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
}
