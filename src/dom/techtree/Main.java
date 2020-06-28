package dom.techtree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class Main {
	private static TechTree tree, stockTree;
	
	private static JFrame frame;
	private static TechTreePanel treePanel;
	private static JPanel nodeInfoPanel, partListPanel;
	private static JLabel iconLabel;
	private static JTextField titleField, costField, scaleField, nodeNameField, iconField, idField;
	private static JTextArea descriptionArea;
	private static JCheckBox hideEmptyBox, anyToUnlockBox;
	private static JFileChooser importChooser, exportChooser;
	
	public static void main(String[] args) {
		initGUI();
		
		try {
			stockTree = TechTreeIO.readAll(new File("C:/Apps/Steam/steamapps/common/Kerbal Space Program/GameData/Squad"));
			tree = TechTreeIO.readAll(new File("C:/Apps/Steam/steamapps/common/Kerbal Space Program/GameData/Squad"));
			treePanel.setTechTree(tree);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("serial")
	private static void initGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		frame = new JFrame("Tech Tree Editor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(IconManager.LOGO);
		frame.setLayout(new BorderLayout());
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		JMenuItem newMenuItem = new JMenuItem("New");
		newMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tree.clear();
				treePanel.setTechTree(tree);
				updateNodeInfo(null);
				frame.repaint();
			}
		});
		fileMenu.add(newMenuItem);
		
		JMenuItem importMenuItem = new JMenuItem("Import");
		importMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		        if(importChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
		            try {
						tree = TechTreeIO.readAll(importChooser.getSelectedFile());
						treePanel.setTechTree(tree);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
		        }
			}
		});
		fileMenu.add(importMenuItem);
		
		JMenuItem exportMenuItem = new JMenuItem("Export");
		exportMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		        if(exportChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
		            try {
						TechTreeIO.write(tree, stockTree, exportChooser.getSelectedFile());
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
		        }
			}
		});
		fileMenu.add(exportMenuItem);
		
		treePanel = new TechTreePanel() {
			@Override
			public void onSelect(Node node) {
				updateNodeInfo(node);
			}
			
			@Override
			public void onDeselect(Node node) {
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
		
		titleField = new JTextField();
		titleField.setSize(321, 20);
		titleField.setLocation(69, 0);
		nodeInfoPanel.add(titleField);
		
		iconField = new JTextField();
		JComponent temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));
		temp.add(new JLabel("icon = "));
		temp.add(iconField);
		temp.setSize(321, 20);
		temp.setLocation(69, 22);
		nodeInfoPanel.add(temp);
		
		idField = new JTextField();
		temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));
		temp.add(new JLabel("id = "));
		temp.add(idField);
		temp.setSize(321, 20);
		temp.setLocation(69, 44);
		nodeInfoPanel.add(temp);
		
		descriptionArea = new JTextArea();
		descriptionArea.setLineWrap(true);
		descriptionArea.setBorder(titleField.getBorder());
		descriptionArea.setFont(titleField.getFont());
		descriptionArea.setSize(390, 80);
		descriptionArea.setLocation(0, 69);
		nodeInfoPanel.add(descriptionArea);
		
		costField = new JTextField();
		temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));
		temp.add(new JLabel("cost = "));
		temp.add(costField);
		temp.setSize(100, 20);
		temp.setLocation(0, 154);
		nodeInfoPanel.add(temp);
		
		scaleField = new JTextField();
		temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));
		temp.add(new JLabel("scale = "));
		temp.add(scaleField);
		temp.setSize(100, 20);
		temp.setLocation(140, 154);
		nodeInfoPanel.add(temp);
		
		hideEmptyBox = new JCheckBox("hideEmpty");
		hideEmptyBox.setSize(110, 20);
		hideEmptyBox.setLocation(280, 154);
		nodeInfoPanel.add(hideEmptyBox);
		
		anyToUnlockBox = new JCheckBox("anyToUnlock");
		anyToUnlockBox.setSize(110, 20);
		anyToUnlockBox.setLocation(280, 179);
		nodeInfoPanel.add(anyToUnlockBox);
		
		nodeNameField = new JTextField();
		temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));
		temp.add(new JLabel("nodeName = "));
		temp.add(nodeNameField);
		temp.setSize(275, 20);
		temp.setLocation(0, 179);
		nodeInfoPanel.add(temp);
		
		temp = new JLabel(" Part Title");
		temp.setFont(temp.getFont().deriveFont(Font.BOLD));
		temp.setSize(100, 20);
		temp.setLocation(0, 204);
		nodeInfoPanel.add(temp);
		
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
		
		updateNodeInfo(null);
	}
	
	private static void updateNodeInfo(Node node) {
		if(node == null) {
			iconLabel.setIcon(null);
			
			titleField.setText("");
			descriptionArea.setText("");
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
			
			if(tree != null) {
				partListPanel.removeAll();
				for(PartInfo part : tree.getPartList()) {
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
			
			titleField.setText(node.title);
			descriptionArea.setText(node.description);
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
			
			if(tree != null) {
				partListPanel.removeAll();
				for(PartInfo part : tree.getPartList(node)) {
					PartPanel partPanel = new PartPanel(part);
					partListPanel.add(partPanel);
				}
			}
		}
		//nodeInfoPanel.repaint();
		nodeInfoPanel.revalidate();
		partListPanel.revalidate();
	}
}
