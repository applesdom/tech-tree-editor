package dom.techtree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import dom.techtree.data.Node;
import dom.techtree.data.Part;
import dom.techtree.data.TechTree;
import dom.techtree.gui.ImportDialog;
import dom.techtree.gui.PartPanel;
import dom.techtree.gui.PartSelectDialog;
import dom.techtree.gui.TechTreePanel;

public class Main {
	private static final Color NORMAL_COLOR = new JTextField().getBackground(),
			   				   INVALID_COLOR = new Color(255, 192, 192),
			   				   LOC_COLOR = new Color(255, 255, 160);
	
	private static TechTree tree, stockTree;
	private static Node selectedNode;
	
	private static JFrame frame;
	private static TechTreePanel treePanel;
	private static JPanel nodeInfoPanel, partListPanel;
	private static JLabel iconLabel;
	private static JTextField titleField, costField, scaleField, nodeNameField, iconField, idField;
	private static JButton addPartButton;
	private static JTextArea descriptionArea;
	private static JCheckBox hideEmptyBox, anyToUnlockBox;
	private static JMenuItem newMenuItem, importMenuItem, exportMenuItem;
	private static JFileChooser importChooser, exportChooser;
	private static PartSelectDialog partSelectDialog;
	private static ImportDialog importDialog;
	
	public static void main(String[] args) {
		initGUI();
		
		selectedNode = null;
		
		importDialog.setVisible(true);
	}
	
	@SuppressWarnings("serial")
	private static void initGUI() {
		// Set look-and-feel of GUI to get rid of ugly Java look
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// Set up GUI event listeners
		ActionListener menuItemActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == newMenuItem) {
					Persistent.currentTree.clear();
					treePanel.setTechTree(Persistent.currentTree);
					updateNodeInfo(null);
					frame.repaint();
				} else if(e.getSource() == importMenuItem) {
					if(importChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			            try {
			            	Persistent.currentTree = TechTreeIO.readAll(importChooser.getSelectedFile());
							treePanel.setTechTree(Persistent.currentTree);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
			        }
				} else if(e.getSource() == exportMenuItem) {
					if(exportChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			            try {
							TechTreeIO.write(Persistent.currentTree, stockTree, exportChooser.getSelectedFile());
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
			        }
				}
			}
		};
		
		MouseAdapter locMouseAdapter = new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if(e.getSource() == titleField) {
					if(selectedNode != null && LocalizationManager.hasTranslation(selectedNode.title)) {
						titleField.setText(selectedNode.title);
						titleField.setBackground(NORMAL_COLOR);
					}
				} else if(e.getComponent() == descriptionArea) {
					if(selectedNode != null && LocalizationManager.hasTranslation(selectedNode.description)) {
						descriptionArea.setText(selectedNode.description);
						descriptionArea.setBackground(NORMAL_COLOR);
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if(e.getComponent() == titleField) {
					if(selectedNode != null && LocalizationManager.hasTranslation(selectedNode.title) && !e.getComponent().isFocusOwner()) {
						titleField.setBackground(LOC_COLOR);
						titleField.setText(LocalizationManager.translate(selectedNode.title));
					}
				} else if(e.getComponent() == descriptionArea) {
					if(selectedNode != null && LocalizationManager.hasTranslation(selectedNode.description) && !e.getComponent().isFocusOwner()) {
						descriptionArea.setBackground(LOC_COLOR);
						descriptionArea.setText(LocalizationManager.translate(selectedNode.description));
					}
				}
			}
		};
		
		FocusListener locFocusListener = new FocusListener() {
			public void focusGained(FocusEvent e) {}

			@Override
			public void focusLost(FocusEvent e) {
				if(e.getComponent() == titleField) {
					if(selectedNode != null && LocalizationManager.hasTranslation(selectedNode.title)) {
						titleField.setBackground(LOC_COLOR);
						titleField.setText(LocalizationManager.translate(selectedNode.title));
					}
				} else if(e.getComponent() == descriptionArea) {
					if(selectedNode != null && LocalizationManager.hasTranslation(selectedNode.description)) {
						descriptionArea.setBackground(LOC_COLOR);
						descriptionArea.setText(LocalizationManager.translate(selectedNode.description));
					}
				}
			}
		};
		
		DocumentListener documentListener = new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if(selectedNode == null) {
					return;
				} else if(e.getDocument() == titleField.getDocument()) {
					if(titleField.getBackground().equals(NORMAL_COLOR)) {
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
						idField.setBackground(NORMAL_COLOR);
					} else {
						idField.setBackground(INVALID_COLOR);
					}
				} else if(e.getDocument() == descriptionArea.getDocument()) {
					if(descriptionArea.getBackground().equals(NORMAL_COLOR)) {
						selectedNode.description = descriptionArea.getText();
					}
				} else if(e.getDocument() == costField.getDocument()) {
					try {
						costField.setBackground(NORMAL_COLOR);
						selectedNode.cost = Double.parseDouble(costField.getText());
					} catch(NumberFormatException e1) {
						costField.setBackground(INVALID_COLOR);
					}
				} else if(e.getDocument() == scaleField.getDocument()) {
					try {
						scaleField.setBackground(NORMAL_COLOR);
						selectedNode.scale = Double.parseDouble(scaleField.getText());
					} catch(NumberFormatException e1) {
						scaleField.setBackground(INVALID_COLOR);
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
		
		ActionListener addPartListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				partSelectDialog.setPartList(Persistent.currentTree.getPartList("None"));
				List<Part> result = partSelectDialog.showSelectDialog();
				if(result != null) {
					for(Part part : result) {
						part.techRequired = selectedNode.id;
						partListPanel.add(new PartPanel(part));
					}
					partListPanel.revalidate();
					partListPanel.repaint();
				}
			}
		};
		
		frame = new JFrame("Tech Tree Editor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(IconManager.LOGO);
		frame.setLayout(new BorderLayout());
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		fileMenu.add(new JMenuItem("KSP Directory Setup (TODO)"));
		fileMenu.addSeparator();
		
		newMenuItem = new JMenuItem("New  ");
		newMenuItem.addActionListener(menuItemActionListener);
		fileMenu.add(newMenuItem);
		
		importMenuItem = new JMenuItem("Import");
		importMenuItem.addActionListener(menuItemActionListener);
		fileMenu.add(importMenuItem);
		
		exportMenuItem = new JMenuItem("Export");
		exportMenuItem.addActionListener(menuItemActionListener);
		fileMenu.add(exportMenuItem);
		
		JMenu editMenu = new JMenu("Edit  ");
		menuBar.add(editMenu);
		editMenu.add(new JMenuItem("Undo (TODO)"));
		editMenu.add(new JMenuItem("Redo (TODO)"));
		editMenu.addSeparator();
		editMenu.add(new JMenuItem("View Part List (TODO)"));
		
		JMenu helpMenu = new JMenu("Help  ");
		menuBar.add(helpMenu);
		helpMenu.add(new JMenuItem("About (TODO)"));
		
		treePanel = new TechTreePanel() {
			@Override
			public void onSelect(Node node) {
				selectedNode = node;
				updateNodeInfo(node);
			}
			
			@Override
			public void onDeselect(Node node) {
				selectedNode = null;
				updateNodeInfo(null);
			}
		};
		treePanel.setPreferredSize(new Dimension(600, 600));
		frame.add(treePanel, BorderLayout.CENTER);
		
		JPanel sidePanel = new JPanel();
		sidePanel.setPreferredSize(new Dimension(400, 600));
		sidePanel.setLayout(new BorderLayout());
		sidePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.add(sidePanel, BorderLayout.LINE_END);
		
		nodeInfoPanel = new JPanel();
		nodeInfoPanel.setLayout(null);
		nodeInfoPanel.setPreferredSize(new Dimension(390, 229));
		sidePanel.add(nodeInfoPanel, BorderLayout.PAGE_START);
		
		iconLabel = new JLabel();
		iconLabel.setSize(64, 64);
		iconLabel.setLocation(0, 0);
		nodeInfoPanel.add(iconLabel);
		
		JLabel iconBackLabel = new JLabel(new ImageIcon(IconManager.BLANK_NODE));
		iconBackLabel.setSize(64, 64);
		iconBackLabel.setLocation(0, 0);
		nodeInfoPanel.add(iconBackLabel);
		
		// Shared border for all text fields
		Border textFieldBorder = BorderFactory.createEmptyBorder(2, 4, 3, 4);
		
		titleField = new JTextField();
		titleField.addMouseListener(locMouseAdapter);
		titleField.addFocusListener(locFocusListener);
		titleField.getDocument().addDocumentListener(documentListener);
		titleField.setSize(321, 20);
		titleField.setLocation(69, 0);
		titleField.setBorder(textFieldBorder);
		nodeInfoPanel.add(titleField);
		
		iconField = new JTextField();
		iconField.setBorder(textFieldBorder);
		iconField.getDocument().addDocumentListener(documentListener);
		JComponent temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));
		temp.add(new JLabel("icon = "));
		temp.add(iconField);
		temp.setSize(321, 20);
		temp.setLocation(69, 22);
		nodeInfoPanel.add(temp);
		
		idField = new JTextField();
		idField.setBorder(textFieldBorder);
		idField.getDocument().addDocumentListener(documentListener);
		temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));
		temp.add(new JLabel("id = "));
		temp.add(idField);
		temp.setSize(321, 20);
		temp.setLocation(69, 44);
		nodeInfoPanel.add(temp);
		
		descriptionArea = new JTextArea();
		descriptionArea.setLineWrap(true);
		descriptionArea.setBorder(textFieldBorder);
		descriptionArea.setFont(titleField.getFont());
		descriptionArea.addMouseListener(locMouseAdapter);
		descriptionArea.addFocusListener(locFocusListener);
		descriptionArea.getDocument().addDocumentListener(documentListener);
		descriptionArea.setSize(390, 80);
		descriptionArea.setLocation(0, 69);
		nodeInfoPanel.add(descriptionArea);
		
		costField = new JTextField();
		costField.setBorder(textFieldBorder);
		costField.getDocument().addDocumentListener(documentListener);
		temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));
		temp.add(new JLabel("cost = "));
		temp.add(costField);
		temp.setSize(100, 20);
		temp.setLocation(0, 154);
		nodeInfoPanel.add(temp);
		
		scaleField = new JTextField();
		scaleField.setBorder(textFieldBorder);
		scaleField.getDocument().addDocumentListener(documentListener);
		temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));
		temp.add(new JLabel("scale = "));
		temp.add(scaleField);
		temp.setSize(100, 20);
		temp.setLocation(140, 154);
		nodeInfoPanel.add(temp);
		
		hideEmptyBox = new JCheckBox("hideEmpty");
		hideEmptyBox.addActionListener(checkBoxListener);
		hideEmptyBox.setSize(110, 20);
		hideEmptyBox.setLocation(280, 154);
		nodeInfoPanel.add(hideEmptyBox);
		
		anyToUnlockBox = new JCheckBox("anyToUnlock");
		anyToUnlockBox.addActionListener(checkBoxListener);
		anyToUnlockBox.setSize(110, 20);
		anyToUnlockBox.setLocation(280, 179);
		nodeInfoPanel.add(anyToUnlockBox);
		
		nodeNameField = new JTextField();
		nodeNameField.setBorder(textFieldBorder);
		nodeNameField.getDocument().addDocumentListener(documentListener);
		temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));
		temp.add(new JLabel("nodeName = "));
		temp.add(nodeNameField);
		temp.setSize(275, 20);
		temp.setLocation(0, 179);
		nodeInfoPanel.add(temp);
		
		temp = new JLabel(" Part List");
		temp.setFont(temp.getFont().deriveFont(Font.BOLD));
		temp.setSize(100, 20);
		temp.setLocation(0, 204);
		nodeInfoPanel.add(temp);
		
		addPartButton = new JButton("+");
		addPartButton.addActionListener(addPartListener);
		addPartButton.setSize(30, 20);
		addPartButton.setLocation(60, 204);
		nodeInfoPanel.add(addPartButton);
		
		temp = new JLabel("Hidden");
		temp.setFont(temp.getFont().deriveFont(Font.BOLD));
		temp.setSize(60, 20);
		temp.setLocation(230, 204);
		nodeInfoPanel.add(temp);
		
		temp = new JLabel("Entry Cost");
		temp.setFont(temp.getFont().deriveFont(Font.BOLD));
		temp.setSize(100, 20);
		temp.setLocation(290, 204);
		nodeInfoPanel.add(temp);
		
		partListPanel = new JPanel();
		partListPanel.setBackground(titleField.getBackground());
		partListPanel.setLayout(new BoxLayout(partListPanel, BoxLayout.PAGE_AXIS));
		JScrollPane scrollPane = new JScrollPane(partListPanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sidePanel.add(scrollPane, BorderLayout.CENTER);
		
		frame.pack();
		frame.setVisible(true);
		
		importChooser = new JFileChooser();
		importChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		File defaultFile = new File("C:\\Apps\\Steam\\steamapps\\common\\Kerbal Space Program\\Gamedata\\Squad");
		if(defaultFile.exists()) {
			importChooser.setCurrentDirectory(defaultFile);
		}
		
		exportChooser = new JFileChooser();
		exportChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		partSelectDialog = new PartSelectDialog(frame);
		
		importDialog = new ImportDialog(frame);
		importDialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				treePanel.setTechTree(Persistent.currentTree);
			}
		});
		
		updateNodeInfo(null);
	}
	
	private static void updateNodeInfo(Node node) {
		if(node == null) {
			iconLabel.setIcon(null);
			
			titleField.setText("");
			titleField.setBackground(costField.getBackground());
			descriptionArea.setText("");
			descriptionArea.setBackground(costField.getBackground());
			costField.setText("");
			scaleField.setText("");
			hideEmptyBox.setSelected(false);
			anyToUnlockBox.setSelected(false);
			nodeNameField.setText("");
			iconField.setText("");
			idField.setText("");
			
			titleField.setEnabled(false);
			descriptionArea.setEnabled(false);
			costField.setEnabled(false);
			scaleField.setEnabled(false);
			hideEmptyBox.setEnabled(false);
			anyToUnlockBox.setEnabled(false);
			nodeNameField.setEnabled(false);
			iconField.setEnabled(false);
			idField.setEnabled(false);
			addPartButton.setEnabled(false);
			
			if(Persistent.currentTree != null) {
				partListPanel.removeAll();
				for(Part part : Persistent.currentTree.getPartList()) {
					PartPanel partPanel = new PartPanel(part);
					partListPanel.add(partPanel);
				}
			}
		} else {
			Image icon = IconManager.get(node.icon);
			if(icon != null) {
				iconLabel.setIcon(new ImageIcon(
						icon.getScaledInstance(iconLabel.getWidth(), iconLabel.getHeight(), Image.SCALE_SMOOTH)));
			}
			
			if(LocalizationManager.hasTranslation(node.title)) {
				titleField.setBackground(LOC_COLOR);
				titleField.setText(LocalizationManager.translate(node.title));
			} else {
				titleField.setText(node.title);
				titleField.setBackground(NORMAL_COLOR);
			}
			if(LocalizationManager.hasTranslation(node.description)) {
				descriptionArea.setBackground(LOC_COLOR);
				descriptionArea.setText(LocalizationManager.translate(node.description));
			} else {
				descriptionArea.setText(node.description);
				descriptionArea.setBackground(NORMAL_COLOR);
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
			addPartButton.setEnabled(true);
			
			if(Persistent.currentTree != null) {
				partListPanel.removeAll();
				for(Part part : Persistent.currentTree.getPartList(node)) {
					PartPanel partPanel = new PartPanel(part, true, true);
					partPanel.addMouseListener(new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent e) {
							if(SwingUtilities.isLeftMouseButton(e)) {
								if(e.getClickCount() == 2) {
									partListPanel.remove(partPanel);
									part.techRequired = "None";
									partListPanel.revalidate();
									partListPanel.repaint();
								}
							}
						}
					});
					partListPanel.add(partPanel);
				}
			}
		}
		//nodeInfoPanel.revalidate();
		nodeInfoPanel.repaint();
		partListPanel.repaint();
	}
}
