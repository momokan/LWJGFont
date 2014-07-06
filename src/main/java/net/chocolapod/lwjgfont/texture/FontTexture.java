/**
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2014 momokan (http://lwjgfont.chocolapod.net)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.chocolapod.lwjgfont.texture;
 
import static net.chocolapod.lwjgfont.texture.FontAlphaBlend.AlphaBlend;
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
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glVertex3f;

public class FontTexture {
	private int	 target;
	private int	 textureID;
 
	private int	 width;
	private int	 height;
 
	private int	 textureWidth;
	private int	 textureHeight;

	private float			red;
	private float			green;
	private float			blue;
	private FontAlphaBlend		alphaBlend;
	private float			alpha;
	private boolean 		isAlphaPremultiplied;
 
	public FontTexture(int target, int textureID) {
		this.target = target;
		this.textureID = textureID;
		this.alpha = 1f;
		this.alphaBlend = AlphaBlend;
		this.isAlphaPremultiplied = true;
		this.red = 1f;
		this.green = 1f;
		this.blue = 1f;
	}

	public void draw(float dstX1, float dstY1) {
		draw(dstX1, dstY1, dstX1 + width, dstY1 - height, 0, 0, 0, width, height);
	}

	public void draw(float dstX1, float dstY1, float dstX2, float dstY2, float dstZ, float srcX1, float srcY1, float srcX2, float srcY2) {
		float	halfWidth = (dstX2 - dstX1) / 2;
		float	halfHeight = (dstY1 - dstY2) / 2;

		// store the current model matrix
		glPushMatrix();

		// bind to the appropriate texture for this sprite
		bind();

		// translate to the right location and prepare to draw
		glTranslatef(dstX1 + halfWidth, dstY1 - halfHeight, dstZ);
//		glTranslatef(dstX, dstY, 0);
		
		glEnable(GL_BLEND);
		
		alphaBlend.config();
		
		//	透過率を設定する
		if (isAlphaPremultiplied) {
			//	透過イメージを表示する (pre-multipled)
			//	Premultiplied な画像である PNG を半透明表示する場合、 RGB のそれぞれについて alpha 値をかける
			glColor4f(red * alpha, green * alpha, blue * alpha, alpha);
		} else {
			//	透過イメージを表示する (not pre-multipled)
			glColor4f(red, green, blue, alpha);
		}

		// draw a quad textured to match the sprite
		glBegin(GL_QUADS);
		{
			float	tx1 = srcX1 / textureWidth;
			float	tx2 = srcX2 / textureWidth;
			float	ty1 = srcY1 / textureHeight;
			float	ty2 = srcY2 / textureHeight;

			glTexCoord2f(tx1, ty1);
			glVertex2f(halfWidth * -1, halfHeight);

			glTexCoord2f(tx1, ty2);
			glVertex2f(halfWidth * -1, halfHeight * -1);

			glTexCoord2f(tx2, ty2);
			glVertex2f(halfWidth, halfHeight * -1);

			glTexCoord2f(tx2, ty1);
			glVertex2f(halfWidth, halfHeight);
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

	public void setAlphaBlend(FontAlphaBlend alphaBlend) {
		this.alphaBlend = alphaBlend;
	}
	
	public void setColor(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

}
