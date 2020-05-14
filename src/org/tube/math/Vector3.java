package org.tube.math;

/**
 * Represents vector in 3d space.
 * Also could be used to represent 3d point.
 *
 * @author Igor Khotin
 */
public class Vector3 {
	public double x, y, z;
    
    
    // static operations
    public static Vector3 add(Vector3 a, Vector3 b) {
        return new Vector3(a.x + b.x, a.y + b.y, a.z + b.z);
    }    
    public static Vector3 sub(Vector3 a, Vector3 b) {
        return new Vector3(a.x - b.x, a.y - b.y, a.z - b.z);
    }    
    public static Vector3 scale(Vector3 a, double scale) {
        return new Vector3(a.x * scale, a.y * scale, a.z * scale);
    }
    public static Vector3 cross(Vector3 a,Vector3 b) {
        return new Vector3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
    }
    public static double dot(Vector3 a, Vector3 b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }
	
	
    /**
     * The default constructor.
     * Creates the zero vector.
     */
	public Vector3() {
		this.x = this.y = this.z = 0;
	}
    
    /**
     * Creates a vector based on 3 scalar values x, y, z
     */
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
    
    /**
     * Creates a vector based on values from incoming vector.
     * Use it to create an identical copy of existing vector.
     */
	public Vector3(Vector3 vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}
    
    /**
     * Assigns values for all components
     */
	public void set(double x, double y, double z) {
		this.x = x; this.y = y; this.z = z;
	}
    
    /**
     * Assign values for all components based on incoming vector.
     * In other words, turns this vector into a copy of incoming vector.
     */
	public void set(Vector3 vec) {
		this.x = vec.x; this.y = vec.y; this.z = vec.z;
	}

	public double getLength() {
		double length = x*x + y*y + z*z;
		length = Math.sqrt(length);
		return length;
	}

	public boolean normalize() {
		double length = this.getLength();	
		if (length == 0) return false;
		x = x / length;
		y = y / length;
		z = z / length;
		return true;
	}
	
    public double mag2() { return x*x + y*y + z*z; }    
    public double mag() { return (float)Math.sqrt(mag2()); }

	public void add(Vector3 vec) {
		this.x += vec.x;
		this.y += vec.y;
		this.z += vec.z;
	}

	public void add(double x, double y, double z)
	{
		this.x += x;
		this.y += y;
		this.z += z;
	}

	public void sub(Vector3 vec) {
		this.x -= vec.x;
		this.y -= vec.y;
		this.z -= vec.z;
	}

	public void negative() {
		x = -x;
		y = -y;
		z = -z;
	}

	public void mul(double val) {
		this.x *= val;
		this.y *= val;
		this.z *= val;
	}

	public void mul(Vector3 vec) {
		double x = this.x;
		double y = this.y;
		double z = this.z;
		
		this.x = y * vec.z - z * vec.y;
		this.y = z * vec.x - x * vec.z;
		this.z = x * vec.y - y * vec.x;
	}

	public double dot(Vector3 vec) {
		return this.x * vec.x + this.y * vec.y + this.z * vec.z;
	}
    
    /**
     * Returns reflected agains normal vector
     *.
     * @returns V - 2(V * N) * N
     */
    public Vector3 getReflected(Vector3 normal) {
        double d = 2.0d * Vector3.dot(this, normal);
        return new Vector3(
                this.x - d * normal.x,
                this.y - d * normal.y,
                this.z - d * normal.z
            );
    }
    
    /**
     * Returns opposite vector
     */
    public Vector3 getOpposite() {
        Vector3 opposite = new Vector3(this);
        opposite.negative();
        return opposite;
    }

    /*
	public void mul(Matrix43 mat) {
		double x = this.x;
		double y = this.y;
		double z = this.z;
		this.x = x * mat.vals[0][0] + y * mat.vals[1][0] + z * mat.vals[2][0] + mat.vals[3][0];
		this.y = x * mat.vals[0][1] + y * mat.vals[1][1] + z * mat.vals[2][1] + mat.vals[3][1];
		this.z = x * mat.vals[0][2] + y * mat.vals[1][2] + z * mat.vals[2][2] + mat.vals[3][2];
	}
    */
    
	public void rotateOX(int alpha) {
		double nx, ny, nz;
		alpha %= (360*60);
		nx = x;
		ny = y * TMath.cos(alpha) - z * TMath.sin(alpha);
		nz = y * TMath.sin(alpha) + z * TMath.cos(alpha);	
		x = nx;
		y = ny;
		z = nz;
	}

	public void rotateOY(int beta) {
		double nx, ny, nz;
		beta %= (360*60);
		nx = x * TMath.cos(beta) + z * TMath.sin(beta);
		ny = y;
		nz = z * TMath.cos(beta) - x * TMath.sin(beta);
		x = nx;
		y = ny;
		z = nz;
	}

	public void rotateOZ(int gamma) {
		double nx, ny, nz;
		gamma %= (360*60);
		nx = x * TMath.cos(gamma) - y * TMath.sin(gamma);
		ny = x * TMath.sin(gamma) + y * TMath.cos(gamma);
		nz = z;
		x = nx;
		y = ny;
		z = nz;
	}

	public double getCosFi(Vector3 vec) {
		//get the cos of angle between vectors
		double cosFi;
		cosFi = (this.x * vec.x + this.y * vec.y + this.z * vec.z) 
				/ (this.getLength() * vec.getLength());
		return cosFi;
	}

	public double getCosFiN(Vector3 vec) {
		//get the cos of angle between vectors (for normal vectors)
		double cosFi;
		cosFi = (this.x * vec.x + this.y * vec.y + this.z * vec.z);
		return cosFi;
	}

	public int getAlpha() {
		int alpha;

		if (y == 0 && z == 0) return -1;
		else {
			double alpha_r = Math.atan2(y, z);
			if (alpha_r < 0) alpha_r += (Math.PI * 2);
			alpha = (int)(alpha_r / TMath.dFactor);
		}
		return alpha;
	}

	public int getBeta() {
		int beta;

		if (x == 0 && z == 0) return -1;
		else {
			double beta_r = Math.atan2(x, z);
			if (beta_r < 0) beta_r += (Math.PI * 2);
			beta = (int)(beta_r / TMath.dFactor);			
		}
		return beta;
	}
    
    // debug
    public void print() {
        System.out.println("[" + x + ":" + y + ":" + z + "]");
    }
}
