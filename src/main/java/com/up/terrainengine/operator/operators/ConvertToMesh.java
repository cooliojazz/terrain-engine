package com.up.terrainengine.operator.operators;

import com.up.pe.math.Angle3D;
import com.up.pe.math.Point3D;
import com.up.pe.math.Triangle;
import com.up.pe.math.Vector3D;
import com.up.pe.mesh.Mesh;
import com.up.pe.mesh.MeshGrouper;
import com.up.pe.mesh.SimpleMesh;
import com.up.pe.render.Material;
import com.up.pe.render.Vertex;
import com.up.terrainengine.util.TypeReference;
import com.up.terrainengine.operator.Operator;
import com.up.terrainengine.operator.Properties;
import com.up.terrainengine.operator.Terminal;
import com.up.terrainengine.operator.Terminal.Mode;
import com.up.terrainengine.structures.Vector;
import com.up.terrainengine.structures.VectorMap;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Ricky
 */
public class ConvertToMesh extends Operator {
    
    private Terminal[] terminals = new Terminal[] {
            new Terminal<>(new TypeReference<VectorMap<Double>>() {}, this, Mode.INPUT, "Input")
        };
//    private ImageProperties props = new ImageProperties();
    private Mesh mesh;

    @Override
    public String getName() {
        return "Convert To Mesh";
    }

    @Override
    public List<Terminal> getTerminals() {
        return Arrays.asList(terminals);
    }

    @Override
    public boolean operate() {
        VectorMap<Double> in = (VectorMap<Double>)terminals[0].getState();
        int size = in.size();
        double[][] heightmap = new double[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                heightmap[x][y] = in.get(x, y).get() * 10;
            }
        }
        mesh = createMesh(heightmap);
        return true;
    }

    public Mesh getMesh() {
        return mesh;
    }
    
    @Override
    public Properties getProperties() {
        return new Properties();
    }

//    private class ImageProperties extends Properties {
//
//        public ImageProperties() {
//            declareProperty("size", new TypeReference<Integer>() {}, 4);
//        }
//        
//        public Integer getSize() {
//            return get("size");
//        }
//        
//        public void setValue(int s) {
//            set("size", s);
//        }
//    }
    
    private Vector3D getFaceNormal(double[][] heightmap, double x, double z) {
//        double h = 0.000000001;
//        double gx = (getSimplexPoint(x + h, z) - getSimplexPoint(x, z)) / h;
//        double gy = 1;
//        double gz = (getSimplexPoint(x, z + h) - getSimplexPoint(x, z)) / h;
        double gx = x + 1 < heightmap.length ? heightmap[(int)(x + 1)][(int)z] - heightmap[(int)x][(int)z] : 0;
        double gy = 1;
        double gz = z + 1 < heightmap.length ? heightmap[(int)x][(int)(z + 1)] - heightmap[(int)x][(int)z] : 0;
        
        return new Vector3D(gx, gy, gz).normalize();
//        return new Vector3D(0, 1, 0);
    }
    
    private SimpleMesh createMesh(double[][] heightmap) {
        int size = heightmap.length;
        MeshGrouper grouper = new MeshGrouper(new Point3D(-size / 2, 0, -size / 2), Angle3D.zeros(), Vector3D.ones());
        Material mat = new Material(new float[] {0.5f, 0.25f, 0.75f, 1.0f}, Material.white, Material.black, 0.0f, null);
        for (int x = 0; x < size - 1; x++) {
            for (int z = 0; z < size - 1; z++) {
                // Slope Normal
//                double h1 = heightmap[x][z];
//                double h2 = heightmap[x + 1][z];
//                double h3 = heightmap[x + 1][z + 1];
//                double h4 = heightmap[x][z + 1];
//                double h5 = (h1 + h2 + h3 + h4) / 4;
//                Vector3D n5 = getFaceNormal(heightmap, x + 0.5, z + 0.5);
//                
//                Vertex[] verts = new Vertex[12];
//                verts[0] = new Vertex(new Point3D(x, h1, z), getFaceNormal(heightmap, x, z));
//                verts[1] = new Vertex(new Point3D(x + 0.5, h5, z + 0.5), n5);
//                verts[2] = new Vertex(new Point3D(x + 1, h2, z), getFaceNormal(heightmap, x + 1, z));
//                
//                verts[3] = new Vertex(new Point3D(x + 1, h3, z + 1), getFaceNormal(heightmap, x + 1, z + 1));
//                verts[4] = new Vertex(new Point3D(x + 0.5, h5, z + 0.5), n5);
//                verts[5] = new Vertex(new Point3D(x, h4, z + 1), getFaceNormal(heightmap, x, z + 1));
//                
//                verts[6] = new Vertex(new Point3D(x, h4, z + 1), getFaceNormal(heightmap, x, z + 1));
//                verts[7] = new Vertex(new Point3D(x + 0.5, h5, z + 0.5), n5);
//                verts[8] = new Vertex(new Point3D(x, h1, z), getFaceNormal(heightmap, x, z));
//                
//                verts[9] = new Vertex(new Point3D(x + 1, h2, z), getFaceNormal(heightmap, x + 1, z));
//                verts[10] = new Vertex(new Point3D(x + 0.5, h5, z + 0.5), n5);
//                verts[11] = new Vertex(new Point3D(x + 1, h3, z + 1), getFaceNormal(heightmap, x + 1, z + 1));

                // 2 Triangle Height Normal
//                Point3D p1 = new Point3D(x, heightmap[x][z], z);
//                Point3D p2 = new Point3D(x + 1, heightmap[x + 1][z], z);
//                Point3D p3 = new Point3D(x + 1, heightmap[x + 1][z + 1], z + 1);
//                Point3D p4 = new Point3D(x, heightmap[x][z + 1], z + 1);
//                
//                Vector3D n1 = p1.vectorTo(p2).crossProduct(p3.vectorTo(p2));
//                Vector3D n2 = p3.vectorTo(p4).crossProduct(p1.vectorTo(p4));
//                
//                Vertex[] verts = new Vertex[6];
//                verts[0] = new Vertex(p3, n1);
//                verts[1] = new Vertex(p2, n1);
//                verts[2] = new Vertex(p1, n1);
//                
//                verts[3] = new Vertex(p1, n2);
//                verts[4] = new Vertex(p4, n2);
//                verts[5] = new Vertex(p3, n2);

                // 4 Triangle Height Normal
                Point3D p1 = new Point3D(x, heightmap[x][z], z);
                Point3D p2 = new Point3D(x + 1, heightmap[x + 1][z], z);
                Point3D p3 = new Point3D(x + 1, heightmap[x + 1][z + 1], z + 1);
                Point3D p4 = new Point3D(x, heightmap[x][z + 1], z + 1);
                Point3D p5 = new Point3D(x + 0.5, (p1.getY() + p2.getY() + p3.getY() + p4.getY()) / 4, z + 0.5);
                
                Vector3D n1 = p2.vectorTo(p5).crossProduct(p1.vectorTo(p5));
                Vector3D n2 = p3.vectorTo(p5).crossProduct(p2.vectorTo(p5));
                Vector3D n3 = p4.vectorTo(p5).crossProduct(p3.vectorTo(p5));
                Vector3D n4 = p1.vectorTo(p5).crossProduct(p4.vectorTo(p5));
                
                Vertex[] verts = new Vertex[12];
                verts[0] = new Vertex(p1, n1);
                verts[1] = new Vertex(p5, n1);
                verts[2] = new Vertex(p2, n1);
                
                verts[3] = new Vertex(p2, n2);
                verts[4] = new Vertex(p5, n2);
                verts[5] = new Vertex(p3, n2);
                
                verts[6] = new Vertex(p3, n3);
                verts[7] = new Vertex(p5, n3);
                verts[8] = new Vertex(p4, n3);
                
                verts[9] = new Vertex(p4, n4);
                verts[10] = new Vertex(p5, n4);
                verts[11] = new Vertex(p1, n4);

                grouper.addMesh(new SimpleMesh(verts, mat, new Point3D(0, 0, 0), new Angle3D(0, 0, 0), new Vector3D(1, 1, 1)));
            }
        }
        return grouper.simplify();
    }
    
}
