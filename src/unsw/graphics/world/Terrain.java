package unsw.graphics.world;



import java.nio.DoubleBuffer;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;



/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {

    private int width;
    private int depth;
    private float[][] altitudes;
    private List<Tree> trees;
    private List<Road> roads;
    private Vector3 sunlight;

    
    public int width() {
    	return width;
    }
    
    public int depth() {
    	return depth;
    }
    
    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth, Vector3 sunlight) {
        this.width = width;
        this.depth = depth;
        altitudes = new float[width][depth];
        trees = new ArrayList<Tree>();
        roads = new ArrayList<Road>();
        this.sunlight = sunlight;
    }

    public List<Tree> trees() {
        return trees;
    }

    public List<Road> roads() {
        return roads;
    }

    public Vector3 getSunlight() {
        return sunlight;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        sunlight = new Vector3(dx, dy, dz);      
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public float getGridAltitude(int x, int z) {
        return altitudes[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, float h) {
        altitudes[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * @param x
     * @param z
     * @return
     */
    public float altitude(float x, float z) {
        
        float alt = 0;
        int x_floor = (int) Math.floor(x);
        int x_ceil = (int) Math.ceil(x);
        
        int z_floor = (int) Math.floor(z);
        int z_ceil = (int) Math.ceil(z);
        
        // Cases where floor(x)=ceil(x) only need linear interpolation
        if(x_floor == x_ceil && z_floor == z_ceil) {
        	return getGridAltitude(x_floor, z_floor);
        }  else if (x_floor == x_ceil && z_floor != z_ceil) {
        	float t = (z_ceil-z)/(z_ceil-z_floor);
        	float y1 =  getGridAltitude(x_floor, z_floor);
        	float y2 =  getGridAltitude(x_ceil, z_ceil);
        	
        	return ((1-t)*y1)+((t)*y2);
        	
        } 
        // Cases where floor(z)=ceil(z) only need linear interpolation
        else if (x_floor != x_ceil && z_floor == z_ceil) {
        	float t = (x_ceil-x)/(x_ceil-x_floor);
        	float y1 =  getGridAltitude(x_floor, z_floor);
        	float y2 =  getGridAltitude(x_ceil, z_ceil);
        	
        	return ((1-t)*y1)+((t)*y2);
        	
        }
        
        
        // === Point 1 === //
        Point3D p1 = new Point3D(x_floor, getGridAltitude(x_floor, z_floor), z_floor);
        
        // === Point 2 === //
        Point3D p2 = new Point3D(x_floor, getGridAltitude(x_floor, z_ceil), z_ceil);
        
        // === Point 3 === //
        Point3D p3 = new Point3D(x_ceil, getGridAltitude(x_ceil, z_floor), z_floor);
     
        // === Point 4 === //
        Point3D p4 = new Point3D(x_ceil, getGridAltitude(x_ceil, z_ceil), z_ceil);
        
        float x1 = p1.getX();
        float y1 = p1.getY();
        float z1 = p1.getZ();
        
        float x2 = p2.getX();
        float y2 = p2.getY();
        float z2 = p2.getZ();
        
        float x3 = p3.getX();
        float y3 = p3.getY();
        float z3 = p3.getZ();

        float x4 = p4.getX();
        float y4 = p4.getY();
        float z4 = p4.getZ();
        
        float t = (x3-x)/(x3-x1);
        if(x3==x1) {
        	t= (z3-z)/(z3-z1);
        	if(z3==z1) {
        		System.out.println("DIVISION BY 0 (X3 == X1)");
        	}
        }
        float t1 = (x4-x)/(x4-x2);
        
        float x5 = (t * x1) + ((1-t)*x3);
        float y5 = (t * y1) + ((1-t)*y3);
        float z5 = (t * z1) + ((1-t)*z3);
        
        float x6 = (t1*x2) + ((1-t1)*x4);
        float y6 = (t1*y2) + ((1-t1)*y4);
        float z6 = (t1*z2) + ((1-t1)*z4);
        
        
        float t2 = (z6-z)/(z6-z5);
        if(z6 == z5) {
        	t2 = (z6-z)/(z6-z5);
        	if(z6==z5 && x6==x5) {
            	System.out.println("DIVISION BY 0 (X4 == X2)");
            }
        }
        
        alt = ((1-t2) * y5) + (t2*y6);
    	
        
        return alt;
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(float x, float z) {
        float y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        trees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(float width, List<Point2D> spine) {
        Road road = new Road(width, spine);
        roads.add(road);        
    }

}
