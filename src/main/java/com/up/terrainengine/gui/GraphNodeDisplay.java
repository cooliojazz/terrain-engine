package com.up.terrainengine.gui;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.up.pe.event.MouseCameraAdapter;
import com.up.pe.math.Point2D;
import com.up.pe.math.Point3D;
import com.up.pe.render.DisplayManager;
import com.up.pe.render.Light;
import com.up.pe.render.camera.FreeTrackingCamera;
import com.up.terrainengine.gui.GraphDisplay.Linking;
import com.up.terrainengine.operator.operators.ConvertToImage;
import com.up.terrainengine.operator.Operator;
import com.up.terrainengine.operator.Properties;
import com.up.terrainengine.operator.Terminal;
import com.up.terrainengine.operator.Terminal.Mode;
import com.up.terrainengine.operator.operators.ConvertToMesh;
import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.*;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Ricky
 */
public class GraphNodeDisplay extends Panel {
    
    private Operator o;

    public GraphNodeDisplay(Operator o) {
        this.o = o;
        
        setSize(120, Math.max(o.getInputs().size(), o.getOutputs().size()) * 20 + 10);
        
        NodeMover nm = new NodeMover();
        addMouseListener(nm);
        addMouseMotionListener(nm);
        
//        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        
//        Panel inputs = new Panel();
//        inputs.setLayout(new BoxLayout(inputs, BoxLayout.Y_AXIS));
//        for (Terminal start : o.getTerminals()) {
//            if (start.getMode() == Mode.INPUT) inputs.add(new TerminalDisplay(start));
//        }
//        add(inputs);
//        
//        Label text = new Label(o.getClass().getSimpleName());
//        add(text);
//        cleanChild(text);
//        
//        Panel outputs = new Panel();
//        outputs.setLayout(new BoxLayout(outputs, BoxLayout.Y_AXIS));
//        for (Terminal start : o.getTerminals()) {
//            if (start.getMode() == Mode.OUTPUT) outputs.add(new TerminalDisplay(start));
//        }
//        add(outputs);
    }

    private Color c = Color.GRAY;
    private Terminal selected;
    
    @Override
    public void paint(Graphics g) {
        g.setColor(c);
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
        g.setColor(Color.BLACK);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);
        g.setColor(o.needsUpdate() ? Color.RED : Color.GREEN);
        g.fillRect(getWidth() / 2 - 15, 5, 10, 5);
        g.setColor(o.getLastResult() ? Color.GREEN : Color.RED);
        g.fillRect(getWidth() / 2 + 15, 5, 10, 5);
        g.setColor(Color.BLACK);
        g.drawString(o.getName(), getWidth() / 2 - g.getFontMetrics().stringWidth(o.getName()) / 2, getHeight() / 2 + 10);
        List<Terminal> inputs = o.getInputs();
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
        }
        List<Terminal> outputs = o.getOutputs();
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
        }
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    public Operator getOperator() {
        return o;
    }
    
    public boolean containsTerminal(Terminal t) {
        return o.getTerminals().contains(t);
    }
    
    public Point getTerminalPosition(Terminal t) {
        Point ret = null;
        List<Terminal> inputs = o.getInputs();
        for (int i = 0; i < inputs.size(); i++) {
            if (t == inputs.get(i)) {
                ret = new Point(0, 15 + i * 20);
            }
        }
        List<Terminal> outputs = o.getOutputs();
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
        final String initProps = o.getProperties().getJson();
        PropertiesEditor editor = new PropertiesEditor(o, null, true, () -> {
                if (!o.getProperties().getJson().equals(initProps)) {o.changed(); repaint();}
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
        o.update();
        for (Component c : getParent().getComponents()) c.repaint();
        d.setVisible(false);
        d.dispose();
    }
    
    private void view() {
        Thread newContext = new Thread(() -> {
            if (o.needsUpdate()) {
                run();
            }
            if (o instanceof ConvertToImage) {
                Image i = ((ConvertToImage)o).getImage();
                Dialog d = new Dialog((Frame)null);
                Canvas c = new Canvas() {
                    @Override
                    public void paint(Graphics g) {
                        g.fillRect(0, 0, getWidth(), getHeight());
                        g.drawImage(i, 0, 0, getWidth(), getHeight(), null);
                    }
                };
                c.setSize(i.getWidth(null), i.getHeight(null));
                d.add(c);
                d.pack();
                d.setVisible(true);
                d.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            d.setVisible(false);
                        }
                    });
            }
            if (o instanceof ConvertToMesh) {
//                        Toolkit.getDefaultToolkit().
                Frame f = new Frame();
                DisplayManager dm = new DisplayManager() {
                    @Override
                    public void preTranslation(GL2 gl) {
                    }

                    @Override
                    public void preRotation(GL2 gl) {
                    }

                    @Override
                    public void preRender(GL2 gl) {
                        drawAxis(gl, 100);
                    }

                    @Override
                    public void postRender(GL2 gl) {
                        renderString(Math.round(getFPS() * 10) / 10d + "fps", Color.red, new Point2D(20, 60));
                    }
                };
                dm.addMesh(((ConvertToMesh)o).getMesh());
                dm.addLight(new Light(new Point3D(0, 100, 0), new float[] {1f, 1f, 1f, 1f}, 10000));
                dm.zFar = 1000f;
                dm.setAmbient(new float[] {0.25f, 0.25f, 0.25f, 1.0f});

                GLCapabilities cap = new GLCapabilities(GLProfile.getDefault());
        //        cap.setSampleBuffers(true);
                GLCanvas c = dm.getCanvas(cap);
                FreeTrackingCamera camera = new FreeTrackingCamera(Point3D::zeros);
                dm.setCamera(camera);
                new MouseCameraAdapter(camera).addToComponent(c);
                f.add(c);

//                    f.pack();
                f.setSize(500, 500);
                f.setVisible(true);
                f.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            f.setVisible(false);
                        }
                    });
                dm.startDefaultRenderLoop();
            }
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
                if (e.getSource() == getParent()) {
                    Terminal oldSelected = selected;
                    selected = findTerminalFor(e.getPoint());
                    if (selected != null) {
                        if ((parent.linking.start.getMode() == Mode.INPUT && selected.getMode() == Mode.OUTPUT) || (parent.linking.start.getMode() == Mode.OUTPUT && selected.getMode() == Mode.INPUT)) {
                            ((GraphDisplay)getParent()).linking.end = selected;
                            if (oldSelected != selected) repaint();
                        } else {
                            selected = null;
                        }
                    }
                } else {
                    getParent().dispatchEvent(new MouseEvent(GraphNodeDisplay.this, e.getID(), e.getWhen(), e.getModifiers(), getX() + e.getX(), getY() + e.getY(), e.getClickCount(), e.isPopupTrigger(), e.getButton()));
                }
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
//            getParent().dispatchEvent(new MouseEvent(GraphNodeDisplay.this, e.getID(), e.getWhen(), e.getModifiers(), getX() + e.getX(), getY() + e.getY(), e.getClickCount(), e.isPopupTrigger(), e.getButton()));
            GraphDisplay parent = (GraphDisplay)getParent();
            last = null;
            if (moving) {
                moving = false;
            } else if (parent.linking != null) {
                if (parent.linking.end != null) {
                    if (parent.linking.start.getMode() == Mode.OUTPUT) {
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
            Terminal oldSelected = selected;
            selected = findTerminalFor(e.getPoint());
            if (oldSelected != selected) {
                repaint();
                getParent().repaint();
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            selected = null;
            repaint();
        }
        
        private Terminal findTerminalFor(Point p) {
            List<Terminal> inputs = o.getInputs();
            for (int i = 0; i < inputs.size(); i++) {
                if (new Rectangle(0, i * 20 + 10, 5, 10).contains(p)) return inputs.get(i);
            }
            List<Terminal> outputs = o.getOutputs();
            for (int i = 0; i < outputs.size(); i++) {
                if (new Rectangle(getWidth() - 5, i * 20 + 10, 5, 10).contains(p)) return outputs.get(i);
            }
            return null;
        }
    }
}
