package com.up.terrainengine.operator.operators;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.up.terrainengine.util.TypeReference;
import com.up.terrainengine.operator.Operator;
import com.up.terrainengine.operator.Properties;
import com.up.terrainengine.operator.terminal.Terminal;
import com.up.terrainengine.operator.terminal.TerminalDefinition;
import com.up.terrainengine.operator.terminal.TerminalMode;
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
public class ConvertToImage extends Operator {
    
	private static TerminalDefinition<VectorMap<Double>> inputTermninal = new TerminalDefinition<>(new TypeReference<VectorMap<Double>>() {}, TerminalMode.INPUT, "Input");
	
    private Image outI;

	public ConvertToImage() {
		super(new Properties(), inputTermninal);
	}

    @Override
    public String getName() {
        return "Convert To Image";
    }

    @Override
    public boolean operate() {
        VectorMap<Double> in = getTerminal(inputTermninal).getState();
        int size = in.size();
        BufferedImage i = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Vector<Double> v = in.get(x, y);
				if (v.size() == 1) {
//                    int c = (int)(v.get() * 255);
//                    i.setRGB(x, y, new Color(c, c, c).getRGB());
                    float c = (float)(double)v.get();
                    i.setRGB(x, y, new Color(c, c, c).getRGB());
                } else if (v.size() == 3) {
//                    i.setRGB(x, y, new Color((int)(v.get(0) * 255), (int)(v.get(1) * 255), (int)(v.get(2) * 255)).getRGB());
                    i.setRGB(x, y, new Color((float)(double)v.get(0), (float)(double)v.get(1), (float)(double)v.get(2)).getRGB());
                }
//                i.setRGB(x, y, v << 16 + v << 8 + v);
            }
        }
        outI = i;
        return true;
    }

	@JsonIgnore
    public Image getImage() {
        return outI;
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
    
}
