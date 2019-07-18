package unsw.graphics.world;



import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Vector4;
import unsw.graphics.examples.sailing.objects.Mouse;
import unsw.graphics.geometry.Point3D;

public class Camera {
	private CoordFrame3D viewFrame;
	private CoordFrame3D viewFrameIdentity;
	private CoordFrame3D frame;
	
	private float rot;
	private Terrain terrain;
	
	private static final float ROTATION_SPEED = 5;
	private static final float TRANS_SPEED = 0.2f;
	
	private Vector4 viewDirection;
	

	public Camera(Terrain terrain, CoordFrame3D frame) {
		
		
		this.terrain = terrain;
		
		
		// To Set the initial position of camera
		viewFrameIdentity = CoordFrame3D.identity();
		viewFrame = viewFrameIdentity;
		this.frame = frame.translate(5, -0.7f, -5).rotateY(180);
		//this.viewFrame = viewFrame.translate(10, -0.7f, 10);
		rot = 0;
		
		viewDirection = new Vector4(0, 0, TRANS_SPEED, 0);
		
	}
	
	
	public CoordFrame3D viewFrame() {
		return viewFrame;
	}
	public CoordFrame3D frame() {
		return frame;
	}
	
	
	public void forward() {
		Vector4 forwardVector = new Vector4(0, 0, TRANS_SPEED, 0);
		viewFrame = viewFrame.translate(Matrix4.rotationY(rot).multiply(forwardVector).asPoint3D());
	
	}
	
	public void backward() {
		Vector4 backwardVector = new Vector4(0, 0, -TRANS_SPEED, 0);
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
		viewDirection = Matrix4.rotationY(ROTATION_SPEED).multiply(viewDirection);
		
		
	}
	
	public void rotateRight() {
		rot += ROTATION_SPEED;
		viewFrame = viewFrame.rotateY(-ROTATION_SPEED);
		viewDirection = Matrix4.rotationY(-ROTATION_SPEED).multiply(viewDirection);
	}
}
