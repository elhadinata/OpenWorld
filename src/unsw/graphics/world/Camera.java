package unsw.graphics.world;


import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.geometry.Point3D;

public class Camera {
	private CoordFrame3D viewFrame;
	private float rot;
	private Terrain terrain;
	
	public Camera() {
		viewFrame = CoordFrame3D.identity()
				.translate(0, -1.5f, -9)
                .scale(0.75f, 0.75f, 0.75f);
		rot = 0;
	}
	
	public Camera(Terrain terrain) {
		viewFrame = CoordFrame3D.identity()
				.translate(-1, -1.5f, -9)
                .scale(0.75f, 0.75f, 0.75f);
		rot = 0;
		this.terrain = terrain;
	}
	
	public CoordFrame3D frame() {
		return viewFrame;
	}
	
	
	public void up() {
//		System.out.println("up");
		
		Matrix4 next = viewFrame.translate(0, 0, 1).getMatrix();
		float[] arr = next.getValues();
//		for(int i =0; i<16; ++i) {
//			System.out.print(" "+arr[i]);
//		}
		float x = arr[3];
		float z = arr[11];
		float width = terrain.width();
		float depth = terrain.depth();
		
//		System.out.println(next.getMatrix());
		System.out.println(x+" "+z);
//		
		viewFrame = viewFrame.translate(0, terrain.altitude(x/width, z/depth), 1);
		
	}
	
	public void down() {
//		System.out.println("down");
		CoordFrame3D next = viewFrame.translate(0, 0, -1);
		float x = next.getMatrix().getValues()[3];
		float z = next.getMatrix().getValues()[11];
//		System.out.println(x+" "+z);
		viewFrame = viewFrame.translate(0, terrain.altitude(x, z), -1);
	}
	
	public void left() {
//		System.out.println("left");
		rot = 2;
		viewFrame = viewFrame.rotateY(rot);
	}
	
	public void right() {
//		System.out.println("right");
		rot = -2;
		viewFrame = viewFrame.rotateY(rot);
	}
}
