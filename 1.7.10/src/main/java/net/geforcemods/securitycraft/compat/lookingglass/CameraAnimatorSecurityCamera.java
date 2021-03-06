package net.geforcemods.securitycraft.compat.lookingglass;

import com.xcompwiz.lookingglass.api.animator.ICameraAnimator;
import com.xcompwiz.lookingglass.api.view.IViewCamera;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChunkCoordinates;

/**
 * The IWorldView animator for the security cameras. <p>
 *
 * Sets the location of the camera, and rotates the view.
 *
 * @author Geforce
 */
public class CameraAnimatorSecurityCamera implements ICameraAnimator {

	private final double cameraYOffset = 2.425D;

	private IViewCamera camera;
	private int cameraMeta = 0;
	private int xCoord, yCoord, zCoord;

	public CameraAnimatorSecurityCamera(IViewCamera camera, int xCoord, int yCoord, int zCoord, int securityCameraMeta){
		this.camera = camera;
		cameraMeta = securityCameraMeta;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.zCoord = zCoord;

		this.camera.setLocation(camera.getX() + 0.5D, camera.getY() - cameraYOffset, camera.getZ() + 0.5D);

		if(securityCameraMeta == 1)
			this.camera.setYaw(180F);
		else if(securityCameraMeta == 2)
			this.camera.setYaw(90F);
		else if(securityCameraMeta == 3)
			this.camera.setYaw(0F);
		else if(securityCameraMeta == 4)
			this.camera.setYaw(270F);
		else if(cameraMeta == 0)
			camera.setPitch(90F);
	}


	@Override
	public void setTarget(ChunkCoordinates target){}

	@Override
	public void refresh(){}

	@Override
	public void update(long arg0) {
		if(camera == null)
			return;
		if(Minecraft.getMinecraft().theWorld.getBlock(xCoord, yCoord, zCoord) != SCContent.securityCamera)
			return;

		float cameraRotation = ((TileEntitySecurityCamera) Minecraft.getMinecraft().theWorld.getTileEntity(xCoord, yCoord, zCoord)).cameraRotation * 60;

		if(cameraMeta == 4)
			camera.setYaw(180 + cameraRotation);
		else if(cameraMeta == 2)
			camera.setYaw(90 + cameraRotation);
		else if(cameraMeta == 3)
			camera.setYaw(0 + cameraRotation);
		else if(cameraMeta == 1)
			camera.setYaw(270 + cameraRotation);
		else if(cameraMeta == 0)
			camera.setYaw(cameraRotation);
	}
}
