package unsw.graphics.world;



import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Vector4;
import unsw.graphics.geometry.Point3D;

public class Camera {
	private CoordFrame3D viewFrame;
	private CoordFrame3D viewFrameIdentity;
	private CoordFrame3D frame;
	
	private float rot;
	private Terrain terrain;
	
//	private static final Vector4 UP = new Vector4(0, 1, 0, 0);
	private static final float ROTATION_SPEED = 2;
	private Vector4 viewDirection;
	

	public Camera(Terrain terrain, CoordFrame3D frame) {
		viewFrame = CoordFrame3D.identity()
				.translate(-1, -1.5f, -9)
                .scale(0.75f, 0.75f, 0.75f);
		viewFrameIdentity = CoordFrame3D.identity()
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
		Vector4 forwardVector = new Vector4(0, 0, 1, 0);
		viewFrame = viewFrame.translate(Matrix4.rotationY(rot).multiply(forwardVector).asPoint3D());
	
	}
	
	public void backward() {
		Vector4 backwardVector = new Vector4(0, 0, -1, 0);
		viewFrame = viewFrame.translate(Matrix4.rotationY(rot).multiply(backwardVector).asPoint3D());
	}
	
	public void left() {
		Matrix4 sideDirection = Matrix4.translation(1, 0, 0);
		
		Vector4 p =  Matrix4.rotationY(rot).multiply(sideDirection.phi());
		viewFrame = viewFrame.translate(p.asPoint3D());	

		viewDirection = Matrix4.rotationY(rot).multiply(sideDirection.phi());
	}
	public void right() {
		Matrix4 sideDirection = Matrix4.translation(-1, 0, 0);
		
		Vector4 p =  Matrix4.rotationY(rot).multiply(sideDirection.phi());
		viewFrame = viewFrame.translate(p.asPoint3D());	

		viewDirection = Matrix4.rotationY(rot).multiply(sideDirection.phi());
	}
	
	public void rotateLeft() {
		rot -= ROTATION_SPEED;
		viewFrame = viewFrame.rotateY(ROTATION_SPEED);
		viewDirection = Matrix4.rotationY(-ROTATION_SPEED).multiply(viewDirection);
		
	}
	
	public void rotateRight() {
		rot += ROTATION_SPEED;
		viewFrame = viewFrame.rotateY(rot);
		viewDirection = Matrix4.rotationY(ROTATION_SPEED).multiply(viewDirection);
	}
}
