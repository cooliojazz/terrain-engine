package com.up.terrainengine.gui;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.up.pe.input.MouseFreeTrackingCameraAdapter;
import com.up.pe.math.Point2D;
import com.up.pe.math.Point3D;
import com.up.pe.render.DisplayManager;
import com.up.pe.render.camera.FreeTrackingCamera;
import com.up.pe.render.event.RenderInterceptorAdapter;
import com.up.pe.render.light.PointLight;
import com.up.terrainengine.gui.GraphDisplay.Linking;
import com.up.terrainengine.operator.operators.ConvertToImage;
import com.up.terrainengine.operator.Operator;
import com.up.terrainengine.operator.terminal.Terminal;
import com.up.terrainengine.operator.terminal.TerminalDefinition;
import com.up.terrainengine.operator.terminal.TerminalMode;
//import com.up.terrainengine.operator.operators.ConvertToMesh;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Ricky
 */
//public class GraphNodeDisplay extends Panel {
@JsonAutoDetect(isGetterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class GraphNodeDisplay extends Component {
    
	@JsonProperty
    private Operator op;
    private Color bgColor = Color.GRAY;
    private Terminal selected;
    private boolean showLabels = false;
    private int height;

	@JsonCreator
    public GraphNodeDisplay(Operator op) {
        this.op = op;
        
        height = Math.max(op.getInputs().size(), op.getOutputs().size()) * 20 + 10;
        setSize(120, height);
        
        NodeMover nm = new NodeMover();
        addMouseListener(nm);
        addMouseMotionListener(nm);
        
    }

	@Override
	@JsonGetter
	public Point getLocation() {
		return super.getLocation();
	}

	@Override
	@JsonSetter
	public void setLocation(Point p) {
		super.setLocation(p);
	}

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(120, height);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(120, height);
    }
    
    @Override
    public void paint(Graphics g) {
        g.setColor(bgColor);
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
        g.setColor(Color.BLACK);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);
        g.setColor(op.needsUpdate() ? Color.RED : Color.GREEN);
        g.fillRect(getWidth() / 2 - 15, 5, 10, 5);
        g.setColor(op.getLastResult() ? Color.GREEN : Color.RED);
        g.fillRect(getWidth() / 2 + 15, 5, 10, 5);
        g.setColor(Color.BLACK);
        g.drawString(op.getName(), getWidth() / 2 - g.getFontMetrics().stringWidth(op.getName()) / 2, getHeight() / 2 + 10);
        List<Terminal> inputs = op.getInputs();
        for (int i = 0; i < inputs.size(); i++) {
            Terminal t = inputs.get(i);
            if (t == selected) {
                g.setColor(Color.BLUE);
                g.fillArc(-5, 10 + i * 20, 11, 11, -90, 180);
                g.setColor(Color.YELLOW);
                g.drawArc(-5, 10 + i * 20, 10, 10, -90, 180);
            } else {
                g.setColor(Color.BLUE.darker());
                g.fillArc(-5, 10 + i * 20, 11, 11, -90, 180);
                g.setColor(Color.BLACK);
                g.drawArc(-5, 10 + i * 20, 10, 10, -90, 180);
            }
            if (showLabels) {
                g.setColor(Color.BLACK);
                g.drawString(t.getDefinition().getDescription(), -g.getFontMetrics().stringWidth(t.getDefinition().getDescription()) - 5, 15 + i * 20);
            }
        }
        List<Terminal> outputs = op.getOutputs();
        for (int i = 0; i < outputs.size(); i++) {
            Terminal t = outputs.get(i);
            if (t == selected) {
                g.setColor(Color.GREEN);
                g.fillArc(getWidth() - 6, 10 + i * 20, 11, 11, 90, 180);
                g.setColor(Color.YELLOW);
                g.drawArc(getWidth() - 6, 10 + i * 20, 10, 10, 90, 180);
            } else {
                g.setColor(Color.GREEN.darker());
                g.fillArc(getWidth() - 6, 10 + i * 20, 11, 11, 90, 180);
                g.setColor(Color.BLACK);
                g.drawArc(getWidth() - 6, 10 + i * 20, 10, 10, 90, 180);
            }
            if (showLabels) {
                g.setColor(Color.BLACK);
                g.drawString(t.getDefinition().getDescription(), getWidth() + 5, 15 + i * 20);
            }
        }
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    public Operator getOperator() {
        return op;
    }
    
    public boolean containsTerminal(Terminal t) {
        return op.getTerminals().contains(t);
    }
    
    public Point getTerminalPosition(Terminal t) {
        Point ret = null;
        List<Terminal> inputs = op.getInputs();
        for (int i = 0; i < inputs.size(); i++) {
            if (t == inputs.get(i)) {
                ret = new Point(0, 15 + i * 20);
            }
        }
        List<Terminal> outputs = op.getOutputs();
        for (int i = 0; i < outputs.size(); i++) {
            if (t == outputs.get(i)) {
                ret = new Point(getWidth() - 1, 15 + i * 20);
            }
        }
        ret.translate(getX(), getY());
        return ret;
    }
    
    private PopupMenu getContextMenu() {
        PopupMenu menu = new PopupMenu("Operator Options");
        MenuItem edit = new MenuItem("Edit");
        edit.addActionListener(e -> edit());
        menu.add(edit);
        MenuItem delete = new MenuItem("Delete");
        delete.addActionListener(e -> delete());
        menu.add(delete);
        MenuItem run = new MenuItem("Run");
        run.addActionListener(e -> run());
        menu.add(run);
        MenuItem view = new MenuItem("View");
        view.addActionListener(e -> view());
        menu.add(view);
        return menu;
    }
    
    private void edit() {
//        final String initProps = op.getProperties().getJson();
        PropertiesEditor editor = new PropertiesEditor(op, null, true, changed -> {
//                if (!op.getProperties().getJson().equals(initProps)) {op.changed(); repaint();}
                if (changed) {
					op.changed();
					repaint();
				}
            });
        editor.setVisible(true);
    }
    
    private void delete() {
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this operator?", "Confirm Deletion", JOptionPane.YES_NO_OPTION) == 0) {
            ((GraphDisplay)getParent()).removeNode(this);
        }
    }
    
    private void run() {
        Dialog d = new Dialog((Frame)null, "Running Operations");
        d.add(new Label("Please wait while operators are computing..."));
        d.pack();
        d.setVisible(true);
        op.update();
        for (Component c : getParent().getComponents()) c.repaint();
        d.setVisible(false);
        d.dispose();
    }
    
    private void view() {
        Thread newContext = new Thread(() -> {
            if (op.needsUpdate()) {
                run();
            }
            if (op instanceof ConvertToImage) {
                Dialog d = new Dialog((Frame)null);
				Panel p = new Panel(new BorderLayout());
				d.add(p);
				
                Canvas c = new Canvas() {
                    @Override
                    public void paint(Graphics g) {
                        g.fillRect(0, 0, getWidth(), getHeight());
						Image i = ((ConvertToImage)op).getImage();
                        g.drawImage(i, 0, 0, getWidth(), getHeight(), null);
                    }

					@Override
					public void update(Graphics g) {
						paint(g);
					}
                };
                Image i = ((ConvertToImage)op).getImage();
                c.setSize(i.getWidth(null), i.getHeight(null));
                p.add(c, BorderLayout.CENTER);
				
				Button b = new Button("Refresh");
				b.addActionListener(e -> {
						run();
						c.repaint();
					});
				p.add(b, BorderLayout.SOUTH);
				
                d.pack();
                d.setVisible(true);
                d.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            d.setVisible(false);
                        }
                    });
            }
//            if (op instanceof ConvertToMesh) {
////                        Toolkit.getDefaultToolkit().
//                Frame f = new Frame();
//                DisplayManager dm = new DisplayManager(new RenderInterceptorAdapter() {
//                    @Override
//                    public void preTransformation(DisplayManager dm, GL2 gl) {
//                    }
//
//                    @Override
//                    public void preRender(DisplayManager dm, GL2 gl) {
//                        dm.drawAxis(gl, 100);
//                    }
//
//                    @Override
//                    public void postRender(DisplayManager dm, GL2 gl) {
//                        dm.renderString(gl, Math.round(dm.getFPS() * 10) / 10d + "fps", Color.red, new Point2D(20, 60), 12);
//                    }
//                });
//                dm.addMesh(((ConvertToMesh)op).getMesh());
//                dm.addLight(new PointLight(new Point3D(0, 100, 0), new float[] {1f, 1f, 1f, 1f}, 10000));
//                dm.setRenderDistance(1000f);
//                dm.setAmbient(new float[] {0.25f, 0.25f, 0.25f, 1.0f});
//
//                GLCapabilities cap = new GLCapabilities(GLProfile.getDefault());
//        //        cap.setSampleBuffers(true);
//                GLCanvas c = dm.getCanvas(cap);
//                FreeTrackingCamera camera = new FreeTrackingCamera(Point3D::zeros);
//                dm.setCamera(camera);
//                new MouseFreeTrackingCameraAdapter(camera).addToComponent(c);
//                f.add(c);
//
////                    f.pack();
//                f.setSize(500, 500);
//                f.setVisible(true);
//                f.addWindowListener(new WindowAdapter() {
//                        @Override
//                        public void windowClosing(WindowEvent e) {
//                            f.setVisible(false);
//                        }
//                    });
//                dm.startDefaultRenderLoop();
//            }
        });
        newContext.start();
    }
    
    private class NodeMover extends MouseAdapter {

        private Point last;
        private boolean moving = false;
        
        @Override
        public void mouseDragged(MouseEvent e) {
            GraphDisplay parent = (GraphDisplay)getParent();
            if (moving) {
                int newX = getX() + e.getX() - (int)last.getX();
                if (newX < 0) newX = 0;
                if (newX + getWidth() > getParent().getWidth()) newX = getParent().getWidth() - getWidth();
                int newY = getY() + e.getY() - (int)last.getY();
                if (newY < 0) newY = 0;
                if (newY + getHeight() > getParent().getHeight()) newY = getParent().getHeight() - getHeight();
                setLocation(newX, newY);
                repaint();
                getParent().repaint();
            } else if (parent.linking != null) {
                showLabels = true;
                Terminal oldSelected = selected;
                selected = findTerminalFor(e.getPoint());
                if (oldSelected != selected) {
                    if (selected != null) {
                        if ((parent.linking.start.getDefinition().getMode() == TerminalMode.INPUT && selected.getDefinition().getMode() == TerminalMode.OUTPUT) ||
								(parent.linking.start.getDefinition().getMode() == TerminalMode.OUTPUT && selected.getDefinition().getMode() == TerminalMode.INPUT)) {
                            ((GraphDisplay)getParent()).linking.end = selected;
                        } else {
                            selected = null;
                        }
                    }
                }
                repaint();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == 1) {
                if (findTerminalFor(e.getPoint()) != null) {
                    findTerminalFor(e.getPoint()).unlink();
                }
                last = e.getPoint();
                if (selected == null) {
                    moving = true;
                } else {
                    ((GraphDisplay)getParent()).linking = new Linking(selected, GraphNodeDisplay.this);
                }
            } else if (e.getButton() == 3) {
                PopupMenu menu = getContextMenu();
                getParent().add(menu);
                menu.show(GraphNodeDisplay.this, e.getX(), e.getY());
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            GraphDisplay parent = (GraphDisplay)getParent();
            last = null;
            if (moving) {
                moving = false;
            } else if (parent.linking != null) {
                if (parent.linking.end != null) {
                    if (parent.linking.start.getDefinition().getMode() == TerminalMode.OUTPUT) {
                        parent.linking.start.linkTo(parent.linking.end);
                    } else {
                        parent.linking.end.linkTo(parent.linking.start);
                    }
                }
                parent.linking = null;
                parent.repaint();
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 && e.getButton() == 1) {
                edit();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            selected = findTerminalFor(e.getPoint());
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            showLabels = true;
            repaint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            selected = null;
            showLabels = false;
            repaint();
        }
        
        private Terminal findTerminalFor(Point p) {
            List<Terminal> inputs = op.getInputs();
            for (int i = 0; i < inputs.size(); i++) {
                if (new Rectangle(0, i * 20 + 10, 5, 10).contains(p)) return inputs.get(i);
            }
            List<Terminal> outputs = op.getOutputs();
            for (int i = 0; i < outputs.size(); i++) {
                if (new Rectangle(getWidth() - 5, i * 20 + 10, 5, 10).contains(p)) return outputs.get(i);
            }
            return null;
        }
    }
}
