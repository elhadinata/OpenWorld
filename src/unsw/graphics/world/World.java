package unsw.graphics.world;

import static org.hamcrest.CoreMatchers.not;

import java.awt.Color;
import java.awt.image.AreaAveragingScaleFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Point2DBuffer;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.Vector4;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;



/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class World extends Application3D {

    private Terrain terrain;
	private List<Point3D>  vertex;
	private List<Integer> indices;
	
	private TriangleMesh treeMesh;
	private TriangleMesh terrainMesh;
	private Texture textures[];
	
	private Point3DBuffer vertexBuffer;
    private Point2DBuffer texCoordBuffer;
    private IntBuffer indicesBuffer;
    private int verticesName;
    private int texCoordsName;
    private int indicesName;
	
	private Shader shader;
	
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
		
		// Identity
		CoordFrame3D frame = CoordFrame3D.identity();
		
		// Write something here, probably
		CoordFrame3D viewFrame = CoordFrame3D.identity()
				.translate(0, 0, -2)
                .translate(-4, 2, -9)
                .scale(0.75f, 0.75f, 0.75f)
                .rotateX(35).rotateY(10).rotateZ(5);
	
				
        Shader.setViewMatrix(gl, viewFrame.getMatrix());
        
         
        // Terrain Texture
        Shader.setInt(gl, "tex", 0);
        
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0].getId());
        
        
        
        
        // DRAW the texture differently, too smooth, probably wrong text coordinates
//        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
//        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);
//        
//        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texCoordsName);
//        gl.glVertexAttribPointer(Shader.TEX_COORD, 2, GL.GL_FLOAT, false, 0, 0);
//        
//        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);
        
        
        
        terrainMesh.draw(gl, frame);
        
//        Shader.setInt(gl, "tex", 1);
//        gl.glActiveTexture(GL.GL_TEXTURE1);
//        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[1].getId()); // causes terrain to lose texture
        
        Shader.setPenColor(gl, Color.WHITE);
        
        for(Tree t: terrain.trees()) {
        	float x = t.getPosition().getX();
        	float z = t.getPosition().getZ();
        	
        	CoordFrame3D treeFrame= frame.translate(0,5,0).translate(x, terrain.altitude(x, z), z);
        	
        	treeMesh.draw(gl, treeFrame);
        }
	}

	@Override
	public void destroy(GL3 gl) {
		super.destroy(gl);
		
	}

	@Override
	public void init(GL3 gl) {
		super.init(gl);
		
		
		int[] names = new int[3];
        gl.glGenBuffers(3, names, 0);
        
        verticesName = names[0];
        texCoordsName = names[1];
        indicesName = names[2];
		
		// Terrain
		initTerrain();
		terrainMesh = new TriangleMesh(vertex, indices,true);
		terrainMesh.init(gl);
    
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 3 * Float.BYTES,
                vertexBuffer.getBuffer(), GL.GL_STATIC_DRAW);
        
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texCoordsName);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, texCoordBuffer.capacity() * 2 * Float.BYTES,
                texCoordBuffer.getBuffer(), GL.GL_STATIC_DRAW);
       
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * Integer.BYTES,
                indicesBuffer, GL.GL_STATIC_DRAW);
		
		initTexture(gl);
    
		initTrees();
		treeMesh.init(gl);
		
//		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL3.GL_LINE); // DRAW OUTLINE ONLY
		gl.glDisable(GL.GL_CULL_FACE);
		
		
		shader.use(gl);
        
	}

	
	// Set all the indices and vertices for terrain
	private void initTerrain() {
		int width = terrain.width();
        int depth = terrain.depth();
        
		this.vertex= new ArrayList<>();

    	for(int z=0; z<10; z++) {
    		for(int x=0;x <10 ; x++) {
    			vertex.add(new Point3D(x, terrain.altitude(x, z), z));
        	}
        }

        this.indices=new ArrayList<>();
        for(int z=0; z<depth-1; z++) {
        	for(int x=0; x<width-1; x++) {
        		indices.add(x+z*width);		// 0
        		indices.add(x+(z+1)*width);	// 2
        		indices.add((x+1)+z*width);	// 1
        		

        		indices.add((x+1)+z*width);		// 0
        		indices.add(x+(z+1)*width); 	// 2
        		indices.add((x+1)+(z+1)*width);	// 3
        	}
        }
        vertexBuffer = new Point3DBuffer(vertex);
        texCoordBuffer = new Point2DBuffer(Arrays.asList(
                new Point2D(0,0),
                new Point2D(0,1f),
                new Point2D(0.25f,0),
                new Point2D(0.25f,1f),
                new Point2D(0.5f,0),
                new Point2D(0.5f,1f),
                new Point2D(0.75f,0),
                new Point2D(0.75f,1f),
                new Point2D(1,0),
                new Point2D(1,1f)));

        int[] array = new int[indices.size()];
        for(int i = 0; i < indices.size(); i++) 
        	array[i] = indices.get(i);
        
        indicesBuffer = GLBuffers.newDirectIntBuffer(array);
        
	}
	
	private void initTrees() {
		try {
			this.treeMesh = new TriangleMesh("res/models/tree.ply", true, true);
		} catch (IOException e) {
			System.out.println("Error reading tree.ply");
			e.printStackTrace();
		}
	}
	
	private void initTexture(GL3 gl) {
		textures = new Texture[3];
		textures[0] = new Texture(gl, "res/textures/cartoon_grass.jpg", "jpg", false);
		textures[1] = new Texture(gl, "res/textures/rock.bmp", "bmp", false);
        
		shader = new Shader(gl, "shaders/vertex_tex_3d.glsl",
	                "shaders/fragment_tex_3d.glsl");
        
	}
	
	@Override
	public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 1, 100));
	}
}
