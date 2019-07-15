package unsw.graphics.world;



import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Vector4;

public class Camera {
	private CoordFrame3D viewFrame;
	private float rot;
	private Terrain terrain;
	
//	private static final Vector4 UP = new Vector4(0, 1, 0, 0);
	private static final float ROTATION_SPEED = 2;
	private Vector4 viewDirection;
	
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
		viewDirection = new Vector4(0, 0, 1, 0);
		
	}
	
	public CoordFrame3D frame() {
		return viewFrame;
	}
	
	
	public void forward() {
		viewFrame = viewFrame.translate(viewDirection.asPoint3D());		
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
