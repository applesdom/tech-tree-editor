package dom.techtree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class Main {
	private static TechTree tree;
	
	private static JFrame frame;
	private static TechTreePanel treePanel;
	private static JPanel nodeInfoPanel;
	private static JLabel iconLabel;
	private static JTextField titleField, costField, nodeNameField, iconField, idField;
	private static JTextArea descriptionArea;
	private static JCheckBox hideEmptyBox, anyToUnlockBox;
	private static JFileChooser importChooser, exportChooser;
	
	public static void main(String[] args) {
		initGUI();
		
		try {
			tree = TechTreeIO.read(Main.class.getResourceAsStream("/TechTree.cfg"));
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
		frame.setPreferredSize(new Dimension(1000, 600));
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
						TechTreeIO.write(tree, exportChooser.getSelectedFile());
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
		frame.add(treePanel, BorderLayout.CENTER);
		
		JPanel sidePanel = new JPanel();
		sidePanel.setPreferredSize(new Dimension(300, 600));
		sidePanel.setLayout(new BorderLayout());
		sidePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.add(sidePanel, BorderLayout.LINE_END);
		
		nodeInfoPanel = new JPanel();
		nodeInfoPanel.setLayout(null);
		nodeInfoPanel.setPreferredSize(new Dimension(300, 204));
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
		titleField.setPreferredSize(new Dimension(221, 20));
		titleField.setSize(221, 20);
		titleField.setLocation(69, 0);
		nodeInfoPanel.add(titleField);
		
		iconField = new JTextField();
		JPanel temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));
		temp.add(new JLabel("icon = "));
		temp.add(iconField);
		temp.setSize(221, 20);
		temp.setLocation(69, 22);
		nodeInfoPanel.add(temp);
		
		idField = new JTextField();
		temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));
		temp.add(new JLabel("id = "));
		temp.add(idField);
		temp.setSize(221, 20);
		temp.setLocation(69, 44);
		nodeInfoPanel.add(temp);
		
		descriptionArea = new JTextArea();
		descriptionArea.setLineWrap(true);
		descriptionArea.setBorder(titleField.getBorder());
		descriptionArea.setFont(titleField.getFont());
		descriptionArea.setSize(290, 80);
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
		
		hideEmptyBox = new JCheckBox("hideEmpty");
		hideEmptyBox.setSize(110, 20);
		hideEmptyBox.setLocation(180, 154);
		nodeInfoPanel.add(hideEmptyBox);
		
		anyToUnlockBox = new JCheckBox("anyToUnlock");
		anyToUnlockBox.setSize(110, 20);
		anyToUnlockBox.setLocation(180, 179);
		nodeInfoPanel.add(anyToUnlockBox);
		
		nodeNameField = new JTextField();
		temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));
		temp.add(new JLabel("nodeName = "));
		temp.add(nodeNameField);
		temp.setSize(175, 20);
		temp.setLocation(0, 179);
		nodeInfoPanel.add(temp);
		
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
	}
	
	private static void updateNodeInfo(Node node) {
		if(node == null) {
			iconLabel.setIcon(null);
			
			titleField.setText("");
			descriptionArea.setText("");
			costField.setText("");
			hideEmptyBox.setSelected(false);
			anyToUnlockBox.setSelected(false);
			nodeNameField.setText("");
			iconField.setText("");
			idField.setText("");
			
			titleField.setEnabled(false);
			descriptionArea.setEnabled(false);
			costField.setEnabled(false);
			hideEmptyBox.setEnabled(false);
			anyToUnlockBox.setEnabled(false);
			nodeNameField.setEnabled(false);
			iconField.setEnabled(false);
			idField.setEnabled(false);
		} else {
			Image icon = IconManager.get(node.icon);
			if(icon != null) {
				iconLabel.setIcon(new ImageIcon(
						icon.getScaledInstance(iconLabel.getWidth(), iconLabel.getHeight(), Image.SCALE_SMOOTH)));
			}
			
			titleField.setText(node.title);
			descriptionArea.setText(node.description);
			costField.setText(Double.toString(node.cost));
			hideEmptyBox.setSelected(node.hideEmpty);
			anyToUnlockBox.setSelected(node.anyToUnlock);
			nodeNameField.setText(node.nodeName);
			iconField.setText(node.icon);
			idField.setText(node.id);
			
			titleField.setEnabled(true);
			descriptionArea.setEnabled(true);
			costField.setEnabled(true);
			hideEmptyBox.setEnabled(true);
			anyToUnlockBox.setEnabled(true);
			nodeNameField.setEnabled(true);
			iconField.setEnabled(true);
			idField.setEnabled(true);
		}
		frame.repaint();
		frame.revalidate();
	}
}
