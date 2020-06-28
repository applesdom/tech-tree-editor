package dom.techtree;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class PartPanel extends JPanel {
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
		
		JLabel titleLabel = new JLabel(" " + part.title);
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
	}
}
