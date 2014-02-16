package net.chocolapod.lwjgfont.texture;
 
import static net.chocolapod.lwjgfont.texture.AlphaBlend.AlphaBlend;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glVertex3f;

public class Texture {
	private int	 target;
	private int	 textureID;
 
	private int	 width;
	private int	 height;
 
	private int	 textureWidth;
	private int	 textureHeight;

	private float			r;
	private float			g;
	private float			b;
	private AlphaBlend		alphaBlend;
	private float			alpha;
	private boolean 		isAlphaPremultiplied;
 
	public Texture(int target, int textureID) {
		this.target = target;
		this.textureID = textureID;
		this.alpha = 1f;
		this.alphaBlend = AlphaBlend;
		this.isAlphaPremultiplied = true;
		this.r = 1f;
		this.g = 1f;
		this.b = 1f;
	}

	public void draw(float dstX1, float dstY1) {
		draw(dstX1, dstY1, dstX1 + width, dstY1 - height, 0, 0, width, height);
	}

	public void draw(float dstX1, float dstY1, float dstX2, float dstY2, float srcX1, float srcY1, float srcX2, float srcY2) {
		// store the current model matrix
		glPushMatrix();

		// bind to the appropriate texture for this sprite
		bind();

		// translate to the right location and prepare to draw
		glTranslatef(0, 0, 0);
//		glTranslatef(dstX, dstY, 0);
		
		glEnable(GL_BLEND);
		
		alphaBlend.config(this);
		
		//	透過率を設定する
		if (isAlphaPremultiplied) {
			//	透過イメージを表示する (pre-multipled)
			//	Premultiplied な画像である PNG を半透明表示する場合、 RGB のそれぞれについて alpha 値をかける
			glColor4f(r * alpha, g * alpha, b * alpha, alpha);
		} else {
			//	透過イメージを表示する (not pre-multipled)
			glColor4f(r, g, b, alpha);
		}

		// draw a quad textured to match the sprite
		glBegin(GL_QUADS);
		{
			float	tx1 = 1.0f * srcX1 / textureWidth;
			float	tx2 = 1.0f * srcX2 / textureWidth;
			float	ty1 = 1.0f * srcY1 / textureHeight;
			float	ty2 = 1.0f * srcY2 / textureHeight;

			glTexCoord2f(tx1, ty1);
			glVertex2f(dstX1, dstY1);

			glTexCoord2f(tx1, ty2);
			glVertex2f(dstX1, dstY2);

			glTexCoord2f(tx2, ty2);
			glVertex2f(dstX2, dstY2);

			glTexCoord2f(tx2, ty1);
			glVertex2f(dstX2, dstY1);
		}
		glEnd();

		// restore the model view matrix to prevent contamination
		glPopMatrix();
	}

	public void point(int srcX, int srcY) {
		float   tx = 1.0f * srcX / textureWidth;
		float   ty = 1.0f * srcY / textureHeight;
 
		glTexCoord2f(tx, ty);
	}

	void setTextureHeight(int texHeight) {
		this.textureHeight = texHeight;
	}

	void setTextureWidth(int texWidth) {
		this.textureWidth = texWidth;
	}
 
	int getTextureWidth() {	
		return textureWidth;
	}

	int getTextureHeight() {
		return textureHeight;
	}

	public int getWidth() {
		return width;
	}

	void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	void setHeight(int height) {
		this.height = height;
	}

	public boolean isAlphaPremultiplied() {
		return isAlphaPremultiplied;
	}

	void setAlphaPremultiplied(boolean isAlphaPremultiplied) {
		this.isAlphaPremultiplied = isAlphaPremultiplied;
	}

	public void dispose() {
		if (0 < textureID) {
			glDeleteTextures(textureID);
			textureID = -1;
		}
	}

	public void bind() {
		glBindTexture(target, textureID);
	}

	public void setAlphaBlend(AlphaBlend alphaBlend) {
		this.alphaBlend = alphaBlend;
	}
	
	public void setColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

}
