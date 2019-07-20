// UNUSED





//package unsw.graphics.world;
//
//
//
//import java.awt.image.SinglePixelPackedSampleModel;
//
//import unsw.graphics.CoordFrame3D;
//import unsw.graphics.Matrix4;
//import unsw.graphics.Vector4;
//import unsw.graphics.examples.sailing.objects.Mouse;
//import unsw.graphics.geometry.Point3D;
//import unsw.graphics.scene.MathUtil;
//
//public class Camera {
//	
//	private float rotation;
//	
//	private float x;
//	private float z;
//	
//	private Terrain terrain;
//	
//	private static final float ROTATION_SPEED = 5;
//	private static final float TRANS_SPEED = 0.2f;
//	
//	private Vector4 viewDirection;
//	
//
//	public Camera(Terrain terrain) {
//		
//		
//		this.terrain = terrain;
//		this.rotation = 0;
//		this.viewDirection = new Vector4(0, 0, TRANS_SPEED, 0);
//		this.x = 0;
//		this.z = 0;
//	}
//	
//	public float x() {
//		return this.x;
//	}
//	public float z() {
//		return this.z;
//	}
//	public float rotation() {
//		return rotation;
//	}
//	
//	public void forward() {
//		z -= 1;//(float) (Math.cos(Math.toRadians(MathUtil.normaliseAngle(rotation)))*TRANS_SPEED);
//		x -= 1;//(float) (Math.sin(Math.toRadians(MathUtil.normaliseAngle(rotation)))*TRANS_SPEED);
//	}
//	
//	public void backward() {
//		z += 1;//(float) (Math.cos(Math.toRadians(MathUtil.normaliseAngle(rotation)))*TRANS_SPEED);
//		x += 1;//(float) (Math.sin(Math.toRadians(MathUtil.normaliseAngle(rotation)))*TRANS_SPEED);
//	}
//	
//	public void left() {}
//	public void right() {}
//	
//	public void rotateLeft() {
//		this.rotation += ROTATION_SPEED;
//		
//	}
//	
//	public void rotateRight() {
//		this.rotation -= ROTATION_SPEED;
//	}
//}
