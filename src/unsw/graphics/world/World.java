package unsw.graphics.world;

import java.awt.Color;
import java.awt.image.AreaAveragingScaleFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;



/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class World extends Application3D {

    private Terrain terrain;

    public World(Terrain terrain) {
    	super("Assignment 2", 800, 600);
        this.terrain = terrain;
   
    }
   
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        World world = new World(terrain);
        world.start();
    }

	@Override
	public void display(GL3 gl) {
		super.display(gl);
		
		// Write something here, probably
		CoordFrame3D viewFrame = CoordFrame3D.identity()
				.translate(0, 0, -2)
                .translate(-4, 2, -9)
                .scale(0.75f, 0.75f, 0.75f)
                .rotateX(45).rotateZ(30);
//		
				
        Shader.setViewMatrix(gl, viewFrame.getMatrix());
        List<Point3D> points= new ArrayList<>();
        
        
    	for(int z=0; z<10; z++) {
    		for(int x=0;x <10 ; x++) {
        		points.add(new Point3D(x, terrain.altitude(x, z), z));
        	}
        }
//        points.add(new Point3D(-1, 0, 1));
//        points.add(new Point3D(1, 0, 1));
//        points.add(new Point3D(1, 0, -1));
//        points.add(new Point3D(-1, 0, -1));

        
        List<Integer> indices=new ArrayList<>();// = Arrays.asList(0,1,2,0,2,3);
        
        int width = terrain.width();
        int depth = terrain.depth();
        
        for(int z=0; z<depth-1; z++) {
        	for(int x=0; x<width-1; x++) {
        		indices.add(x+z*width);
        		indices.add(x+(z+1)*width);
        		indices.add((x+1)+z*width);
        		
        		indices.add(x+(z+1)*width);
        		indices.add((x+1)+(z+1)*width);
        		indices.add((x+1)+z*width);
        	}
        }
        CoordFrame3D frame = CoordFrame3D.identity();
        
        TriangleMesh tMesh = new TriangleMesh(points, indices,true);
        
        Shader.setPenColor(gl, Color.BLACK);
        tMesh.init(gl);
        tMesh.draw(gl);
	}

	@Override
	public void destroy(GL3 gl) {
		super.destroy(gl);
		
	}

	@Override
	public void init(GL3 gl) {
		super.init(gl);
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL3.GL_LINE); // HAPUS NANTI
        
	}

	@Override
	public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 1, 100));
	}
}
