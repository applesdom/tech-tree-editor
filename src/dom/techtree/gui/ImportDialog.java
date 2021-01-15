package dom.techtree.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import dom.techtree.IconManager;
import dom.techtree.LocalizationManager;
import dom.techtree.Persistent;
import dom.techtree.TechTreeIO;
import dom.techtree.data.TechTree;

@SuppressWarnings("serial")
public class ImportDialog extends JDialog {
	private JTextField pathField;
	private JButton browseButton, loadButton, okButton;
	private JCheckBox loadModsCheckBox;
	private JLabel outputLabel;
	private JFileChooser fileChooser;
	
	public ImportDialog(JFrame parent) {
		super(parent, "KSP Directory Setup", JDialog.ModalityType.DOCUMENT_MODAL);
		initGUI();
		initListeners();
	}
	
	private void initGUI() {
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		this.setLayout(null);
		this.getContentPane().setPreferredSize(new Dimension(700, 95));
		this.pack();
		Point p = this.getParent().getLocation();
		Dimension d = this.getParent().getSize();
		this.setLocation(p.x + (d.width - this.getWidth()) / 2, p.y + (d.height - this.getHeight()) / 2);
		this.setResizable(false);
		this.setVisible(false);

		JLabel label = new JLabel("Path to GameData folder in local KSP installation:");
		label.setSize(500, 20);
		label.setLocation(5, 10);
		this.add(label);
		
		pathField = new JTextField();
		pathField.setSize(625, 25);
		pathField.setLocation(5, 30);
		if(Persistent.gameDataDirectory == null) {
			pathField.setText("");
		} else {
			pathField.setText(Persistent.gameDataDirectory.getAbsolutePath());
		}
		this.add(pathField);
		
		browseButton = new JButton("Browse");
		browseButton.setSize(60, 25);
		browseButton.setLocation(635, 30);
		this.add(browseButton);
		
		loadModsCheckBox = new JCheckBox("Include mods?");
		loadModsCheckBox.setSize(140, 20);
		loadModsCheckBox.setLocation(500, 5);
		loadModsCheckBox.setSelected(true);
		this.add(loadModsCheckBox);
		
		outputLabel = new JLabel();
		outputLabel.setSize(525, 25);
		outputLabel.setLocation(0, 65);
		outputLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(outputLabel);
		
		loadButton = new JButton("Load");
		loadButton.setSize(80, 25);
		loadButton.setLocation(530, 65);
		this.add(loadButton);
		
		okButton = new JButton("Finish");
		okButton.setSize(80, 25);
		okButton.setLocation(615, 65);
		okButton.setEnabled(false);
		this.add(okButton);
		
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}
	
	private void initListeners() {
		JDialog thisDialog = this;
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == browseButton) {
					if(fileChooser.showOpenDialog(thisDialog) == JFileChooser.APPROVE_OPTION) {
						pathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
			        }
				} else if(e.getSource() == loadButton) {
					// Launch a new thread to allow for real-time updates to outputLabel
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							load();							
						}
					});
					thread.start();
				} else if(e.getSource() == okButton) {
					thisDialog.dispatchEvent(new WindowEvent(thisDialog, WindowEvent.WINDOW_CLOSING));
				}
			}
		};
		browseButton.addActionListener(actionListener);
		loadButton.addActionListener(actionListener);
		okButton.addActionListener(actionListener);
	}
	
	private void load() {
		outputLabel.setText("...");
		
		// Make sure this is the GameData directory
		File baseDir = new File(pathField.getText());
		if(!baseDir.getName().equals("GameData")) {
			outputLabel.setText("ERROR: This isn\'t the \'GameData\' directory!");
			return;
		}
		
		// Make sure specified directory exists
		if(!baseDir.exists()) {
			outputLabel.setText("ERROR: Directory does not exist!");
			return;
		}
		
		// Make sure requisite subfolders and files exist
		File partsDir = new File(baseDir, "Squad/Parts");
		File techTreeFile = new File(baseDir, "Squad/Resources/TechTree.cfg");
		File iconDir = new File(baseDir, "Squad/PartList/SimpleIcons/");
		File locFile = new File(baseDir, "Squad/Localization/dictionary.cfg/");
		if(!partsDir.exists() && !techTreeFile.exists() && !iconDir.exists() && !locFile.exists()) {
			outputLabel.setText("ERROR: \'GameData\' folder contents differ from expected! Could not load tech tree!");
			return;
		}
		
		Persistent.gameDataDirectory = baseDir;
		
		// Load parts + nodes
		TechTree tree = new TechTree();
		try {
			if(partsDir.exists()) {
				tree = TechTreeIO.readAll(tree, partsDir);
			}
			outputLabel.setText(String.format("Loaded %d parts...", tree.getPartCount()));
			
			if(techTreeFile.exists()) {
				tree = TechTreeIO.read(tree, techTreeFile);
			}
			outputLabel.setText(String.format("Loaded %d parts, %d nodes...", tree.getPartCount(), tree.getNodeCount()));
			
			if(loadModsCheckBox.isSelected()) {
				for(File file : baseDir.listFiles()) {
					tree = TechTreeIO.readAll(tree, file);
					outputLabel.setText(String.format("Loaded %d parts, %d nodes...", tree.getPartCount(), tree.getNodeCount()));
				}
			}
		} catch (IOException e1) {
			outputLabel.setText("ERROR: " + e1.getMessage());
			return;
		}
		
		// Load icons
		int iconCount = 0;
		if(iconDir.exists()) {
			iconCount += IconManager.load();
		}
		outputLabel.setText(String.format("Loaded %d parts, %d nodes, %d icons...", tree.getPartCount(), tree.getNodeCount(), iconCount));
		
		// Load localization definitions
		int locCount = 0;
		if(locFile.exists()) {
			locCount += LocalizationManager.load();
		}
		
		outputLabel.setText(String.format("Loaded %d parts, %d nodes, %d icons, and %d LOC values", tree.getPartCount(), tree.getNodeCount(), iconCount, locCount));
		okButton.setEnabled(true);
		Persistent.currentTree = tree;
	}
}
