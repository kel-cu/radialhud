package anticope.radialhud;

public class Vector2 {
    float x, y;

    Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    void normalize() {
        float mag = getMag();
        if (mag != 0 && mag != 1)
            divide(mag);
    }

    void subtract(Vector2 vec) {
        this.x -= vec.x;
        this.y -= vec.y;
    }

    void divide(float n) {
        x /= n;
        y /= n;
    }

    void multiply(float n) {
        x *= n;
        y *= n;
    }

    private float getMag() {
        return (float) Math.sqrt(x * x + y * y);
    }
}