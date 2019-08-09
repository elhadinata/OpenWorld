package unsw.graphics.world;


import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.IntBuffer;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jogamp.nativewindow.util.Point;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
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
import unsw.graphics.Vector3;
import unsw.graphics.geometry.LineStrip2D;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleFan3D;
import unsw.graphics.geometry.TriangleMesh;
import unsw.graphics.scene.MathUtil;



/**
 * COMMENT: Comment Game 
 * Implemented the terrain properly, working bilinear interpolation
 * Camera fully working
 * Textured Mesh and Trees
 * Directional Lighting
 * Day Night toggle with keypress "L" <- implemented
 * Added Test 8
 * Test and Normal Mode, 
 * if TEST==false then will generate 100x100 terrain without trees instead
 * 
 */
public class World extends Application3D implements KeyListener{

    private Terrain terrain;
	private List<Point3D>  vertex;
	private List<Integer> indices;
	private List<Point3D>  vertexWater;
	private List<Integer> indicesWater;
	
	// Terrain, Tree Mesh
	private TriangleMesh treeMesh;
	private TriangleMesh avatarMesh;
	private TriangleMesh terrainMesh;
	private TriangleMesh skyMesh;
	private TriangleMesh waterMesh;
	private TriangleMesh world;
	private TriangleMesh roadMesh;
	
	private Texture textures[];
	
	TriangleMesh curve;
	
	// Terrain Texture buffer
	private Point3DBuffer vertexBuffer;
    private Point2DBuffer texCoordBuffer;
    private IntBuffer indicesBuffer;
    
    // Road Buffer
    private Point3DBuffer vertexBuffer1;
    private Point2DBuffer texCoordBuffer1;
    private IntBuffer indicesBuffer1;
    
    // Texture buffer ids/names
    private int verticesName;
    private int texCoordsName;
    private int indicesName;
	
 // road buffer
    private int verticesName1;
    private int texCoordsName1;
    private int indicesName1;
    
	private Shader shader;
	
	// Get Camera translation
	float x;
	float z;
	float x1;
	float z1;
	
	float rotation;
	
	/* Properties
	*	If TEST == true then it will print a large flat surface, to
	*	test directional lighting and camera
	*
	*	If LIGHTING == true then Day and Night Texture will be used
	*	IF DAY == true -> use Sky texture
	*	ELSE -> use Night texture
	*/
	private static boolean LIGHTING = true;
	private static boolean DAY = true;
	private static final boolean TEST = true;
	public static final float INIT_ROTATION = 135;
	private static final Color NIGHT_COLOR = new Color(0.3f, 0.3f, 0.3f, 0.5f);
	public static boolean THIRD_PERSON = false;
	public static final float DISTANCE = 6;// distance of avatar to camera
	
    public World(Terrain terrain) {
    	super("Assignment 2", 800, 600);
        this.terrain = terrain;
        try {
			this.world = new TriangleMesh("res/models/cube_normals.ply", true, true);
			this.treeMesh = new TriangleMesh("res/models/tree.ply", true, true);
			this.avatarMesh = new TriangleMesh("res/models/cow.ply", true, true);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		
		CoordFrame3D viewFrame = CoordFrame3D.identity();
		float alt = -1.5f;
		// Check if camera is within the terrain, if it is then add altitude
		if(-x <= terrain.width()-1 && -z <= terrain.depth()-1 && z<=0 && x<=0) {
		 alt = -1.5f-(terrain.altitude(-x, -z));
		}
		if(THIRD_PERSON == false) {
			Shader.setViewMatrix(gl, viewFrame.rotateY(rotation).translate(x, alt, z).getMatrix());
		} else {
//			double offsetX = DISTANCE * (float)Math.sin(Math.toRadians(rotation%360));
//			double offsetZ = -DISTANCE * (float)Math.cos(Math.toRadians(rotation%360));
			x1 = (float) (x + DISTANCE*Math.sin(Math.toRadians(rotation)));
    		z1 = (float) (z + -DISTANCE*Math.cos(Math.toRadians(rotation)));
			CoordFrame3D avatar = viewFrame.rotateY(rotation).translate(x, alt, z);
//			CoordFrame3D camera = viewFrame.rotateY(rotation).translate(x1+(float)offsetX+1, alt, z1+(float)offsetZ+1);

			CoordFrame3D camera = viewFrame.rotateY(rotation).translate(x1, alt, z1);
			System.out.println("rotation: "+rotation);
			System.out.println("avatar at: "+x+", "+z);
			System.out.println("camera at: "+(x1)+", "+(z1)+"\n");
			
			
			Shader.setViewMatrix(gl, camera.getMatrix());
			

			// Draw the avatar
			TriangleFan3D face = new TriangleFan3D(-1,-1,1, 1,-1,1, 1,1,1, -1,1,1);
			
	        // Front
//	        Shader.setPenColor(gl, Color.RED);
//	        face.draw(gl, viewFrame.translate(-x, -alt, -z).rotateY(-rotation));
	        Shader.setPenColor(gl, new Color(10, 10, 10));
	        avatarMesh.draw(gl, viewFrame.translate(-x, -alt-1.5f, -z).rotateY(-rotation).rotateY(90).scale(0.5f, 0.5f, 0.5f));
//	        
	        
		}
		
		CoordFrame3D modelFrame = CoordFrame3D.identity().translate(0, 0, 0);
        
		
		
//		Shader.setPenColor(gl,Color.BLUE);
//        waterMesh.draw(gl, modelFrame.translate(0, 0.25f, 0));
        
        // Set Terrain Texture
        Shader.setInt(gl, "tex", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0].getId());
        Shader.setPenColor(gl, Color.WHITE);
        
        // Draw the texture according to the supplied vertices and indices
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);
        
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texCoordsName);
        gl.glVertexAttribPointer(Shader.TEX_COORD, 2, GL.GL_FLOAT, false, 0, 0);
        
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);
      
        terrainMesh.draw(gl, modelFrame);
        
       
        
        // Set tree Texture
//        Shader.setInt(gl, "tex", 0);
//        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[1].getId());
        Shader.setPenColor(gl, Color.WHITE);
        
        
        
        if (LIGHTING) {
        	if (DAY) {
	    		setBackground(Color.WHITE);
	        	// Set the lighting properties
	    		
	        	Vector3 light = terrain.getSunlight();//.plus(new Vector3(0, 20, 0));
	            Shader.setInt(gl, "day", 1);
	        	Shader.setPoint3D(gl, "lightPos", new Point3D(light.getX(),light.getY(),light.getZ()));
	            Shader.setColor(gl, "lightIntensity", Color.WHITE);
	            Shader.setColor(gl, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));
	
	            // Set the material properties
	            Shader.setColor(gl, "ambientCoeff", Color.WHITE);
	            Shader.setColor(gl, "diffuseCoeff", new Color(1f, 1f, 1f));
	            Shader.setColor(gl, "specularCoeff", new Color(0.5f, 0.5f, 0.5f));
	            Shader.setFloat(gl, "phongExp", 128f);
	            Shader.setFloat(gl, "attenuationFactor", 0.5f);
	            Shader.setFloat(gl, "exponent", 1);
	            
        	} else {
        		
        		setBackground(NIGHT_COLOR);
        		Vector3 light = terrain.getSunlight();//.plus(new Vector3(0, 20, 0));x
        		
        		
                Shader.setInt(gl, "day", 0);
        		Shader.setPoint3D(gl, "lightPos", new Point3D(light.getX(),light.getY(),light.getZ()));
                Shader.setColor(gl, "lightIntensity", Color.WHITE);
                Shader.setColor(gl, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));

                // Set the material properties
                Shader.setColor(gl, "ambientCoeff", Color.WHITE);
                Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
                Shader.setColor(gl, "specularCoeff", new Color(0.8f, 0.8f, 0.8f));
                Shader.setFloat(gl, "phongExp", 128f);
	            Shader.setFloat(gl, "exponent", 0.5f);
	            Shader.setFloat(gl, "cutoff", 10f);
	            Shader.setFloat(gl, "outercutoff", 17.5f);
        	
	            // fog properties
	            Shader.setFloat(gl, "density", 0.03f);
	            Shader.setFloat(gl, "gradient", 1.4f);
	            
        	}
    	}
        
        // If in test mode then draw the trees
        if(TEST==true) {
	        for(Tree t: terrain.trees()) {
	        	float tx = t.getPosition().getX();
	        	float tz = t.getPosition().getZ();
	        	CoordFrame3D treeFrame= modelFrame.translate(0,5,0.5f).translate(tx, terrain.altitude(tx, tz), tz);
	        	
	        	treeMesh.draw(gl, treeFrame);
	        }
        }
        
        
        Shader.setPenColor(gl, Color.RED);
        roadMesh.draw(gl, modelFrame);
        
        
        
	}

	@Override
	public void destroy(GL3 gl) {
		super.destroy(gl);
		
	}

	@Override
	public void init(GL3 gl) {
		super.init(gl);
		
		rotation = INIT_ROTATION;
		getWindow().addKeyListener(this);
		
		int[] names = new int[3];
        gl.glGenBuffers(3, names, 0);
        
        verticesName = names[0];
        texCoordsName = names[1];
        indicesName = names[2];
		

		// Inialisation
		initTerrain(gl);
		initWater(gl);
		initMesh(gl);
		initTexture(gl);
		initRoad(gl);
		
//		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL3.GL_LINE); // DRAW OUTLINE ONLY
		gl.glDisable(GL.GL_CULL_FACE);
		
		
		shader.use(gl);
        
	}

	
	// Set all the indices and vertices for terrain
	private void initTerrain(GL3 gl) {
		int width = terrain.width();
        int depth = terrain.depth();
		this.vertex= new ArrayList<>();

		ArrayList<Point2D> texCoord = new ArrayList<>();
		// If not in test mode, generate a 100x100 flat surfact
		if(TEST == false) {
        	width = 100;
        	depth = 100;
        	
        	for(int z=0; z<width; z++) {
        		for(int x=0;x <depth ; x++) {
        			vertex.add(new Point3D(x, 0, z));
        			texCoord.add(new Point2D(x, z));
            	}
            }

            this.indices=new ArrayList<>();
            for(int z=0; z<depth-1; z++) {
            	for(int x=0; x<width-1; x++) {
            		
            		indices.add((x+1)+z*width);	// 1
            		indices.add(x+(z+1)*width);	// 2
            		indices.add(x+z*width);		// 0

            		indices.add((x+1)+(z+1)*width);	// 3
            		indices.add(x+(z+1)*width); 	// 2
            		indices.add((x+1)+z*width);		// 1
            		
            	}
            }
            vertexBuffer = new Point3DBuffer(vertex);
       
            texCoordBuffer = new Point2DBuffer(texCoord);
            
            int[] array = new int[indices.size()];
            for(int i = 0; i < indices.size(); i++) 
            	array[i] = indices.get(i);
            
            indicesBuffer = GLBuffers.newDirectIntBuffer(array);
        	
        } else {
        	for(int z=0; z<width; z++) {
        		for(int x=0;x <depth ; x++) {
        			vertex.add(new Point3D(x, terrain.altitude(x, z), z));
        			texCoord.add(new Point2D(x, z));
            	}
            }

            this.indices=new ArrayList<>();
            for(int z=0; z<depth-1; z++) {
            	for(int x=0; x<width-1; x++) {
            		
            		indices.add((x+1)+z*width);	// 1
            		indices.add(x+(z+1)*width);	// 2
            		indices.add(x+z*width);		// 0

            		indices.add((x+1)+(z+1)*width);	// 3
            		indices.add(x+(z+1)*width); 	// 2
            		indices.add((x+1)+z*width);		// 0
            		
            	}
            }
            vertexBuffer = new Point3DBuffer(vertex);
       
            texCoordBuffer = new Point2DBuffer(texCoord);
            
            int[] array = new int[indices.size()];
            for(int i = 0; i < indices.size(); i++) 
            	array[i] = indices.get(i);
            
            indicesBuffer = GLBuffers.newDirectIntBuffer(array);
        }
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
		
        
	}
	
	// Prepare the trees
	private void initMesh(GL3 gl) {
			treeMesh.init(gl);
			avatarMesh.init(gl);
	}
	
	
	
	private void initWater(GL3 gl) {
		int width = terrain.width();
        int depth = terrain.depth();
		this.vertexWater= new ArrayList<>();
		width *= 2;
    	depth *= 2;
    	
    	for(int z=0; z<width; z++) {
    		for(int x=0;x <depth ; x++) {
    			vertexWater.add(new Point3D(x, 0, z));
        	}
        }

        this.indicesWater=new ArrayList<>();
        for(int z=0; z<depth-1; z++) {
        	for(int x=0; x<width-1; x++) {
        		
        		indicesWater.add((x+1)+z*width);	// 1
        		indicesWater.add(x+(z+1)*width);	// 2
        		indicesWater.add(x+z*width);		// 0

        		indicesWater.add((x+1)+(z+1)*width);	// 3
        		indicesWater.add(x+(z+1)*width); 	// 2
        		indicesWater.add((x+1)+z*width);		// 0
        		
        	}
        }
        
        int[] array = new int[indices.size()];
        for(int i = 0; i < indices.size(); i++) 
        	array[i] = indices.get(i);
        
        waterMesh = new TriangleMesh(vertexWater, indicesWater,true);
		waterMesh.init(gl);
	}
	
	// Prepare the textures
	private void initTexture(GL3 gl) {
		textures = new Texture[4];
		textures[0] = new Texture(gl, "res/textures/cartoon_grass.jpg", "jpg", false);
		textures[1] = new Texture(gl, "res/textures/rock.bmp", "bmp", false);
        textures[2] = new Texture(gl, "res/textures/sky.bmp", "bmp", false);
		textures[3] = new Texture(gl, "res/textures/darkskies/darkskies_lf.png",
                    "res/textures/darkskies/darkskies_rt.png",
                    "res/textures/darkskies/darkskies_dn.png",
                    "res/textures/darkskies/darkskies_up.png",
                    "res/textures/darkskies/darkskies_ft.png",
                    "res/textures/darkskies/darkskies_bk.png", "png", false);
        
        if (LIGHTING) {
			shader = new Shader(gl, "shaders/vertex_ass_phong.glsl",
                    "shaders/fragment_ass_phong.glsl");
//        	shader = new Shader(gl, "shaders/vertex_phong.glsl",
//                    "shaders/fragment_cubemap.glsl");
        } else {
        	shader = new Shader(gl, "shaders/vertex_tex_3d.glsl",
	                "shaders/fragment_tex_3d.glsl");
        }
		shader.use(gl);
	}
	
	private void toggleTime() {
		DAY = DAY ^ true;
		System.out.println("Day = "+DAY);
	}
	private void toggleCamera() {
		THIRD_PERSON = THIRD_PERSON ^ true;
		System.out.println("Third Person = "+THIRD_PERSON);
	}
	
	private void initRoad(GL3 gl) {
		
		List<Point3D> vertices = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		List<Point2D> texCoord = new ArrayList<>();
		
		for (Road r : terrain.roads()) {
			float width = (float)r.width();
			
			// Use starting point as altitude for the whole road
			Point2D start = r.controlPoint(0);
			float alt = terrain.altitude(start.getX(), start.getY());
    		System.out.println("alt: " + alt);
			
    		int count = 0;
			for (float t = 0.000f; t <= r.size(); t+=0.001f) {
				Point2D p = r.point(t);
				float x = p.getX();
				float z = p.getY();
				
				Point2D tangent = Road.normalize(r.tangent(t));
				Vector3 k = new Vector3(tangent.getX(), 0, tangent.getY()).normalize();
				Vector3 i = new Vector3(-k.getY(), k.getX(), 0).normalize();
				Vector3 j = k.cross(i).normalize().scale(width/2);
				Point3D c = new Point3D(x, alt+0.02f, z);
				
				vertices.add(c.translate(j.negate()));
				vertices.add(c.translate(j));
				vertices.add(c.translate(j.negate()).translate(k));
				vertices.add(c.translate(j).translate(k));
				
        		indices.add(count);		// 0
				indices.add(count+1);	// 1
        		indices.add(count+2);	// 2

        		indices.add(count+1);	// 1
        		indices.add(count+3);	// 3
        		indices.add(count+2); 	// 2
        		
        		count += 2;
			}
		}
		
		for (int z=0; z < terrain.depth(); z++) {
    		for (int x=0; x < terrain.width(); x++) {
    			texCoord.add(new Point2D(x, z));
        	}
        }
		
		vertexBuffer1 = new Point3DBuffer(vertices);
        texCoordBuffer1 = new Point2DBuffer(texCoord);
        
        int[] array = new int[indices.size()];
        for(int i = 0; i < indices.size(); i++) 
        	array[i] = indices.get(i);
        
        indicesBuffer1 = GLBuffers.newDirectIntBuffer(array);
        
        roadMesh = new TriangleMesh(vertices, indices, true);
        roadMesh.init(gl);
    
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexBuffer1.capacity() * 3 * Float.BYTES,
                vertexBuffer1.getBuffer(), GL.GL_STATIC_DRAW);
        
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texCoordsName);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, texCoordBuffer1.capacity() * 2 * Float.BYTES,
                texCoordBuffer1.getBuffer(), GL.GL_STATIC_DRAW);
       
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer1.capacity() * Integer.BYTES,
                indicesBuffer1, GL.GL_STATIC_DRAW);
	}
	
	@Override
	public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 1, 100));
	
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
        
        case KeyEvent.VK_LEFT:
        	rotation -= 5;
        	if(THIRD_PERSON == true) {
        		x1 = (float) (x + DISTANCE*Math.sin(Math.toRadians(rotation)));
        		z1 = (float) (z + -DISTANCE*Math.cos(Math.toRadians(rotation)));
        		
        	}
        	  
            break;
        case KeyEvent.VK_RIGHT:
        	rotation += 5;
        	if(THIRD_PERSON == true) {
        		x1 = (float) (x+DISTANCE*Math.sin(Math.toRadians(rotation)));
        		z1 = (float) (z+-DISTANCE*Math.cos(Math.toRadians(rotation)));
        		
        	}
        	break;
        case KeyEvent.VK_UP:
        	x -= Math.sin(rotation * Math.PI/180);
            z += Math.cos(rotation * Math.PI/180);       
            if(THIRD_PERSON == true) {
            	x1 = (float) (x + DISTANCE*Math.sin(Math.toRadians(rotation)));
        		z1 = (float) (z + -DISTANCE*Math.cos(Math.toRadians(rotation)));  
        	}
            
            break;
        case KeyEvent.VK_DOWN:
            x += Math.sin(rotation * Math.PI/180);
            z -= Math.cos(rotation * Math.PI/180);
            if(THIRD_PERSON == true) {
            	x1 = (float) (x + DISTANCE*Math.sin(Math.toRadians(rotation)));
        		z1 = (float) (z + -DISTANCE*Math.cos(Math.toRadians(rotation)));
            }
            break;
        case KeyEvent.VK_L:
            // Toggle light
        	toggleTime();
            break;
        case KeyEvent.VK_P:
            // Toggle 3rd person view
        	toggleCamera();
            break;
        default:
            break;
        }
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
