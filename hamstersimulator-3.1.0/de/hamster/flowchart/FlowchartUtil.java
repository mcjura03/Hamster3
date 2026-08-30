package de.hamster.flowchart;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.hamster.flowchart.model.CommentObject;
import de.hamster.flowchart.model.ProcedureObject;
import de.hamster.flowchart.model.RenderObject;
import de.hamster.flowchart.model.StartStopObject;
import de.hamster.flowchart.model.command.ToolbarCommandObject;
import de.hamster.flowchart.model.decision.ToolbarDecisionObject;
import de.hamster.workbench.Utils;

public class FlowchartUtil {

	public static Boolean TRANSITIONMODE = false;
	public static BufferedImage ANHCORIMG = getImage("flowchart/anchor.png");

	public static List<RenderObject> getRenderObjects() {
		List<RenderObject> renderObjects = new LinkedList<RenderObject>();
		renderObjects.add(new StartStopObject("Start/Stop", null));
		renderObjects.add(new ToolbarCommandObject("Operation"));
		renderObjects.add(new ToolbarDecisionObject("Verzweigung"));
		renderObjects.add(new ProcedureObject("Unterprogramm", null));
		renderObjects.add(new CommentObject("Kommentar"));
		return renderObjects;
	}

	public static BufferedImage getImage(String name) {
		Image img = Utils.getIcon(name).getImage();
		BufferedImage buffImg = new BufferedImage(img.getWidth(null),
				img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics g = buffImg.getGraphics();
		g.drawImage(img, 0, 0, null);
		return buffImg;
	}

	/**
	 * generating unique ID from timestamp
	 * 
	 * @return
	 */
	public static int generateId() {
		Long longId = new Date().getTime();
		int shortId = (int) (longId.intValue());
		if (shortId < 0)
			shortId *= -1;
		return shortId;
	}

}
