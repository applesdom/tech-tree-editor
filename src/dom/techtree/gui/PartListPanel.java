package dom.techtree.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import dom.techtree.LocalizationManager;
import dom.techtree.Persistent;
import dom.techtree.data.Node;
import dom.techtree.data.Part;

@SuppressWarnings("serial")
public class PartListPanel extends JScrollPane {
	private JPanel innerPanel;
	
	public PartListPanel() {
		super();
		initGUI();
	}
	
	public void initGUI() {
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.PAGE_AXIS));
		this.setViewportView(innerPanel);
	}
	
	public void addPart(Part part) {
		PartPanel partPanel = new PartPanel(part, true, true);
		partPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					if(e.getClickCount() == 2) {
						innerPanel.remove(partPanel);
						part.techRequired = "None";
						innerPanel.revalidate();
						innerPanel.repaint();
					}
				}
			}
		});
		innerPanel.add(partPanel);
		innerPanel.revalidate();
		innerPanel.repaint();
	}
	
	public void setPartList(List<Part> partList) {
		innerPanel.removeAll();
		if(partList != null) {
			Map<String, List<Part>> nodePartMap = new HashMap<String, List<Part>>();
			for(Part part : partList) {
				List<Part> nodePartList = nodePartMap.get(part.techRequired);
				if(nodePartList == null) {
					nodePartList = new ArrayList<Part>();
					nodePartMap.put(part.techRequired, nodePartList);
				}
				nodePartList.add(part);
			}
			
			List<Part> noneList = nodePartMap.get("None");
			if(noneList != null) {
				for(Part part : noneList) {
					addPart(part);
				}
			}
			
			for(Entry<String, List<Part>> entry : nodePartMap.entrySet()) {
				String labelText = entry.getKey();
				if(Persistent.currentTree != null) {
					Node node = Persistent.currentTree.getNodeByID(entry.getKey());
					if(node != null) {
						labelText = Persistent.currentTree.getNodeByID(entry.getKey()).title;
						if(LocalizationManager.hasTranslation(labelText)) {
							labelText = LocalizationManager.translate(labelText);
						}
					}
				}
				innerPanel.add(new JLabel(labelText));
				for(Part part : entry.getValue()) {
					addPart(part);
				}
			}
		}
	}
}
