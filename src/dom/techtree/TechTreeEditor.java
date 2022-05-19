package dom.techtree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import dom.techtree.data.Node;
import dom.techtree.data.Part;
import dom.techtree.data.TechTree;
import dom.techtree.gui.SetupDialog;
import dom.techtree.gui.NodeInfoPanel;
import dom.techtree.gui.PartListPanel;
import dom.techtree.gui.PartSelectDialog;
import dom.techtree.gui.TechTreePanel;

@SuppressWarnings("serial")
public class TechTreeEditor extends JFrame {
	private JMenuItem setupMenuItem, importMenuItem, exportMenuItem;
	
	private TechTreePanel treePanel;
	private NodeInfoPanel nodeInfoPanel;
	private JButton addPartButton;
	private PartListPanel partListPanel;
	
	private SetupDialog setupDialog;
	private PartSelectDialog partSelectDialog;
	private static JFileChooser exportChooser;
	
	private Node selectedNode;
	
	public TechTreeEditor() {
		super();
		Persistent.load();
		initGUI();
		initListeners();
		
		if(Persistent.gameDataDirectory == null) {
			setupDialog.setVisible(true);
		}
	}
	
	private void initGUI() {
		// Set look-and-feel to get rid of ugly Java default
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(Persistent.windowSize == null ? new Dimension(800, 600) : Persistent.windowSize);
		this.setLocation(Persistent.windowLocation == null ? new Point(0, 0) : Persistent.windowLocation);
		this.setTitle("Tech Tree Editor");
		this.setIconImage(IconManager.LOGO);
		this.setLayout(new BorderLayout());
		
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		JMenu editMenu = new JMenu("Edit");
		JMenu helpMenu = new JMenu("Help");
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(helpMenu);
		
		setupMenuItem = new JMenuItem("KSP Directory Setup");
		importMenuItem = new JMenuItem("Import");
		exportMenuItem = new JMenuItem("Export");
		fileMenu.add(setupMenuItem);
		fileMenu.add(importMenuItem);
		fileMenu.add(exportMenuItem);
		
		editMenu.add(new JMenuItem("Undo (TODO)"));
		editMenu.add(new JMenuItem("Redo (TODO)"));
		editMenu.addSeparator();
		editMenu.add(new JMenuItem("View Part List (TODO)"));
		editMenu.add(new JMenuItem("View Node List (TODO)"));
		editMenu.add(new JMenuItem("View LOC Definitions (TODO)"));
		
		helpMenu.add(new JMenuItem("About (TODO)"));
		
		treePanel = new TechTreePanel() {
			@Override
			public void onSelect(Node node) {
				selectedNode = node;
				nodeInfoPanel.setNode(node);
				addPartButton.setEnabled(true);
				if(Persistent.currentTree != null) {
					partListPanel.setPartList(Persistent.currentTree.getPartList(node));
				}
			}
			
			@Override
			public void onDeselect(Node node) {
				selectedNode = null;
				nodeInfoPanel.setNode(null);
				addPartButton.setEnabled(false);
				if(Persistent.currentTree != null) {
					partListPanel.setPartList(new ArrayList<Part>(Persistent.currentTree.getPartList()));
				}
			}
		};
		treePanel.setPreferredSize(new Dimension(600, 600));
		treePanel.setTechTree(Persistent.currentTree);
		this.add(treePanel, BorderLayout.CENTER);
		
		JPanel sidePanel = new JPanel();
		sidePanel.setPreferredSize(new Dimension(400, 600));
		sidePanel.setLayout(new BorderLayout());
		sidePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.add(sidePanel, BorderLayout.LINE_END);
		
		nodeInfoPanel = new NodeInfoPanel();
		sidePanel.add(nodeInfoPanel, BorderLayout.PAGE_START);
		
		JLabel temp = new JLabel(" Part List");
		temp.setSize(100, 20);
		temp.setLocation(0, 204);
		temp.setFont(temp.getFont().deriveFont(Font.BOLD));
		nodeInfoPanel.add(temp);
		
		addPartButton = new JButton("+");
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
		
		partListPanel = new PartListPanel();
		if(Persistent.currentTree != null) {
			partListPanel.setPartList(new ArrayList<Part>(Persistent.currentTree.getPartList()));
		}
		sidePanel.add(partListPanel, BorderLayout.CENTER);
		
		this.setVisible(true);
		
		setupDialog = new SetupDialog(this);
		
		partSelectDialog = new PartSelectDialog(this);
		
		exportChooser = new JFileChooser();
		exportChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	}
	
	private void initListeners() {
		JFrame thisFrame = this;
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == setupMenuItem) {
					setupDialog.setVisible(true);
				} else if(e.getSource() == importMenuItem) {
					//importDialog.setVisible(true);
				} else if(e.getSource() == exportMenuItem) {
					if(exportChooser.showOpenDialog(thisFrame) == JFileChooser.APPROVE_OPTION) {
			            try {
			            	TechTree stockTree = TechTreeIO.readAll(Persistent.gameDataDirectory);
							TechTreeIO.write(Persistent.currentTree, stockTree, exportChooser.getSelectedFile());
						} catch (IOException e1) {
							e1.printStackTrace();
						}
			        }
				} else if(e.getSource() == addPartButton) {
					partSelectDialog.setPartList(Persistent.currentTree.getPartList("None"));
					List<Part> result = partSelectDialog.showSelectDialog();
					if(result != null) {
						for(Part part : result) {
							part.techRequired = selectedNode.id;
							partListPanel.addPart(part);
						}
					}
				}
			}
		};
		setupMenuItem.addActionListener(actionListener);
		importMenuItem.addActionListener(actionListener);
		exportMenuItem.addActionListener(actionListener);
		addPartButton.addActionListener(actionListener);
		
		WindowAdapter windowAdapter = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(e.getSource() == thisFrame) {
					Persistent.windowSize = thisFrame.getSize();
					Persistent.windowLocation = thisFrame.getLocation();
					Persistent.save();
				} else if(e.getSource() == setupDialog) {
					treePanel.setTechTree(Persistent.currentTree);
				}
			}
		};
		this.addWindowListener(windowAdapter);
		setupDialog.addWindowListener(windowAdapter);
	}
	
	public static void main(String[] args) {
		new TechTreeEditor();
	}
}
