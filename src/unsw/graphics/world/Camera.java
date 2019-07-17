package unsw.graphics.world;



import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Vector4;
import unsw.graphics.geometry.Point3D;

public class Camera {
	private CoordFrame3D viewFrame;
	private CoordFrame3D frame;
	
	private float rot;
	private Terrain terrain;
	
//	private static final Vector4 UP = new Vector4(0, 1, 0, 0);
	private static final float ROTATION_SPEED = 2;
	private Vector4 viewDirection;
	
//	public Camera() {
//		viewFrame = CoordFrame3D.identity()
//				.translate(0, -1.5f, -9)
//                .scale(0.75f, 0.75f, 0.75f);
//		rot = 0;
//	}
//	
//	public Camera(Terrain terrain) {
//		viewFrame = CoordFrame3D.identity()
//				.translate(-1, -1.5f, -9)
//                .scale(0.75f, 0.75f, 0.75f);
//		rot = 0;
//		this.terrain = terrain;
//		
//		viewDirection = new Vector4(0, 0, 1, 0);
//		
//	}
	public Camera(Terrain terrain, CoordFrame3D frame) {
		viewFrame = CoordFrame3D.identity()
				.translate(-1, -1.5f, -9)
                .scale(0.75f, 0.75f, 0.75f);
		
		this.frame = frame;
		rot = 0;
		this.terrain = terrain;
		
		viewDirection = new Vector4(0, 0, 1, 0);
		
	}
	
	
	public CoordFrame3D viewFrame() {
		return viewFrame;
	}
	public CoordFrame3D frame() {
		return frame;
	}
	
	
	public void forward() {
		//
		float[] arr1 = frame.getMatrix().getValues();
		float[] arr2 = viewFrame.getMatrix().getValues();
		
		float x1 = arr1[12];
		float z1 = arr1[14];
		
		float x2 = arr2[12];
		float z2 = arr2[14];
//		for(int i=0;i<16; ++i)
//			System.out.print(" "+arr[i]);
//		System.out.println("\n"+x1+" "+z1);
//		System.out.println(x2+" "+z2);
//		
//		if(x2>=x1 && z2>=z1) {// && x2<x1+terrain.width()&& z2<z1+terrain.depth()) {
//			Point3D p = viewDirection.asPoint3D();
//			System.out.println((p.getX()-x1)+ " "+ (p.getZ()-z1));
//			
//			viewFrame = viewFrame.translate(new Point3D(p.getX()
//					, -terrain.altitude(p.getX()-x1, p.getZ()-z1)
//					, p.getZ()));
//			
//		} else {
			viewFrame = viewFrame.translate(viewDirection.asPoint3D());
	
//		}
	}
	
	public void backward() {
		viewFrame = viewFrame.translate(viewDirection.trim().negate().asPoint3D());	
	}
	
	public void left() {
		rot = ROTATION_SPEED;
		viewFrame = viewFrame.rotateY(rot);
		viewDirection = Matrix4.rotationY(-ROTATION_SPEED).multiply(viewDirection);
		
	}
	
	public void right() {
		rot = -ROTATION_SPEED;
		viewFrame = viewFrame.rotateY(rot);
		viewDirection = Matrix4.rotationY(ROTATION_SPEED).multiply(viewDirection);
	}
}
