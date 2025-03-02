package net.p3pp3rf1y.sophisticatedstorage.client.util;

import com.mojang.math.Transformation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;
import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.ArrayList;
import java.util.List;

public class QuadTransformers {
	private static final MutableQuadViewImpl editorQuad = new MutableQuadViewImpl() {
		{
			this.data = new int[EncodingFormat.TOTAL_STRIDE];
			this.clear();
		}

		public void emitDirectly() {
			// noop
		}
	};

	public static List<BakedQuad> process(RenderContext.QuadTransform transform, List<BakedQuad> quads) {
		List<BakedQuad> transformedQuads = new ArrayList<>();

		for (int i = 0; i < quads.size(); i++) {
			BakedQuad quad = quads.get(i);
			MutableQuadView mqv = editorQuad.fromVanilla(quad, null, null);
			transform.transform(mqv);

			BakedQuad transformedQuad = mqv.toBakedQuad(quad.getSprite());
			transformedQuads.add(transformedQuad);
			editorQuad.clear();
		}

		return transformedQuads;
	}

    public static RenderContext.QuadTransform applying(Transformation transform) {
        if (transform.equals(Transformation.identity()))
            return quad -> true;

        Matrix4f matrix = transform.getMatrix();
        Matrix3f normalMatrix = new Matrix3f(matrix).invert().transpose();

        return quad -> {
            for (int i = 0; i < 4; i++) {
                Vector4f pos = new Vector4f(quad.x(i), quad.y(i), quad.z(i), 1).mul(transform.getMatrix());
                pos.div(pos.w);
                quad.pos(i, pos.x(), pos.y(), pos.z());
            }

            for (int i = 0; i < 4; i++) {
                if (quad.hasNormal(i)) {
                    quad.normal(i,
                            new Vector3f(quad.normalX(i), quad.normalY(i), quad.normalZ(i))
                            .mul(normalMatrix)
                            .normalize()
                    );
                }
            }
            return true;
        };
    }
}
