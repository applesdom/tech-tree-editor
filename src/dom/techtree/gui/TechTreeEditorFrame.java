package dom.techtree.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import dom.techtree.IconManager;
import dom.techtree.Persistent;
import dom.techtree.TechTreeIO;
import dom.techtree.data.Node;
import dom.techtree.data.Part;
import dom.techtree.data.TechTree;

@SuppressWarnings("serial")
public class TechTreeEditorFrame extends JFrame {
	private JMenuItem importMenuItem, exportMenuItem;
	
	private TechTreePanel treePanel;
	private NodeInfoPanel nodeInfoPanel;
	private JButton addPartButton;
	private JPanel partListPanel;
	
	private ImportDialog importDialog;
	private PartSelectDialog partSelectDialog;
	private static JFileChooser exportChooser;
	
	private Node selectedNode;
	
	public TechTreeEditorFrame() {
		super();
		initGUI();
		initListeners();
		
		if(Persistent.gameDataDirectory == null) {
			importDialog.setVisible(true);
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
		
		importMenuItem = new JMenuItem("Import");
		exportMenuItem = new JMenuItem("Export");
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
					partListPanel.revalidate();
					partListPanel.repaint();
				}
			}
			
			@Override
			public void onDeselect(Node node) {
				selectedNode = null;
				nodeInfoPanel.setNode(null);
				addPartButton.setEnabled(false);
				if(Persistent.currentTree != null) {
					partListPanel.removeAll();
					for(Part part : Persistent.currentTree.getPartList()) {
						PartPanel partPanel = new PartPanel(part);
						partListPanel.add(partPanel);
					}
				}
				partListPanel.revalidate();
				partListPanel.repaint();
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
		
		partListPanel = new JPanel();
		partListPanel.setBackground(new JTextField().getBackground());
		partListPanel.setLayout(new BoxLayout(partListPanel, BoxLayout.PAGE_AXIS));
		JScrollPane scrollPane = new JScrollPane(partListPanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sidePanel.add(scrollPane, BorderLayout.CENTER);
		
		this.pack();
		
		importDialog = new ImportDialog(this);
		
		partSelectDialog = new PartSelectDialog(this);
		
		exportChooser = new JFileChooser();
		exportChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	}
	
	private void initListeners() {
		JFrame thisFrame = this;
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == importMenuItem) {
					importDialog.setVisible(true);
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
							partListPanel.add(new PartPanel(part));
						}
						partListPanel.revalidate();
						partListPanel.repaint();
					}
				}
			}
		};
		importMenuItem.addActionListener(actionListener);
		exportMenuItem.addActionListener(actionListener);
		addPartButton.addActionListener(actionListener);
		
		WindowAdapter windowAdapter = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(e.getSource() == importDialog) {
					treePanel.setTechTree(Persistent.currentTree);
				}
			}
		};
		importDialog.addWindowListener(windowAdapter);
	}
}
