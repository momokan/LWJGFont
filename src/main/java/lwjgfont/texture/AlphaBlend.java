package lwjgfont.texture;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11.glBlendFunc;
 
public enum AlphaBlend {
	AlphaBlend {
		@Override
		public void config(Texture texture) {
			//  アルファ合成
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		}
	},
	Add {
		@Override
		public void config(Texture texture) {
			//  加算合成
			glBlendFunc(GL_SRC_ALPHA, GL_ONE);
		}
	},
	Multiple {
		@Override
		public void config(Texture texture) {
			//  乗算合成
			glBlendFunc(GL_ZERO, GL_SRC_COLOR);
		}
	},
	Screen {
		@Override
		public void config(Texture texture) {
			//  スクリーン合成
			glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE);
		}
	},
	Reverse {
		@Override
		public void config(Texture texture) {
			//  反転合成
			glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ZERO);
		}
	}
	;
	 
	public abstract void config(Texture texture);
}
