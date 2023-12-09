package textualmold9830.plugins;

import java.awt.image.BufferedImage;
import java.util.Objects;

public class Texture {
    public final int id;
    public BufferedImage texture;

    public Texture(int id, BufferedImage texture) {
        this.id = id;
        this.texture = texture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Texture texture1 = (Texture) o;

        if (id != texture1.id) return false;
        return Objects.equals(texture, texture1.texture);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (texture != null ? texture.hashCode() : 0);
        return result;
    }
}
