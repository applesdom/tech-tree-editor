package dom.techtree.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import dom.techtree.IconManager;
import dom.techtree.LocalizationManager;
import dom.techtree.Persistent;
import dom.techtree.data.Node;
import dom.techtree.data.Part;

public class NodeInfoPanel extends JPanel {
	private static final Color NORMAL_FIELD_COLOR = new Color(255, 255, 255),
			   				   INVALID_FIELD_COLOR = new Color(255, 192, 192),
			   				   LOC_FIELD_COLOR = new Color(255, 255, 160);
	
	private JLabel iconLabel;
	private JTextField titleField, costField, scaleField, nodeNameField, iconField, idField;
	private JTextArea descriptionArea;
	private JCheckBox hideEmptyBox, anyToUnlockBox;
	
	private Node selectedNode;
	
	public NodeInfoPanel() {
		super();
		initGUI();
		initListeners();
	}
	
	private void initGUI() {
		this.setPreferredSize(new Dimension(390, 229));
		this.setMinimumSize(new Dimension(390, 229));
		this.setMaximumSize(new Dimension(390, 229));
		this.setLayout(null);
		
		iconLabel = new JLabel();
		iconLabel.setSize(64, 64);
		iconLabel.setLocation(0, 0);
		this.add(iconLabel);
		
		JLabel iconBackLabel = new JLabel(new ImageIcon(IconManager.BLANK_NODE));
		iconBackLabel.setSize(64, 64);
		iconBackLabel.setLocation(0, 0);
		this.add(iconBackLabel);
		
		// Shared internal border for all text fields (prevents clipping on certain OS)
		Border textFieldBorder = BorderFactory.createEmptyBorder(2, 4, 3, 4);
		
		titleField = new JTextField();
		titleField.setSize(321, 20);
		titleField.setLocation(69, 0);
		titleField.setBorder(textFieldBorder);
		this.add(titleField);
		
		iconField = new JTextField();
		iconField.setBorder(textFieldBorder);
		JComponent temp = new JPanel();
		temp.setSize(321, 20);
		temp.setLocation(69, 22);
		temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));
		temp.add(new JLabel("icon = "));
		temp.add(iconField);
		this.add(temp);
		
		idField = new JTextField();
		idField.setBorder(textFieldBorder);
		temp = new JPanel();
		temp.setSize(321, 20);
		temp.setLocation(69, 44);
		temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));
		temp.add(new JLabel("id = "));
		temp.add(idField);
		this.add(temp);
		
		descriptionArea = new JTextArea();
		descriptionArea.setSize(390, 80);
		descriptionArea.setLocation(0, 69);
		descriptionArea.setBorder(textFieldBorder);
		descriptionArea.setFont(titleField.getFont());
		descriptionArea.setLineWrap(true);
		this.add(descriptionArea);
		
		costField = new JTextField();
		costField.setBorder(textFieldBorder);
		temp = new JPanel();
		temp.setSize(100, 20);
		temp.setLocation(0, 154);
		temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));
		temp.add(new JLabel("cost = "));
		temp.add(costField);
		this.add(temp);
		
		scaleField = new JTextField();
		scaleField.setBorder(textFieldBorder);
		temp = new JPanel();
		temp.setSize(100, 20);
		temp.setLocation(140, 154);
		temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));
		temp.add(new JLabel("scale = "));
		temp.add(scaleField);
		this.add(temp);
		
		hideEmptyBox = new JCheckBox("hideEmpty");
		hideEmptyBox.setSize(110, 20);
		hideEmptyBox.setLocation(280, 154);
		this.add(hideEmptyBox);
		
		anyToUnlockBox = new JCheckBox("anyToUnlock");
		anyToUnlockBox.setSize(110, 20);
		anyToUnlockBox.setLocation(280, 179);
		this.add(anyToUnlockBox);
		
		nodeNameField = new JTextField();
		nodeNameField.setBorder(textFieldBorder);
		temp = new JPanel();
		temp.setSize(275, 20);
		temp.setLocation(0, 179);
		temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));
		temp.add(new JLabel("nodeName = "));
		temp.add(nodeNameField);
		this.add(temp);
		
		// Call removeNode() to set background colors and disable components
		removeNode();
	}
	
	private void initListeners() {
		MouseAdapter locMouseAdapter = new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if(e.getSource() == titleField) {
					if(selectedNode != null && LocalizationManager.hasTranslation(selectedNode.title)) {
						titleField.setText(selectedNode.title);
						titleField.setBackground(NORMAL_FIELD_COLOR);
					}
				} else if(e.getComponent() == descriptionArea) {
					if(selectedNode != null && LocalizationManager.hasTranslation(selectedNode.description)) {
						descriptionArea.setText(selectedNode.description);
						descriptionArea.setBackground(NORMAL_FIELD_COLOR);
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if(e.getComponent() == titleField) {
					if(selectedNode != null && LocalizationManager.hasTranslation(selectedNode.title) && !e.getComponent().isFocusOwner()) {
						titleField.setBackground(LOC_FIELD_COLOR);
						titleField.setText(LocalizationManager.translate(selectedNode.title));
					}
				} else if(e.getComponent() == descriptionArea) {
					if(selectedNode != null && LocalizationManager.hasTranslation(selectedNode.description) && !e.getComponent().isFocusOwner()) {
						descriptionArea.setBackground(LOC_FIELD_COLOR);
						descriptionArea.setText(LocalizationManager.translate(selectedNode.description));
					}
				}
			}
		};
		titleField.addMouseListener(locMouseAdapter);
		descriptionArea.addMouseListener(locMouseAdapter);
				
		FocusListener locFocusListener = new FocusListener() {
			public void focusGained(FocusEvent e) {}

			@Override
			public void focusLost(FocusEvent e) {
				if(e.getComponent() == titleField) {
					if(selectedNode != null && LocalizationManager.hasTranslation(selectedNode.title)) {
						titleField.setBackground(LOC_FIELD_COLOR);
						titleField.setText(LocalizationManager.translate(selectedNode.title));
					}
				} else if(e.getComponent() == descriptionArea) {
					if(selectedNode != null && LocalizationManager.hasTranslation(selectedNode.description)) {
						descriptionArea.setBackground(LOC_FIELD_COLOR);
						descriptionArea.setText(LocalizationManager.translate(selectedNode.description));
					}
				}
			}
		};
		titleField.addFocusListener(locFocusListener);
		descriptionArea.addFocusListener(locFocusListener);
		
		DocumentListener documentListener = new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if(selectedNode == null) {
					return;
				} else if(e.getDocument() == titleField.getDocument()) {
					if(titleField.getBackground().equals(NORMAL_FIELD_COLOR)) {
						selectedNode.title = titleField.getText();
					}
				} else if(e.getDocument() == iconField.getDocument()) {
					selectedNode.icon = iconField.getText();
					Image icon = IconManager.get(selectedNode.icon);
					if(icon == null) {
						icon = IconManager.BLANK_NODE;
					}
					iconLabel.setIcon(new ImageIcon(icon.getScaledInstance(iconLabel.getWidth(),
																		   iconLabel.getHeight(),
																		   Image.SCALE_SMOOTH)));
				} else if(e.getDocument() == idField.getDocument()) {
					Node conflictNode = Persistent.currentTree.getNodeByID(idField.getText());
					if(conflictNode == null || conflictNode == selectedNode) {
						for(Part part : Persistent.currentTree.getPartList(selectedNode)) {
							part.techRequired = idField.getText();
						}
						selectedNode.id = idField.getText();
						idField.setBackground(NORMAL_FIELD_COLOR);
					} else {
						idField.setBackground(INVALID_FIELD_COLOR);
					}
				} else if(e.getDocument() == descriptionArea.getDocument()) {
					if(descriptionArea.getBackground().equals(NORMAL_FIELD_COLOR)) {
						selectedNode.description = descriptionArea.getText();
					}
				} else if(e.getDocument() == costField.getDocument()) {
					try {
						costField.setBackground(NORMAL_FIELD_COLOR);
						selectedNode.cost = Double.parseDouble(costField.getText());
					} catch(NumberFormatException e1) {
						costField.setBackground(INVALID_FIELD_COLOR);
					}
				} else if(e.getDocument() == scaleField.getDocument()) {
					try {
						scaleField.setBackground(NORMAL_FIELD_COLOR);
						selectedNode.scale = Double.parseDouble(scaleField.getText());
					} catch(NumberFormatException e1) {
						scaleField.setBackground(INVALID_FIELD_COLOR);
					}
				} else if(e.getDocument() == nodeNameField.getDocument()) {
					selectedNode.nodeName = nodeNameField.getText();
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
		};
		titleField.getDocument().addDocumentListener(documentListener);
		iconField.getDocument().addDocumentListener(documentListener);
		idField.getDocument().addDocumentListener(documentListener);
		descriptionArea.getDocument().addDocumentListener(documentListener);
		costField.getDocument().addDocumentListener(documentListener);
		scaleField.getDocument().addDocumentListener(documentListener);
		nodeNameField.getDocument().addDocumentListener(documentListener);
		
		ActionListener checkBoxListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selectedNode == null) {
					return;
				} if(e.getSource() == hideEmptyBox) {
					selectedNode.hideEmpty = hideEmptyBox.isSelected();
				} else if(e.getSource() == anyToUnlockBox) {
					selectedNode.anyToUnlock = anyToUnlockBox.isSelected();
				}
			}
		};
		hideEmptyBox.addActionListener(checkBoxListener);
		anyToUnlockBox.addActionListener(checkBoxListener);
	}
	
	public void setNode(Node node) {
		if(node == null) {
			removeNode();
			return;
		}
		
		Image icon = IconManager.get(node.icon);
		if(icon != null) {
			iconLabel.setIcon(new ImageIcon(
					icon.getScaledInstance(iconLabel.getWidth(), iconLabel.getHeight(), Image.SCALE_SMOOTH)));
		}
		
		if(LocalizationManager.hasTranslation(node.title)) {
			titleField.setBackground(LOC_FIELD_COLOR);
			titleField.setText(LocalizationManager.translate(node.title));
		} else {
			titleField.setText(node.title);
			titleField.setBackground(NORMAL_FIELD_COLOR);
		}
		if(LocalizationManager.hasTranslation(node.description)) {
			descriptionArea.setBackground(LOC_FIELD_COLOR);
			descriptionArea.setText(LocalizationManager.translate(node.description));
		} else {
			descriptionArea.setText(node.description);
			descriptionArea.setBackground(NORMAL_FIELD_COLOR);
		}
		costField.setText(Double.toString(node.cost));
		scaleField.setText(Double.toString(node.scale));
		hideEmptyBox.setSelected(node.hideEmpty);
		anyToUnlockBox.setSelected(node.anyToUnlock);
		nodeNameField.setText(node.nodeName);
		iconField.setText(node.icon);
		idField.setText(node.id);
		
		titleField.setEnabled(true);
		descriptionArea.setEnabled(true);
		costField.setEnabled(true);
		scaleField.setEnabled(true);
		hideEmptyBox.setEnabled(true);
		anyToUnlockBox.setEnabled(true);
		nodeNameField.setEnabled(true);
		iconField.setEnabled(true);
		idField.setEnabled(true);
			
		selectedNode = node;
		
		this.repaint();
	}
	
	private void removeNode() {
		selectedNode = null;
		
		iconLabel.setIcon(null);
		
		titleField.setText("");
		titleField.setBackground(NORMAL_FIELD_COLOR);
		iconField.setText("");
		iconField.setBackground(NORMAL_FIELD_COLOR);
		idField.setText("");
		idField.setBackground(NORMAL_FIELD_COLOR);
		descriptionArea.setText("");
		descriptionArea.setBackground(NORMAL_FIELD_COLOR);
		costField.setText("");
		costField.setBackground(NORMAL_FIELD_COLOR);
		scaleField.setText("");
		scaleField.setBackground(NORMAL_FIELD_COLOR);
		hideEmptyBox.setSelected(false);
		anyToUnlockBox.setSelected(false);
		nodeNameField.setText("");
		nodeNameField.setBackground(NORMAL_FIELD_COLOR);
		
		titleField.setEnabled(false);
		iconField.setEnabled(false);
		idField.setEnabled(false);
		descriptionArea.setEnabled(false);
		costField.setEnabled(false);
		scaleField.setEnabled(false);
		hideEmptyBox.setEnabled(false);
		anyToUnlockBox.setEnabled(false);
		nodeNameField.setEnabled(false);
		
		this.repaint();
	}
}
